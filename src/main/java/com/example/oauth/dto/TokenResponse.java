package com.example.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponse {
    private String username;
    private boolean authenticated;
    private String message;
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
}
