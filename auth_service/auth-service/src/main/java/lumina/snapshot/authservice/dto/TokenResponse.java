package lumina.snapshot.authservice.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TokenResponse {
    String accessToken;
    String refreshToken;
    String tokenType;
    Integer expiresIn;
    Integer refreshExpiresIn;
}
