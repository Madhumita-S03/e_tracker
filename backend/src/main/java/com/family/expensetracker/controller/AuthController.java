package com.family.expensetracker.controller;

import com.family.expensetracker.dto.LoginRequest;
import com.family.expensetracker.dto.LoginResponse;
import com.family.expensetracker.entity.User;
import com.family.expensetracker.repository.UserRepository;
import com.family.expensetracker.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login endpoints for family members")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/login")
    @Operation(summary = "Login with username and password", description = "Returns a JWT bearer token on success")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();

        String token = jwtUtil.generateToken(userDetails, user.getId());

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .avatarColor(user.getAvatarColor())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/members")
    @Operation(summary = "List family members", description = "Public list of family member accounts for the home page cards (no sensitive data)")
    public ResponseEntity<List<Map<String, Object>>> listMembers() {
        List<Map<String, Object>> members = userRepository.findAll().stream()
                .map(u -> Map.<String, Object>of(
                        "id", u.getId(),
                        "username", u.getUsername(),
                        "fullName", u.getFullName(),
                        "avatarColor", u.getAvatarColor() == null ? "#6B7280" : u.getAvatarColor()
                ))
                .toList();
        return ResponseEntity.ok(members);
    }
}
