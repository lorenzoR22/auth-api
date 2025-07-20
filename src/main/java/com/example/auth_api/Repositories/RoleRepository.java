package com.example.auth_api.Repositories;


import com.example.auth_api.Models.Entities.ERole;
import com.example.auth_api.Models.Entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
