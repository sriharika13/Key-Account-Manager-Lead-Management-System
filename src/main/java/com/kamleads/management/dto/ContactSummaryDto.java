package com.kamleads.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactSummaryDto {
    private UUID id;
    private String name;
    private String role;
    private String email;
    private Boolean isPrimary;
}