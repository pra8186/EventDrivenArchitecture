package com.eventdriven.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.eventdriven.dto.request.CreateTaxProfileRequest;
import com.eventdriven.dto.request.CreateUserRequest;
import com.eventdriven.dto.request.CreateWorkDayEntryRequest;
import com.eventdriven.dto.response.TaxProfileResponse;
import com.eventdriven.dto.response.UserResponse;
import com.eventdriven.dto.response.WorkDayEntryResponse;
import com.eventdriven.entity.*;
import com.eventdriven.event.EventProducerService;
import com.eventdriven.event.EventType;
import com.eventdriven.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Business logic for users, tax profiles, work day entries, and validation.
 * Publishes events to Kafka via {@link EventProducerService} on every create and validate.
 */
@Service
public class TaxService {

    private static final int BCRYPT_COST = 12;

    private final UserRepository userRepository;
    private final StateRepository stateRepository;
    private final TaxProfileRepository taxProfileRepository;
    private final WorkDayEntryRepository workDayEntryRepository;
    private final EventProducerService eventProducer;

    public TaxService(UserRepository userRepository,
                      StateRepository stateRepository,
                      TaxProfileRepository taxProfileRepository,
                      WorkDayEntryRepository workDayEntryRepository,
                      EventProducerService eventProducer) {
        this.userRepository = userRepository;
        this.stateRepository = stateRepository;
        this.taxProfileRepository = taxProfileRepository;
        this.workDayEntryRepository = workDayEntryRepository;
        this.eventProducer = eventProducer;
    }

    // --- User ---

    public UserResponse createUser(CreateUserRequest request) {
        String passwordHash = BCrypt.withDefaults()
                .hashToString(BCRYPT_COST, request.getPassword().toCharArray());
        User user = new User(
                request.getEmail(), passwordHash,
                request.getFirstName(), request.getLastName(),
                request.getSsnEncrypted()
        );
        User saved = userRepository.save(user);
        eventProducer.publish(
                saved.getUserId().toString(),
                saved.getUserId().toString(),
                EventType.USER_CREATED,
                "User created: " + saved.getFirstName() + " " + saved.getLastName()
        );
        return UserResponse.from(saved);
    }

    public Optional<UserResponse> findUserById(UUID id) {
        return userRepository.findById(id).map(UserResponse::from);
    }

    public List<UserResponse> searchUsers(String term) {
        return userRepository.search(term).stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    // --- TaxProfile ---

    public TaxProfileResponse createTaxProfile(CreateTaxProfileRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found: " + request.getUserId()));
        State state = stateRepository.findById(request.getStateCode())
                .orElseThrow(() -> new NoSuchElementException("State not found: " + request.getStateCode()));
        FilingStatus filingStatus = FilingStatus.valueOf(request.getFilingStatus());
        TaxProfile profile = new TaxProfile(user, state, request.getTaxYear(), filingStatus);
        TaxProfile saved = taxProfileRepository.save(profile);
        eventProducer.publish(
                user.getUserId().toString(),
                saved.getProfileId().toString(),
                EventType.TAX_PROFILE_CREATED,
                "Tax profile created for year " + saved.getTaxYear() + " in " + state.getStateCode()
        );
        return TaxProfileResponse.from(saved);
    }

    public Optional<TaxProfileResponse> findTaxProfileById(UUID profileId) {
        return taxProfileRepository.findById(profileId).map(TaxProfileResponse::from);
    }

    public List<TaxProfileResponse> findTaxProfilesByUser(UUID userId) {
        return taxProfileRepository.findByUser_UserId(userId).stream()
                .map(TaxProfileResponse::from)
                .collect(Collectors.toList());
    }

    // --- WorkDayEntry (association) ---

    public WorkDayEntryResponse createWorkDayEntry(CreateWorkDayEntryRequest request) {
        TaxProfile profile = taxProfileRepository.findById(request.getProfileId())
                .orElseThrow(() -> new NoSuchElementException("TaxProfile not found: " + request.getProfileId()));
        State state = stateRepository.findById(request.getStateCode())
                .orElseThrow(() -> new NoSuchElementException("State not found: " + request.getStateCode()));
        WorkType workType = WorkType.valueOf(request.getWorkType());
        WorkDayEntry entry = new WorkDayEntry(
                profile, state, request.getStartDate(), request.getEndDate(),
                request.getIncome(), workType
        );
        WorkDayEntry saved = workDayEntryRepository.save(entry);
        eventProducer.publish(
                profile.getUser().getUserId().toString(),
                saved.getEntryId().toString(),
                EventType.WORK_ENTRY_CREATED,
                "Work entry created: " + workType + " in " + state.getStateCode()
                        + " (" + request.getStartDate() + " to " + request.getEndDate() + ")"
        );
        return WorkDayEntryResponse.from(saved);
    }

    public List<WorkDayEntryResponse> findEntriesByProfile(UUID profileId) {
        return workDayEntryRepository.findByTaxProfile_ProfileId(profileId).stream()
                .map(WorkDayEntryResponse::from)
                .collect(Collectors.toList());
    }

    // --- Validation ---

    public ValidationResult validateUserProfile(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        Set<String> required = Set.of("USER", "TAX_PROFILE", "WORK_DAY_ENTRIES");
        Set<String> present = new LinkedHashSet<>();

        present.add("USER");

        List<TaxProfile> profiles = taxProfileRepository.findByUser_UserId(userId);
        if (!profiles.isEmpty()) {
            present.add("TAX_PROFILE");
        }

        boolean hasEntries = profiles.stream()
                .anyMatch(p -> !workDayEntryRepository.findByTaxProfile_ProfileId(p.getProfileId()).isEmpty());
        if (hasEntries) {
            present.add("WORK_DAY_ENTRIES");
        }

        Set<String> missing = new LinkedHashSet<>(required);
        missing.removeAll(present);

        ValidationResult result = new ValidationResult(List.copyOf(present), List.copyOf(missing), missing.isEmpty());
        eventProducer.publish(
                userId.toString(),
                userId.toString(),
                EventType.PROFILE_VALIDATED,
                "Validation: " + (result.isComplete() ? "COMPLETE" : "INCOMPLETE, missing: " + result.getMissingItems())
        );
        return result;
    }
}
