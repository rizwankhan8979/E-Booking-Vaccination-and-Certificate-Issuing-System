package com.example.vaccineManagement.Repository;

import com.example.vaccineManagement.Entity.AuthUser;
import com.example.vaccineManagement.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    Optional<User> findByAuthUser(AuthUser authUser);
    Optional<User> findByAuthUserEmail(String email);
}
