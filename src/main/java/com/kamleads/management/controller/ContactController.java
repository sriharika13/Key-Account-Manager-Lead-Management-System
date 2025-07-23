package com.kamleads.management.controller;

import com.kamleads.management.dto.request.ContactCreateRequestDto;
import com.kamleads.management.dto.response.ContactResponseDto;
import com.kamleads.management.exception.ResourceNotFoundException;
import com.kamleads.management.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    /**
     * Creates a new contact for a lead.
     * Accessible by 'KAM' role.
     */
    @PostMapping
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<ContactResponseDto> createContact(@Valid @RequestBody ContactCreateRequestDto requestDto) {
        ContactResponseDto createdContact = contactService.createContact(requestDto);
        return new ResponseEntity<>(createdContact, HttpStatus.CREATED);
    }

    /**
     * Retrieves a contact by ID.
     * Accessible by 'KAM' role.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<ContactResponseDto> getContactById(@PathVariable UUID id) {
        ContactResponseDto contact = contactService.getContactById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with ID: " + id));
        return ResponseEntity.ok(contact);
    }

    /**
     * Retrieves all contacts for a specific lead.
     * Accessible by 'KAM' role.
     */
    @GetMapping("/by-lead/{leadId}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<List<ContactResponseDto>> getContactsByLeadId(@PathVariable UUID leadId) {
        List<ContactResponseDto> contacts = contactService.getContactsByLeadId(leadId);
        return ResponseEntity.ok(contacts);
    }

    /**
     * Updates an existing contact.
     * Accessible by 'KAM' role.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<ContactResponseDto> updateContact(@PathVariable UUID id, @Valid @RequestBody ContactCreateRequestDto requestDto) {
        ContactResponseDto updatedContact = contactService.updateContact(id, requestDto);
        return ResponseEntity.ok(updatedContact);
    }

    /**
     * Deletes a contact.
     * Accessible by 'KAM' role.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<Void> deleteContact(@PathVariable UUID id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}
