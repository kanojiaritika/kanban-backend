package com.kanban.kanbanProject.repository;

import com.kanban.kanbanProject.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<Users, Long> {

    Optional<Users> findByEmailId(String emailId);

}
