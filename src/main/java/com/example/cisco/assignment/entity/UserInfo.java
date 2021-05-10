package com.example.cisco.assignment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "User_Info")
//@IdClass(EmailId.class)
//TODO Fix Primary Key
public class UserInfo {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "userId")
    private UUID userId;

//    @EmbeddedId
//    private EmailId id;

    private String firstName;
    private String lastName;

//    @Id
    private String email;

    private String twitter;
    private String instagram;
    private String devEnvironment;
    private String city;
    private String technology;
    private String profession;


}
