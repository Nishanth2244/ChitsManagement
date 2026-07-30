package com.project.chitti.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.chitti.entity.LoanInstallments;


@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallments, Long> {

	List<LoanInstallments> findByLoanIdOrderByMonthNumberAsc(Long loanId);

	List<LoanInstallments> findByLoanId(Long loanId);

}
