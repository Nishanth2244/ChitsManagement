package com.project.chitti.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.chitti.entity.Installments;

@Repository
public interface InstallmentRepository extends JpaRepository<Installments, Long> {
	
	Optional<Installments> findByChitMemberIdAndMonthNumber(Long chitMemberId, Integer monthNumber);
	
	List<Installments> findByChitMemberIdOrderByMonthNumberAsc(Long chitMemberId);
}