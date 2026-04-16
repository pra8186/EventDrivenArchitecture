package com.eventdriven.core.controller;

import com.eventdriven.core.dto.request.CreateWorkDayEntryRequest;
import com.eventdriven.core.dto.response.WorkDayEntryResponse;
import com.eventdriven.core.service.TaxService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/entries")
public class WorkDayEntryController {

    private final TaxService taxService;

    public WorkDayEntryController(TaxService taxService) {
        this.taxService = taxService;
    }

    /** Create a work day entry (association) — validates input, returns 201. */
    @PostMapping
    public ResponseEntity<WorkDayEntryResponse> create(@Valid @RequestBody CreateWorkDayEntryRequest request) {
        WorkDayEntryResponse response = taxService.createWorkDayEntry(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** List all work day entries for a given tax profile. */
    @GetMapping("/profile/{profileId}")
    public List<WorkDayEntryResponse> listByProfile(@PathVariable UUID profileId) {
        return taxService.findEntriesByProfile(profileId);
    }
}
