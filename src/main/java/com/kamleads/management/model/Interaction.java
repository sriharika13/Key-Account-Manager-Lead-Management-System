package com.kamleads.management.model;

import com.kamleads.management.enums.InteractionStatus;
import com.kamleads.management.enums.InteractionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "interactions",
        indexes = {
                @Index(name = "idx_interactions_lead_date", columnList = "lead_id, interaction_date DESC"),
                @Index(name = "idx_interactions_kam_type", columnList = "kam_id, type, interaction_date"),
                @Index(name = "idx_interactions_contact", columnList = "contact_id"),
                @Index(name = "idx_interactions_follow_up", columnList = "follow_up_date")
        })
public class Interaction { //Interaction Entity represents calls and orders records
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    // Multiple foreign keys
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")  // Nullable - might be general interaction
    private Contact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kam_id", nullable = false)
    private User kam;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private InteractionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InteractionStatus status;

    @CreationTimestamp  // Automatically set when interaction is created
    @Column(name = "interaction_date", nullable = false)
    private LocalDateTime interactionDate;

    // For order-type interactions
    @Column(name = "order_value", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Order value cannot be negative")
    private BigDecimal orderValue;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public Interaction() {}

    public Interaction(Lead lead, User kam, InteractionType type, InteractionStatus status) {
        this.lead = lead;
        this.kam = kam;
        this.type = type;
        this.status = status;
    }


    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Lead getLead() { return lead; }
    public void setLead(Lead lead) { this.lead = lead; }

    public Contact getContact() { return contact; }
    public void setContact(Contact contact) { this.contact = contact; }

    public User getKam() { return kam; }
    public void setKam(User kam) { this.kam = kam; }

    public InteractionType getType() { return type; }
    public void setType(InteractionType type) { this.type = type; }

    public InteractionStatus getStatus() { return status; }
    public void setStatus(InteractionStatus status) { this.status = status; }

    public LocalDateTime getInteractionDate() { return interactionDate; }
    public void setInteractionDate(LocalDateTime interactionDate) { this.interactionDate = interactionDate; }

    public BigDecimal getOrderValue() { return orderValue; }
    public void setOrderValue(BigDecimal orderValue) { this.orderValue = orderValue; }

    public LocalDate getFollowUpDate() { return followUpDate; }
    public void setFollowUpDate(LocalDate followUpDate) { this.followUpDate = followUpDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }


}
