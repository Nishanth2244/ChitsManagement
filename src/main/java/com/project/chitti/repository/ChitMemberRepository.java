package com.project.chitti.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.chitti.entity.ChitMembers;
import com.project.chitti.entity.Chits;

@Repository
public interface ChitMemberRepository extends JpaRepository<ChitMembers, Long> {

	Optional<ChitMembers> findByChitIdAndUserId(Long chitId, Long userId);

	List<ChitMembers> findByChitId(Long chitId);

}
