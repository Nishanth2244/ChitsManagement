package com.project.chitti.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.chitti.entity.LoanTransactions;


@Repository
public interface LoanTransactionRepository extends JpaRepository<LoanTransactions, Long> {

	List<LoanTransactions> findByLoanInstallmentIdOrderByIdAsc(Long installmentId);

}
