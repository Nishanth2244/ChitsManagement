package com.project.chitti.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.chitti.entity.Loans;

@Repository
public interface LoanRepository extends JpaRepository<Loans, Long> {

	List<Loans> findByUserIdAndStatus(Long userId, boolean status);

}
