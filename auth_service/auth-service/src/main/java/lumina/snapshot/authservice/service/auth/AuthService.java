package lumina.snapshot.authservice.service.auth;

import lumina.snapshot.authservice.dto.LoginRequest;
import lumina.snapshot.authservice.dto.RegisterRequest;
import lumina.snapshot.authservice.dto.TokenResponse;
import lumina.snapshot.authservice.dto.UserInfoResponse;

public interface AuthService {
    public TokenResponse login(LoginRequest request);
    public void register(RegisterRequest request);
    public void logout(String refreshToken);
    public UserInfoResponse getUserInfo(String accessToken);
}
