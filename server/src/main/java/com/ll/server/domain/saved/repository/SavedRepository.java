package com.ll.server.domain.saved.repository;

import com.ll.server.domain.saved.entity.Saved;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedRepository extends JpaRepository<Saved,Long> {
    List<Saved> findSavedsByDeletedIsFalse();
}
