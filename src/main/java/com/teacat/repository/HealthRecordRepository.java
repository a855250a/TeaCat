package com.teacat.repository;

import com.teacat.entity.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    List<HealthRecord> findByUserIdOrderByRecordDateDescIdDesc(Long userId);
    List<HealthRecord> findByPetIdAndUserIdOrderByRecordDateDescIdDesc(Long petId, Long userId);
    long countByUserId(Long userId);
    void deleteByPetId(Long petId);
    void deleteByUserId(Long userId);
}
