package com.kamleads.management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import com.kamleads.management.model.Lead;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email", unique = true)
        })
public class User { //User Entity represents Key Account Managers (KAMs)
//    When saving this entity to the database, automatically generate a value for the id field. Use a generator named 'UUID' for that.
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name="UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Name is required") // Bean validation
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Email(message = "Email should be valid") //email format validation
    @NotBlank(message = "Email is required")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    // Timezone for handling different user locations
    @Column(name = "timezone", length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'UTC'")
    private String timezone = "UTC";

    @JsonIgnore
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // Automatic timestamp management
    @CreationTimestamp  // Hibernate automatically sets this on insert
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp  // Hibernate automatically updates this on modify
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One-to-Many relationship: One KAM manages many leads
    @OneToMany(
            mappedBy = "kam",  // Field name in Lead entity that references this User
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},  // Don't cascade DELETE
            fetch = FetchType.LAZY,  // Load leads only when accessed (performance)
            orphanRemoval = false  // Don't delete leads if KAM is removed
    )
    @JsonManagedReference("user-leads")
    private List<Lead> leads= new ArrayList<>();

    // One-to-Many: One KAM creates many interactions
    @OneToMany(
            mappedBy = "kam",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY
    )
    @JsonIgnore  // Too much data for API responses
    private List<Interaction> interactions = new ArrayList<>();

    @OneToMany(
            mappedBy = "kam",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CallSchedule> scheduledCalls;

    // Constructors
    public User() {}  // Required by JPA

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
    // Getters and Setters (Spring Boot generates these via Lombok in real projects)
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public List<Lead> getLeads() { return leads; }
    public void setLeads(List<Lead> leads) { this.leads = leads; }

    public List<Interaction> getInteractions() { return interactions; }
    public void setInteractions(List<Interaction> interactions) { this.interactions = interactions; }


    public void addLead(Lead lead){
        if(lead!=null){
            leads.add(lead);
            lead.setKam(this); // Maintain bidirectional relationship
        }

    }

    public void removeLead(Lead lead){
        if(lead!=null) {
            leads.remove(lead);
            lead.setKam(null);
        }
    }
}
