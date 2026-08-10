package com.kanban.kanbanProject.repository;

import com.kanban.kanbanProject.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<Users, Long> {

    Users findByEmailId(String emailId);

//    Users findByFirstName(String firstName);

    List<Users> findByFirstNameContainingIgnoreCase(String firstName);

}
