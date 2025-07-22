package com.kamleads.management.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kamleads.management.enums.LeadStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "leads",
        indexes = {
                @Index(name = "idx_leads_kam_status", columnList = "kam_id, status"),
                @Index(name = "idx_leads_call_schedule", columnList = "last_call_date, call_frequency"),
                @Index(name = "idx_leads_performance", columnList = "performance_score DESC"),
                @Index(name = "idx_leads_city", columnList = "city"),
                @Index(name = "idx_leads_created", columnList = "created_at DESC")
        })
public class Lead { //Lead Entity represents Restaurant accounts
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @NotBlank(message = "Restaurant name is required")
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "cuisine_type", length = 50)
    private String cuisineType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LeadStatus status= LeadStatus.NEW; // Default status

    // Many-to-One: Many leads belong to one KAM
    @ManyToOne(fetch = FetchType.LAZY)  // LAZY loading for performance
    @JoinColumn(name = "kam_id", nullable = false)  // Foreign key column name
    @JsonBackReference("user-leads")  // Prevents circular serialization
    private User kam;

    @Column(name = "call_frequency", nullable = false)
    @Min(value = 1, message = "Call frequency must be at least 1 day")
    private Integer callFrequency;  // Days between calls

    @Column(name = "last_call_date")
    private LocalDate lastCallDate;

    // Calculated field using database formula
    @Formula("last_call_date + (call_frequency || ' days')::INTERVAL")  // PostgreSQL syntax
    private LocalDate nextCallDate;  // Hibernate calculates this from DB

    @Column(name = "performance_score", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Performance score cannot be negative")
    @DecimalMax(value = "100.0", message = "Performance score cannot exceed 100")
    private BigDecimal performanceScore = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One-to-Many: One lead has many contacts
    @OneToMany(
            mappedBy = "lead",
            cascade = CascadeType.ALL,  // CASCADE all operations to contacts
            fetch = FetchType.LAZY,
            orphanRemoval = true  // Delete contacts if removed from lead
    )
    @JsonManagedReference("lead-contacts")
    @OrderBy("name ASC")  // Primary contacts first, then alphabetical
    private List<Contact> contacts = new ArrayList<>();

    // One-to-Many: One lead has many interactions
    @OneToMany(
            mappedBy = "lead",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}, //why not delete
            fetch = FetchType.LAZY
    )
    @JsonIgnore  // Don't include all interactions in API responses
    @OrderBy("interactionDate DESC")  // Most recent first
    private List<Interaction> interactions = new ArrayList<>();

    @OneToMany(
            mappedBy = "lead",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CallSchedule> scheduledCalls;

    @OneToMany(mappedBy = "lead", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<PerformanceMetrics> performanceMetrics;

    // Constructors
    public Lead() {}

    public Lead(String name, String city, User kam, Integer callFrequency) {
        this.name = name;
        this.city = city;
        this.kam = kam;
        this.callFrequency = callFrequency;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCuisineType() { return cuisineType; }
    public void setCuisineType(String cuisineType) { this.cuisineType = cuisineType; }

    public LeadStatus getStatus() { return status; }
    public void setStatus(LeadStatus status) { this.status = status; }

    public User getKam() { return kam; }
    public void setKam(User kam) { this.kam = kam; }

    public Integer getCallFrequency() { return callFrequency; }
    public void setCallFrequency(Integer callFrequency) { this.callFrequency = callFrequency; }

    public LocalDate getLastCallDate() { return lastCallDate; }
    public void setLastCallDate(LocalDate lastCallDate) { this.lastCallDate = lastCallDate; }

    public LocalDate getNextCallDate() { return nextCallDate; }
    // No setter for nextCallDate - it's calculated by database

    public BigDecimal getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(BigDecimal performanceScore) { this.performanceScore = performanceScore; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public List<Contact> getContacts() { return contacts; }
    public void setContacts(List<Contact> contacts) { this.contacts = contacts; }

    public List<Interaction> getInteractions() { return interactions; }
    public void setInteractions(List<Interaction> interactions) { this.interactions = interactions; }
    // Helper methods
    public void addContact(Contact contact) {
        contacts.add(contact);
        contact.setLead(this);
    }

    public void removeContact(Contact contact) {
        contacts.remove(contact);
        contact.setLead(null);
    }

    public void addInteraction(Interaction interaction) {
        interactions.add(interaction);
        interaction.setLead(this);
    }

    // Business logic method
    public boolean requiresCallToday() {
        if (lastCallDate == null) return true;  // Never called
        return !nextCallDate.isAfter(LocalDate.now());
    }

}
