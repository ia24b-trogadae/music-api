package ch.example.musicapi.services;

import ch.example.musicapi.dao.AppUserDao;
import ch.example.musicapi.model.AppUser;
import ch.example.musicapi.model.LoginRequest;
import ch.example.musicapi.model.LoginResponse;
import ch.example.musicapi.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserDao appUserDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            AppUserDao appUserDao, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.appUserDao = appUserDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        AppUser user =
                appUserDao
                        .findByUsername(request.getUsername())
                        .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new BadCredentialsException("User is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRoleName());

        return new LoginResponse(token, "Bearer", user.getUsername(), user.getRoleName());
    }
}
