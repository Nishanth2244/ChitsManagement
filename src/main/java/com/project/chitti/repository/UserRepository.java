package com.project.chitti.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.chitti.entity.Users;


@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

	Optional<Users> findByEmail(String username);

	boolean existsByEmail(String string);

	boolean existsByName(String name);
	
	Page<Users> findByStatusAndRoleNot(boolean status, String role, Pageable pageable);
	
	
}
