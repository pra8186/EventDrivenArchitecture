package com.eventdriven.core.controller;

import com.eventdriven.core.dto.request.CreateTaxProfileRequest;
import com.eventdriven.core.dto.response.TaxProfileResponse;
import com.eventdriven.core.service.TaxService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
public class TaxProfileController {

    private final TaxService taxService;

    public TaxProfileController(TaxService taxService) {
        this.taxService = taxService;
    }

    /** Create a tax profile — validates input, returns 201. */
    @PostMapping
    public ResponseEntity<TaxProfileResponse> create(@Valid @RequestBody CreateTaxProfileRequest request) {
        TaxProfileResponse response = taxService.createTaxProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Retrieve a single tax profile by ID. */
    @GetMapping("/{id}")
    public ResponseEntity<TaxProfileResponse> getById(@PathVariable UUID id) {
        return taxService.findTaxProfileById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** List all profiles for a given user. */
    @GetMapping("/user/{userId}")
    public List<TaxProfileResponse> listByUser(@PathVariable UUID userId) {
        return taxService.findTaxProfilesByUser(userId);
    }
}
