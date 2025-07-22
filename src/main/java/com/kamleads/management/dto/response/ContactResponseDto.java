package com.kamleads.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponseDto {
    private UUID id;
    private String name;
    private String role;
    private String email;
    private Boolean isPrimary;
    private String leadName;
    private UUID leadId;
    private Integer totalInteractions;
}