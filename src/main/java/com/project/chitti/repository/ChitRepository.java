package com.project.chitti.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.chitti.entity.Chits;

@Repository
public interface ChitRepository extends JpaRepository<Chits, Long> {

	boolean existsByName(String chitName);

	Page<Chits> findByStatus(boolean status, Pageable pageable);

}
