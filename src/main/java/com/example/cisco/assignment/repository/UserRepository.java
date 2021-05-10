package com.example.cisco.assignment.repository;
import com.example.cisco.assignment.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserInfo,UUID> {

    List<UserInfo> findByCity(String city);
    List<UserInfo>findByCityAndProfession(String city, String profession);
    List<UserInfo> findByEmail(String email);
    List<UserInfo> findByDevEnvironmentContainingIgnoreCase(String devEnvironment);

}
