package com.zm.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zm.auth.models.Roles;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Integer> {

	Roles findByRole(String role);

}
