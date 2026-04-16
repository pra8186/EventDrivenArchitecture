package com.eventdriven;

import com.eventdriven.entity.State;
import com.eventdriven.repository.StateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full integration tests using {@code @SpringBootTest} with {@link MockMvc}.
 * H2 in-memory database, Flyway disabled, Hibernate creates schema via ddl-auto.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StateRepository stateRepository;

    @BeforeEach
    void seedStates() {
        if (stateRepository.findById("CA").isEmpty()) {
            stateRepository.save(new State("CA", "California"));
            stateRepository.save(new State("NY", "New York"));
        }
    }

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    // ================================================================
    // POST /api/users — create user (201)
    // ================================================================

    @Test
    @Order(1)
    void createUser_returns201_withResponseJson() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of(
                                "email", "alice@example.com",
                                "password", "securepass123",
                                "firstName", "Alice",
                                "lastName", "Smith",
                                "ssnEncrypted", "enc-ssn-001"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.createdAt").exists())
                // Verify password and SSN are NOT in response
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.ssnEncrypted").doesNotExist());
    }

    // ================================================================
    // POST /api/users — validation errors (400)
    // ================================================================

    @Test
    void createUser_invalidEmail_returns400() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of(
                                "email", "not-an-email",
                                "password", "securepass123",
                                "firstName", "Bob",
                                "lastName", "Jones",
                                "ssnEncrypted", "enc-ssn-002"
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void createUser_blankFields_returns400() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of(
                                "email", "",
                                "password", "",
                                "firstName", "",
                                "lastName", "",
                                "ssnEncrypted", ""
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Email is required")))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createUser_shortPassword_returns400() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of(
                                "email", "short@example.com",
                                "password", "short",
                                "firstName", "Test",
                                "lastName", "User",
                                "ssnEncrypted", "enc-ssn-short"
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Password must be at least 8 characters")));
    }

    // ================================================================
    // POST /api/users — duplicate email (409)
    // ================================================================

    @Test
    void createUser_duplicateEmail_returns409() throws Exception {
        String json = toJson(Map.of(
                "email", "dup@example.com",
                "password", "securepass123",
                "firstName", "Dup",
                "lastName", "User",
                "ssnEncrypted", "enc-ssn-dup1"
        ));
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());

        // Same email, different SSN
        String dup = toJson(Map.of(
                "email", "dup@example.com",
                "password", "securepass123",
                "firstName", "Dup2",
                "lastName", "User2",
                "ssnEncrypted", "enc-ssn-dup2"
        ));
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(dup))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // ================================================================
    // GET /api/users/{id} — found and not found
    // ================================================================

    @Test
    void getUser_found_returns200() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of(
                                "email", "get@example.com",
                                "password", "securepass123",
                                "firstName", "Get",
                                "lastName", "Test",
                                "ssnEncrypted", "enc-ssn-get"
                        ))))
                .andReturn();
        String userId = objectMapper.readTree(result.getResponse().getContentAsString()).get("userId").asText();

        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.email").value("get@example.com"));
    }

    @Test
    void getUser_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/users/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    // ================================================================
    // GET /api/users/search?q=...
    // ================================================================

    @Test
    void searchUsers_returnsMatches() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(Map.of(
                        "email", "search@example.com",
                        "password", "securepass123",
                        "firstName", "Searchable",
                        "lastName", "Person",
                        "ssnEncrypted", "enc-ssn-search"
                ))));

        mockMvc.perform(get("/api/users/search").param("q", "Searchable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].firstName").value("Searchable"));
    }

    @Test
    void searchUsers_noMatch_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/users/search").param("q", "zzzzzzzzz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ================================================================
    // POST /api/profiles — create tax profile (201)
    // ================================================================

    @Test
    void createProfile_returns201() throws Exception {
        String userId = createTestUser("profile@example.com", "enc-ssn-prof");

        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of(
                                "userId", userId,
                                "stateCode", "CA",
                                "taxYear", 2025,
                                "filingStatus", "SINGLE"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.profileId").exists())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.homeStateCode").value("CA"))
                .andExpect(jsonPath("$.taxYear").value(2025))
                .andExpect(jsonPath("$.filingStatus").value("SINGLE"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void createProfile_invalidUser_returns404() throws Exception {
        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of(
                                "userId", UUID.randomUUID().toString(),
                                "stateCode", "CA",
                                "taxYear", 2025,
                                "filingStatus", "SINGLE"
                        ))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("User not found")));
    }

    @Test
    void createProfile_validationError_returns400() throws Exception {
        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of(
                                "userId", UUID.randomUUID().toString(),
                                "stateCode", "X",
                                "taxYear", 1999,
                                "filingStatus", ""
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // ================================================================
    // GET /api/profiles/{id} and /api/profiles/user/{userId}
    // ================================================================

    @Test
    void getProfile_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/profiles/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void listProfilesByUser_returnsProfiles() throws Exception {
        String userId = createTestUser("listprof@example.com", "enc-ssn-lp");
        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(Map.of(
                        "userId", userId, "stateCode", "NY",
                        "taxYear", 2024, "filingStatus", "MFJ"
                ))));

        mockMvc.perform(get("/api/profiles/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    // ================================================================
    // POST /api/entries — create work day entry (association, 201)
    // ================================================================

    @Test
    void createEntry_returns201() throws Exception {
        String userId = createTestUser("entry@example.com", "enc-ssn-entry");
        String profileId = createTestProfile(userId, "CA", 2025, "SINGLE");

        mockMvc.perform(post("/api/entries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of(
                                "profileId", profileId,
                                "stateCode", "NY",
                                "startDate", "2025-01-01",
                                "endDate", "2025-03-31",
                                "income", "50000.00",
                                "workType", "REMOTE"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.entryId").exists())
                .andExpect(jsonPath("$.profileId").value(profileId))
                .andExpect(jsonPath("$.stateCode").value("NY"))
                .andExpect(jsonPath("$.workType").value("REMOTE"))
                .andExpect(jsonPath("$.income").value(50000.00));
    }

    @Test
    void createEntry_invalidProfile_returns404() throws Exception {
        mockMvc.perform(post("/api/entries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of(
                                "profileId", UUID.randomUUID().toString(),
                                "stateCode", "CA",
                                "startDate", "2025-01-01",
                                "endDate", "2025-03-31",
                                "income", "10000.00",
                                "workType", "ONSITE"
                        ))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("TaxProfile not found")));
    }

    @Test
    void listEntriesByProfile_returnsEntries() throws Exception {
        String userId = createTestUser("entries@example.com", "enc-ssn-entries");
        String profileId = createTestProfile(userId, "CA", 2023, "HOH");
        mockMvc.perform(post("/api/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(Map.of(
                        "profileId", profileId, "stateCode", "CA",
                        "startDate", "2023-06-01", "endDate", "2023-12-31",
                        "income", "75000", "workType", "CONFERENCE"
                ))));

        mockMvc.perform(get("/api/entries/profile/" + profileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    // ================================================================
    // GET /api/users/{id}/validate — validation check
    // ================================================================

    @Test
    void validateUser_incomplete_returnsMissing() throws Exception {
        String userId = createTestUser("val@example.com", "enc-ssn-val");

        mockMvc.perform(get("/api/users/" + userId + "/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.complete").value(false))
                .andExpect(jsonPath("$.presentItems", hasItem("USER")))
                .andExpect(jsonPath("$.missingItems", hasItem("TAX_PROFILE")))
                .andExpect(jsonPath("$.missingItems", hasItem("WORK_DAY_ENTRIES")));
    }

    @Test
    void validateUser_complete_returnsComplete() throws Exception {
        String userId = createTestUser("valcomplete@example.com", "enc-ssn-vc");
        String profileId = createTestProfile(userId, "CA", 2025, "SINGLE");
        mockMvc.perform(post("/api/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(Map.of(
                        "profileId", profileId, "stateCode", "CA",
                        "startDate", "2025-01-01", "endDate", "2025-06-30",
                        "income", "60000", "workType", "ONSITE"
                ))));

        mockMvc.perform(get("/api/users/" + userId + "/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.complete").value(true))
                .andExpect(jsonPath("$.missingItems", hasSize(0)));
    }

    @Test
    void validateUser_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/users/" + UUID.randomUUID() + "/validate"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("User not found")));
    }

    // ================================================================
    // GET /api/events — event log
    // ================================================================

    @Test
    void getEvents_returnsLoggedEvents() throws Exception {
        createTestUser("events@example.com", "enc-ssn-events");
        Thread.sleep(3000); // wait for async Kafka consumer to persist

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].eventId").exists())
                .andExpect(jsonPath("$[0].timestamp").exists())
                .andExpect(jsonPath("$[0].userId").exists())
                .andExpect(jsonPath("$[0].resourceId").exists())
                .andExpect(jsonPath("$[0].eventType").exists())
                .andExpect(jsonPath("$[0].details").exists());
    }

    @Test
    void getEvents_filterByType_returnsFiltered() throws Exception {
        createTestUser("evtype@example.com", "enc-ssn-evtype");
        Thread.sleep(3000);

        mockMvc.perform(get("/api/events").param("type", "USER_CREATED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventType").value("USER_CREATED"));
    }

    @Test
    void getEvents_filterByUser_returnsFiltered() throws Exception {
        String userId = createTestUser("evuser@example.com", "enc-ssn-evuser");
        Thread.sleep(3000);

        mockMvc.perform(get("/api/events/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].userId").value(userId));
    }

    // ================================================================
    // Helpers — create test data and extract IDs
    // ================================================================

    private String createTestUser(String email, String ssn) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of(
                                "email", email,
                                "password", "securepass123",
                                "firstName", "Test",
                                "lastName", "User",
                                "ssnEncrypted", ssn
                        ))))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("userId").asText();
    }

    private String createTestProfile(String userId, String stateCode, int year, String filingStatus) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of(
                                "userId", userId,
                                "stateCode", stateCode,
                                "taxYear", year,
                                "filingStatus", filingStatus
                        ))))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("profileId").asText();
    }
}
