package com.kamleads.management.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "contacts",
        indexes = {
                @Index(name = "idx_contacts_lead", columnList = "lead_id"),
                @Index(name = "idx_contacts_email", columnList = "email"),
                @Index(name = "idx_contacts_primary", columnList = "lead_id, is_primary")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_lead_email", columnNames = {"lead_id", "email"})
        })
public class Contact { //represents Points of Contact (POCs) for restaurants
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    // Many contacts belong to one lead
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    @JsonBackReference("lead-contacts")
    private Lead lead;

    @NotBlank(message = "Contact name is required")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "role", length = 50)
    private String role;  // e.g., "Manager", "Owner", "Chef"

    @Email(message = "Email should be valid")
    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One-to-Many: One contact can have many interactions
    @OneToMany(
            mappedBy = "contact",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}, //why not delte???
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private List<Interaction> interactions = new ArrayList<>();

    // Constructors
    public Contact() {}

    public Contact(Lead lead, String name, String role, String email) {
        this.lead = lead;
        this.name = name;
        this.role = role;
        this.email = email;
    }
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Lead getLead() { return lead; }
    public void setLead(Lead lead) { this.lead = lead; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public List<Interaction> getInteractions() { return interactions; }
    public void setInteractions(List<Interaction> interactions) { this.interactions = interactions; }
}
