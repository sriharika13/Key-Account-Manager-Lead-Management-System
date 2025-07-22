package com.kamleads.management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class ContactCreateRequestDto {
    @NotNull(message = "Lead ID is required")
    private UUID leadId;

    @NotBlank(message = "Contact name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 50, message = "Role must not exceed 50 characters")
    private String role;

    @Email(message = "Email should be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    private Boolean isPrimary = false;
}

