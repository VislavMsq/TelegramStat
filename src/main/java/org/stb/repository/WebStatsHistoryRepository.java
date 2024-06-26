package org.stb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stb.entity.WebStatsHistory;

import java.util.UUID;

public interface WebStatsHistoryRepository extends JpaRepository<WebStatsHistory, UUID> {
}
