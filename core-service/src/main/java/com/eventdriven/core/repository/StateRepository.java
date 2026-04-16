package com.eventdriven.core.repository;

import com.eventdriven.core.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<State, String> {
}
