package com.project.chitti.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.chitti.entity.Transactions;


@Repository
public interface TransactionRepository extends JpaRepository<Transactions, Long> {

	List<Transactions> findByInstallmentIdOrderByIdAsc(Long installmentId);

}
