package com.TZ.TechZone.repositories;

import com.TZ.TechZone.entities.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByAction(AuditLog.AuditAction action, Pageable pageable);
    
    Page<AuditLog> findByEntityTypeAndEntityId(AuditLog.AuditEntity entityType, Integer entityId, Pageable pageable);
    
    Page<AuditLog> findByUser_IdAndCreatedAtBetween(Integer userId, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
