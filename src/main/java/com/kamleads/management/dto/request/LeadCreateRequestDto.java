package com.kamleads.management.dto.request;

import com.kamleads.management.enums.LeadStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;


public class LeadCreateRequestDto {
    @NotBlank(message = "Restaurant name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 50, message = "Cuisine type must not exceed 50 characters")
    private String cuisineType;

    @NotNull(message = "KAM ID is required")
    private UUID kamId;

    @NotNull(message = "Call frequency is required")
    @Min(value = 1, message = "Call frequency must be at least 1 day")
    private Integer callFrequency;

    public LeadStatus getStatus() {
        return status;
    }

    public void setStatus(LeadStatus status) {
        this.status = status;
    }

    public Integer getCallFrequency() {
        return callFrequency;
    }

    public void setCallFrequency(Integer callFrequency) {
        this.callFrequency = callFrequency;
    }

    public UUID getKamId() {
        return kamId;
    }

    public void setKamId(UUID kamId) {
        this.kamId = kamId;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private LeadStatus status = LeadStatus.NEW;


}