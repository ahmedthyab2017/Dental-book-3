package com.dantal.task.repository;

import com.dantal.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByClinicIdOrderByCreatedAtDesc(UUID clinicId);

    Optional<Task> findByIdAndClinicId(UUID id, UUID clinicId);
}
