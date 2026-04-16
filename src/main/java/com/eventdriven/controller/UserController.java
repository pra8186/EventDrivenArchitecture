package com.eventdriven.controller;

import com.eventdriven.dto.request.CreateUserRequest;
import com.eventdriven.dto.response.UserResponse;
import com.eventdriven.service.TaxService;
import com.eventdriven.service.ValidationResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

//@Validated ==> validates the method parameters and path variables
//=> @valid used over RequestBody to validate the DTO's
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final TaxService taxService;

    public UserController(TaxService taxService) {
        this.taxService = taxService;
    }

    /** Create a user — validates input, returns 201. */
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = taxService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Retrieve a single user by ID. */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        return taxService.findUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Search users by name or email. */
    @GetMapping("/search")
    public List<UserResponse> search(@RequestParam String q) {
        return taxService.searchUsers(q);
    }

    /** Trigger a validation check on the user's profile completeness. */
    @GetMapping("/{id}/validate")
    public ValidationResult validate(@PathVariable UUID id) {
        return taxService.validateUserProfile(id);
    }
}
