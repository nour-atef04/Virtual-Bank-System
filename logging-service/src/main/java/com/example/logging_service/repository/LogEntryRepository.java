package com.example.logging_service.repository;

import com.example.logging_service.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public  interface LogEntryRepository extends JpaRepository<LogEntry, Long>{
}