package org.example.anpfacturationbackend.service;

import org.example.anpfacturationbackend.entity.AuditLog;
import org.example.anpfacturationbackend.repository.AuditLogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String action, String details) {
        String username = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : "SYSTEM";

        AuditLog log = new AuditLog(username, action, details);
        auditLogRepository.save(log);
    }

    public java.util.List<AuditLog> getAll() {
        return auditLogRepository.findAll(org.springframework.data.domain.Sort
                .by(org.springframework.data.domain.Sort.Direction.DESC, "timestamp"));
    }
}
