package com.ll.server.domain.saved.repository;

import com.ll.server.domain.saved.entity.Saved;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedRepository extends JpaRepository<Saved,Long> {
}
