package com.kamleads.management.service;

import com.kamleads.management.dto.request.UserCreateRequestDto;
import com.kamleads.management.dto.response.UserResponseDto;
import com.kamleads.management.model.User;
import com.kamleads.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Assuming you have a PasswordEncoder bean configured

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new user (KAM).
     * Hashes the password before saving.
     *
     * @param requestDto The DTO containing user creation details.
     * @return UserResponseDto of the created user.
     * @throws IllegalArgumentException if email already exists.
     */
    @Transactional
    public UserResponseDto createUser(UserCreateRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        User user = new User();
        user.setId(UUID.randomUUID()); // Generate a new UUID for the user
        user.setName(requestDto.getName());
        user.setEmail(requestDto.getEmail());
        user.setTimezone(requestDto.getTimezone());
        // Encode the password before saving
        user.setPasswordHash(passwordEncoder.encode(requestDto.getPassword()));

        User savedUser = userRepository.save(user);
        return mapToUserResponseDto(savedUser);
    }

    /**
     * Finds a user by their ID.
     *
     * @param id The UUID of the user.
     * @return Optional<UserResponseDto> if found, empty otherwise.
     */
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> getUserById(UUID id) {
        return userRepository.findById(id).map(this::mapToUserResponseDto);
    }

    /**
     * Finds a user by their email.
     *
     * @param email The email of the user.
     * @return Optional<UserResponseDto> if found, empty otherwise.
     */
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::mapToUserResponseDto);
    }

    /**
     * Retrieves all users with pagination.
     *
     * @param pageable Pagination information.
     * @return Page of UserResponseDto.
     */
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);
        List<UserResponseDto> dtoList = usersPage.getContent().stream()
                .map(this::mapToUserResponseDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, usersPage.getTotalElements());
    }

    /**
     * Retrieves all KAMs who have leads assigned to them.
     *
     * @param pageable Pagination information.
     * @return Page of UserResponseDto.
     */
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getKamsWithLeads(Pageable pageable) {
        Page<User> kamsPage = userRepository.findKamsWithLeads(pageable);
        List<UserResponseDto> dtoList = kamsPage.getContent().stream()
                .map(this::mapToUserResponseDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, kamsPage.getTotalElements());
    }

    /**
     * Updates an existing user's details.
     *
     * @param id The UUID of the user to update.
     * @param requestDto The DTO containing updated user details.
     * @return UserResponseDto of the updated user.
     * @throws RuntimeException if user not found or email already exists (for a different user).
     */
    @Transactional
    public UserResponseDto updateUser(UUID id, UserCreateRequestDto requestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Check if email exists for another user
        Optional<User> existingUserWithEmail = userRepository.findByEmail(requestDto.getEmail());
        if (existingUserWithEmail.isPresent() && !existingUserWithEmail.get().getId().equals(id)) {
            throw new IllegalArgumentException("Email already taken by another user.");
        }

        user.setName(requestDto.getName());
        user.setEmail(requestDto.getEmail());
        user.setTimezone(requestDto.getTimezone());
        // Only update password if a new one is provided (assuming requestDto.getPassword() is not null/blank for update)
        if (requestDto.getPassword() != null && !requestDto.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(requestDto.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return mapToUserResponseDto(updatedUser);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The UUID of the user to delete.
     * @throws RuntimeException if user not found.
     */
    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Helper method to map User entity to UserResponseDto.
     * Note: totalLeads and activeLeads are not directly available in User entity
     * without joining with Lead entity or pre-calculating. For simplicity,
     * they are set to null/0 here. In a real app, you might fetch these counts
     * or use a dedicated DTO for KAM dashboards.
     *
     * @param user The User entity.
     * @return UserResponseDto.
     */
    private UserResponseDto mapToUserResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setTimezone(user.getTimezone());
        // These fields would typically be populated by a more complex query or analytics service
        dto.setTotalLeads(null); // Or fetch from LeadRepository.countByKamId(user.getId())
        dto.setActiveLeads(null); // Or fetch from LeadRepository.countByKamIdAndStatusIn(...)
        return dto;
    }
}
