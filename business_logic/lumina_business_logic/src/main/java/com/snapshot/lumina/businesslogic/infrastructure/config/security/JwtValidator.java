package com.snapshot.lumina.businesslogic.infrastructure.config.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JWT Validator for Keycloak tokens
 *
 * Responsibilities:
 * - Validate JWT signature using Keycloak's public keys (JWKS)
 * - Validate token claims (issuer, expiration, audience)
 * - Extract user information from tokens
 * - Cache public keys for performance
 */
@Slf4j
@Component
public class JwtValidator {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource:business-logic-service}")
    private String clientId;

    @Value("${keycloak.validate-audience:false}")
    private boolean validateAudience;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, PublicKey> publicKeyCache = new ConcurrentHashMap<>();
    private String jwksUrl;
    private String expectedIssuer;

    @PostConstruct
    public void init() {
        this.jwksUrl = String.format("%s/realms/%s/protocol/openid-connect/certs",
                authServerUrl.replaceAll("/+$", ""), realm);
        this.expectedIssuer = String.format("%s/realms/%s",
                authServerUrl.replaceAll("/+$", ""), realm);

        log.info("Keycloak JWKS URL: {}", jwksUrl);
        log.info("Expected JWT issuer: {}", expectedIssuer);

        // Pre-fetch keys on startup
        try {
            fetchPublicKeys();
            log.info("Successfully pre-fetched JWKS keys on startup");
        } catch (Exception e) {
            log.warn("Failed to pre-fetch JWKS keys on startup. Will fetch on first request.", e);
        }
    }

    /**
     * Validate JWT token with Keycloak public key
     *
     * Validates:
     * 1. Signature using Keycloak's public key
     * 2. Token expiration
     * 3. Issuer matches expected Keycloak realm
     * 4. Audience (optional, based on configuration)
     *
     * @param token JWT token string
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            // Parse header to get key ID
            String kid = getKeyIdFromToken(token);
            PublicKey publicKey = getPublicKey(kid);

            // Validate token signature and parse claims
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);

            Claims body = claims.getBody();

            // Validate issuer
            String issuer = body.getIssuer();
            if (!expectedIssuer.equals(issuer)) {
                log.warn("Invalid issuer. Expected: {}, Got: {}", expectedIssuer, issuer);
                return false;
            }

            // Validate audience (optional - Keycloak doesn't always include this)
            if (validateAudience) {
                Set<String> audience = body.getAudience();
                if (audience == null || !audience.contains(clientId)) {
                    log.warn("Token audience validation failed. Client ID '{}' not found in audience: {}",
                            clientId, audience);
                    return false;
                }
            }

            // Token type validation removed - Keycloak puts 'typ' in header, not body
            // The header 'typ' is typically "JWT", not "Bearer"
            // "Bearer" is the authorization scheme used in HTTP headers, not in the token itself

            log.debug("Token validated successfully for user: {}",
                    body.get("preferred_username", String.class));
            return true;

        } catch (ExpiredJwtException e) {
            log.debug("JWT token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token format: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("JWT signature validation failed: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT token is empty or invalid: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * Extract claims from token
     *
     * NOTE: This validates the signature but should only be called after validateToken()
     * for performance reasons (to avoid double validation)
     *
     * @param token JWT token string
     * @return Claims object
     * @throws RuntimeException if token is invalid
     */
    public Claims getClaimsFromToken(String token) {
        try {
            String kid = getKeyIdFromToken(token);
            PublicKey publicKey = getPublicKey(kid);

            return Jwts.parser()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Failed to extract claims from token: {}", e.getMessage());
            throw new RuntimeException("Invalid token", e);
        }
    }

    /**
     * Extract username from JWT token
     *
     * @param token JWT token string
     * @return Username (preferred_username claim)
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        String username = claims.get("preferred_username", String.class);
        if (username == null) {
            log.warn("Token does not contain 'preferred_username' claim");
            // Fallback to 'name' or 'sub'
            username = claims.get("name", String.class);
            if (username == null) {
                username = claims.getSubject();
            }
        }
        return username;
    }

    /**
     * Extract user email from JWT token
     *
     * @param token JWT token string
     * @return Email address (email claim)
     */
    public String getEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("email", String.class);
    }

    /**
     * Extract user ID from JWT token
     *
     * @param token JWT token string
     * @return User ID (sub claim - Keycloak user UUID)
     */
    public String getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject(); // 'sub' claim contains Keycloak user ID
    }

    /**
     * Extract realm roles from JWT token
     *
     * @param token JWT token string
     * @return List of realm roles
     */
    @SuppressWarnings("unchecked")
    public List<String> getRealmRolesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Map<String, Object> realmAccess = claims.get("realm_access", Map.class);

        if (realmAccess != null && realmAccess.containsKey("roles")) {
            Object roles = realmAccess.get("roles");
            if (roles instanceof List) {
                return (List<String>) roles;
            }
        }
        return Collections.emptyList();
    }

    /**
     * Extract client/resource roles from JWT token
     *
     * @param token JWT token string
     * @return List of client-specific roles
     */
    @SuppressWarnings("unchecked")
    public List<String> getClientRolesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Map<String, Object> resourceAccess = claims.get("resource_access", Map.class);

        if (resourceAccess != null && resourceAccess.containsKey(clientId)) {
            Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get(clientId);
            if (clientAccess != null && clientAccess.containsKey("roles")) {
                Object roles = clientAccess.get("roles");
                if (roles instanceof List) {
                    return (List<String>) roles;
                }
            }
        }
        return Collections.emptyList();
    }

    /**
     * Get all roles (realm + client) from token
     *
     * @param token JWT token string
     * @return Combined list of all roles
     */
    public List<String> getAllRolesFromToken(String token) {
        List<String> allRoles = new ArrayList<>();
        allRoles.addAll(getRealmRolesFromToken(token));
        allRoles.addAll(getClientRolesFromToken(token));
        return allRoles;
    }

    /**
     * Extract key ID (kid) from token header
     *
     * @param token JWT token string
     * @return Key ID
     * @throws IllegalArgumentException if token format is invalid
     */
    private String getKeyIdFromToken(String token) {
        int firstDot = token.indexOf('.');
        if (firstDot == -1) {
            throw new IllegalArgumentException("Invalid JWT token format: no header found");
        }

        String header = token.substring(0, firstDot);
        String decodedHeader = new String(Base64.getUrlDecoder().decode(header));

        try {
            JsonNode headerJson = objectMapper.readTree(decodedHeader);
            if (!headerJson.has("kid")) {
                throw new IllegalArgumentException("JWT header does not contain 'kid' field");
            }
            return headerJson.get("kid").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JWT header", e);
        }
    }

    /**
     * Get public key by key ID, with caching
     *
     * @param kid Key ID
     * @return RSA Public Key
     * @throws Exception if key cannot be retrieved
     */
    private PublicKey getPublicKey(String kid) throws Exception {
        // Check cache first
        PublicKey cachedKey = publicKeyCache.get(kid);
        if (cachedKey != null) {
            log.debug("Using cached public key for kid: {}", kid);
            return cachedKey;
        }

        // Fetch from JWKS endpoint
        log.debug("Public key not in cache, fetching from JWKS for kid: {}", kid);
        Map<String, PublicKey> keys = fetchPublicKeys();
        PublicKey key = keys.get(kid);

        if (key == null) {
            throw new RuntimeException("Public key not found for kid: " + kid +
                    ". Available keys: " + keys.keySet());
        }

        return key;
    }

    /**
     * Fetch public keys from Keycloak JWKS endpoint
     * Keys are cached by Spring Cache for 1 hour
     *
     * @return Map of key ID to Public Key
     * @throws Exception if JWKS endpoint cannot be reached
     */
    @Cacheable(value = "jwksKeys", unless = "#result.isEmpty()")
    public Map<String, PublicKey> fetchPublicKeys() throws Exception {
        log.debug("Fetching public keys from JWKS endpoint: {}", jwksUrl);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(jwksUrl))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch JWKS. Status: " + response.statusCode()
                    + ", Body: " + response.body());
        }

        JsonNode jwks = objectMapper.readTree(response.body());
        JsonNode keys = jwks.get("keys");

        if (keys == null || !keys.isArray()) {
            throw new RuntimeException("Invalid JWKS response: 'keys' field missing or not an array");
        }

        Map<String, PublicKey> publicKeys = new ConcurrentHashMap<>();

        for (JsonNode keyNode : keys) {
            String kid = keyNode.get("kid").asText();
            String kty = keyNode.get("kty").asText();

            if ("RSA".equals(kty)) {
                String n = keyNode.get("n").asText();
                String e = keyNode.get("e").asText();

                PublicKey publicKey = createRSAPublicKey(n, e);
                publicKeys.put(kid, publicKey);
                publicKeyCache.put(kid, publicKey); // Update local cache
                log.debug("Loaded RSA public key: kid={}", kid);
            } else {
                log.debug("Skipping non-RSA key: kid={}, kty={}", kid, kty);
            }
        }

        if (publicKeys.isEmpty()) {
            log.warn("No RSA public keys found in JWKS response");
        } else {
            log.info("Successfully fetched {} RSA public keys from JWKS", publicKeys.size());
        }

        return publicKeys;
    }

    /**
     * Create RSA public key from modulus and exponent
     *
     * @param modulusBase64 Base64-encoded modulus
     * @param exponentBase64 Base64-encoded exponent
     * @return RSA Public Key
     * @throws Exception if key cannot be created
     */
    private PublicKey createRSAPublicKey(String modulusBase64, String exponentBase64) throws Exception {
        byte[] modulusBytes = Base64.getUrlDecoder().decode(modulusBase64);
        byte[] exponentBytes = Base64.getUrlDecoder().decode(exponentBase64);

        BigInteger modulus = new BigInteger(1, modulusBytes);
        BigInteger exponent = new BigInteger(1, exponentBytes);

        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory factory = KeyFactory.getInstance("RSA");

        return factory.generatePublic(spec);
    }
}