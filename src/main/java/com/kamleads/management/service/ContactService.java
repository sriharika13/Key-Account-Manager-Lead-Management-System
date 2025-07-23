package com.kamleads.management.service;

import com.kamleads.management.dto.request.ContactCreateRequestDto;
import com.kamleads.management.dto.response.ContactResponseDto;
import com.kamleads.management.model.Contact;
import com.kamleads.management.model.Lead;
import com.kamleads.management.repository.ContactRepository;
import com.kamleads.management.repository.InteractionRepository; // Assuming this is needed for total interactions
import com.kamleads.management.repository.LeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final LeadRepository leadRepository;
    private final InteractionRepository interactionRepository; // To get total interactions for a contact

    @Autowired
    public ContactService(ContactRepository contactRepository, LeadRepository leadRepository,
                          InteractionRepository interactionRepository) {
        this.contactRepository = contactRepository;
        this.leadRepository = leadRepository;
        this.interactionRepository = interactionRepository;
    }

    /**
     * Creates a new contact for a specific lead.
     * Ensures only one primary contact per lead.
     *
     * @param requestDto The DTO containing contact creation details.
     * @return ContactResponseDto of the created contact.
     * @throws RuntimeException if lead not found or email already exists for this lead,
     * or if trying to set multiple primary contacts.
     */
    @Transactional
    public ContactResponseDto createContact(ContactCreateRequestDto requestDto) {
        Lead lead = leadRepository.findById(requestDto.getLeadId())
                .orElseThrow(() -> new RuntimeException("Lead not found with ID: " + requestDto.getLeadId()));

        // Check for unique email within the same lead
        if (requestDto.getEmail() != null && !requestDto.getEmail().isEmpty()) {
            Optional<Contact> existingContact = contactRepository.findByLeadIdAndEmail(lead.getId(), requestDto.getEmail());
            if (existingContact.isPresent()) {
                throw new IllegalArgumentException("Contact with this email already exists for this lead.");
            }
        }

        // Ensure only one primary contact per lead
        if (requestDto.getIsPrimary() != null && requestDto.getIsPrimary()) {
            Optional<Contact> primaryContact = contactRepository.findPrimaryContactByLeadId(lead.getId());
            if (primaryContact.isPresent()) {
                // Option 1: Throw error (strict)
                throw new IllegalArgumentException("A primary contact already exists for this lead. Only one primary contact is allowed.");
                // Option 2: Demote existing primary contact (more flexible)
                // primaryContact.get().setIsPrimary(false);
                // contactRepository.save(primaryContact.get());
            }
        }

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID());
        contact.setLead(lead);
        contact.setName(requestDto.getName());
        contact.setRole(requestDto.getRole());
        contact.setEmail(requestDto.getEmail());
        contact.setIsPrimary(requestDto.getIsPrimary() != null ? requestDto.getIsPrimary() : false);
        // Assuming phone is not in DTO, or add it to DTO if needed
        // contact.setPhone(requestDto.getPhone());

        Contact savedContact = contactRepository.save(contact);
        return mapToContactResponseDto(savedContact);
    }

    /**
     * Retrieves a contact by its ID.
     *
     * @param id The UUID of the contact.
     * @return Optional<ContactResponseDto> if found, empty otherwise.
     */
    @Transactional(readOnly = true)
    public Optional<ContactResponseDto> getContactById(UUID id) {
        return contactRepository.findById(id).map(this::mapToContactResponseDto);
    }

    /**
     * Retrieves all contacts for a specific lead.
     *
     * @param leadId The UUID of the lead.
     * @return List of ContactResponseDto.
     * @throws RuntimeException if lead not found.
     */
    @Transactional(readOnly = true)
    public List<ContactResponseDto> getContactsByLeadId(UUID leadId) {
        if (!leadRepository.existsById(leadId)) {
            throw new RuntimeException("Lead not found with ID: " + leadId);
        }
        return contactRepository.findByLeadIdOrderByNameAsc(leadId).stream()
                .map(this::mapToContactResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing contact's details.
     *
     * @param id The UUID of the contact to update.
     * @param requestDto The DTO containing updated contact details.
     * @return ContactResponseDto of the updated contact.
     * @throws RuntimeException if contact or lead not found, or email conflict, or primary contact conflict.
     */
    @Transactional
    public ContactResponseDto updateContact(UUID id, ContactCreateRequestDto requestDto) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found with ID: " + id));

        Lead lead = leadRepository.findById(requestDto.getLeadId()) // Ensure lead ID matches or is valid
                .orElseThrow(() -> new RuntimeException("Lead not found with ID: " + requestDto.getLeadId()));

        if (!contact.getLead().getId().equals(lead.getId())) {
            throw new IllegalArgumentException("Cannot change lead for an existing contact.");
        }

        // Check for unique email within the same lead, excluding the current contact
        if (requestDto.getEmail() != null && !requestDto.getEmail().isEmpty()) {
            Optional<Contact> existingContactWithEmail = contactRepository.findByLeadIdAndEmail(lead.getId(), requestDto.getEmail());
            if (existingContactWithEmail.isPresent() && !existingContactWithEmail.get().getId().equals(id)) {
                throw new IllegalArgumentException("Contact with this email already exists for this lead.");
            }
        }

        // Handle primary contact logic during update
        if (requestDto.getIsPrimary() != null && requestDto.getIsPrimary()) {
            Optional<Contact> primaryContact = contactRepository.findPrimaryContactByLeadId(lead.getId());
            if (primaryContact.isPresent() && !primaryContact.get().getId().equals(id)) {
                // Demote existing primary contact
                primaryContact.get().setIsPrimary(false);
                contactRepository.save(primaryContact.get());
            }
        } else if (contact.getIsPrimary() && (requestDto.getIsPrimary() == null || !requestDto.getIsPrimary())) {
            // If current contact was primary and is being demoted, ensure another primary is not automatically set,
            // or handle logic for no primary contact.
            // For now, allow demotion without forcing a new primary.
        }


        contact.setName(requestDto.getName());
        contact.setRole(requestDto.getRole());
        contact.setEmail(requestDto.getEmail());
        contact.setIsPrimary(requestDto.getIsPrimary() != null ? requestDto.getIsPrimary() : false);
        // Assuming phone is not in DTO, or add it to DTO if needed
        // contact.setPhone(requestDto.getPhone());

        Contact updatedContact = contactRepository.save(contact);
        return mapToContactResponseDto(updatedContact);
    }

    /**
     * Deletes a contact by its ID.
     *
     * @param id The UUID of the contact to delete.
     * @throws RuntimeException if contact not found.
     */
    @Transactional
    public void deleteContact(UUID id) {
        if (!contactRepository.existsById(id)) {
            throw new RuntimeException("Contact not found with ID: " + id);
        }
        contactRepository.deleteById(id);
    }

    /**
     * Helper method to map Contact entity to ContactResponseDto.
     *
     * @param contact The Contact entity.
     * @return ContactResponseDto.
     */
    private ContactResponseDto mapToContactResponseDto(Contact contact) {
        ContactResponseDto dto = new ContactResponseDto();
        dto.setId(contact.getId());
        dto.setName(contact.getName());
        dto.setRole(contact.getRole());
        dto.setEmail(contact.getEmail());
        dto.setIsPrimary(contact.getIsPrimary());
        dto.setLeadId(contact.getLead().getId());
        dto.setLeadName(contact.getLead().getName());
        // Assuming a method exists in InteractionRepository to count interactions by contactId
        // This might be an N+1 query issue if called for many contacts; consider optimizing.
        // For simplicity, I'm assuming such a method or counting is acceptable.
        // You might need to add this method to InteractionRepository:
        // Long countByContactId(UUID contactId);
        dto.setTotalInteractions(interactionRepository.countByContactId(contact.getId()));
        return dto;
    }
}
