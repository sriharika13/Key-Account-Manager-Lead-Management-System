package com.kamleads.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class JwtResponseDto {
    private String token;
    private String type = "Bearer";
    private UUID id;
    private String username; // This will be the email
    private String email;
    private List<String> roles;
}