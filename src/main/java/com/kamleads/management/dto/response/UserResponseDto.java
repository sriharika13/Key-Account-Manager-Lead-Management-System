package com.kamleads.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Integer getTotalLeads() {
        return totalLeads;
    }

    public void setTotalLeads(Integer totalLeads) {
        this.totalLeads = totalLeads;
    }

    public Integer getActiveLeads() {
        return activeLeads;
    }

    public void setActiveLeads(Integer activeLeads) {
        this.activeLeads = activeLeads;
    }

    private UUID id;
    private String name;
    private String email;
    private String timezone;
    private Integer totalLeads;
    private Integer activeLeads;
}