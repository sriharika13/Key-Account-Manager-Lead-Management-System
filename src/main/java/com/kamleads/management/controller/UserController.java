package com.kamleads.management.controller;

import com.kamleads.management.dto.request.UserCreateRequestDto;
import com.kamleads.management.dto.response.UserResponseDto;
import com.kamleads.management.exception.ResourceNotFoundException;
import com.kamleads.management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Creates a new user (KAM).
     * Accessible by users with 'ADMIN' role (or 'KAM' if all users are KAMs and can create others).
     * For simplicity, assuming 'KAM' can create other 'KAM's for now.
     * In a real app, user creation might be restricted to an 'ADMIN' role.
     */
    @PostMapping
    @PreAuthorize("hasRole('KAM')") // Or hasRole('ADMIN')
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateRequestDto requestDto) {
        UserResponseDto createdUser = userService.createUser(requestDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Retrieves a user by ID.
     * Accessible by 'KAM' role.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id) {
        UserResponseDto user = userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return ResponseEntity.ok(user);
    }

    /**
     * Retrieves all users with pagination.
     * Accessible by 'KAM' role.
     */
    @GetMapping
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(Pageable pageable) {
        Page<UserResponseDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves KAMs who have leads assigned to them, with pagination.
     * Accessible by 'KAM' role.
     */
    @GetMapping("/kams-with-leads")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<Page<UserResponseDto>> getKamsWithLeads(Pageable pageable) {
        Page<UserResponseDto> kams = userService.getKamsWithLeads(pageable);
        return ResponseEntity.ok(kams);
    }

    /**
     * Updates an existing user.
     * Accessible by 'KAM' role.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable UUID id, @Valid @RequestBody UserCreateRequestDto requestDto) {
        UserResponseDto updatedUser = userService.updateUser(id, requestDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Deletes a user.
     * Accessible by 'KAM' role.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
