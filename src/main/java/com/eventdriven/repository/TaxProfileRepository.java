package com.eventdriven.repository;

import com.eventdriven.entity.TaxProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TaxProfileRepository extends JpaRepository<TaxProfile, UUID> {

    List<TaxProfile> findByUser_UserId(UUID userId);
}
