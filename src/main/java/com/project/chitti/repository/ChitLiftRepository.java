package com.project.chitti.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.chitti.entity.ChitLifts;

@Repository
public interface ChitLiftRepository extends JpaRepository<ChitLifts, Long> {
    
    boolean existsByChitMemberId(Long chitMemberId);
    
    boolean existsByChitIdAndMonthNumber(Long chitId, Integer monthNumber);
    
    Optional<ChitLifts> findByChitMemberId(Long chitMemberId);
}