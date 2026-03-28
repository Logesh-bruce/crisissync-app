package com.crisissync.service;

import com.crisissync.dto.AuthResponse;
import com.crisissync.dto.LoginRequest;
import com.crisissync.dto.RegisterRequest;
import com.crisissync.exception.BadRequestException;
import com.crisissync.model.Hotel;
import com.crisissync.model.Role;
import com.crisissync.model.User;
import com.crisissync.repository.HotelRepository;
import com.crisissync.repository.UserRepository;
import com.crisissync.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // ── Register ──────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        // 1. Check duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        // 2. Parse role
        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + request.getRole()
                    + ". Must be one of: GUEST, STAFF, MANAGER, ADMIN");
        }

        // 3. Resolve hotel (optional)
        Hotel hotel = null;
        if (request.getHotelId() != null) {
            hotel = hotelRepository.findById(request.getHotelId())
                    .orElseThrow(() -> new BadRequestException(
                            "Hotel not found with id: " + request.getHotelId()));
        }

        // 4. Build & save user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .hotel(hotel)
                .build();

        user = userRepository.save(user);

        // 5. Generate JWT
        String token = jwtUtil.generateToken(user);

        return buildAuthResponse(user, token);
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Override
    public AuthResponse login(LoginRequest request) {

        // 1. Authenticate via Spring Security's AuthenticationManager
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid email or password");
        }

        // 2. Load user (authentication passed so user must exist)
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        // 3. Generate JWT
        String token = jwtUtil.generateToken(user);

        return buildAuthResponse(user, token);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .hotelId(user.getHotel() != null ? user.getHotel().getId() : null)
                .token(token)
                .build();
    }
}
