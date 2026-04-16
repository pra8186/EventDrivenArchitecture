package com.eventdriven.core.repository;

import com.eventdriven.core.entity.TaxProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TaxProfileRepository extends JpaRepository<TaxProfile, UUID> {

    List<TaxProfile> findByUser_UserId(UUID userId);
}
