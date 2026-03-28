package com.crisissync.service;

import com.crisissync.dto.AuthResponse;
import com.crisissync.dto.LoginRequest;
import com.crisissync.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
