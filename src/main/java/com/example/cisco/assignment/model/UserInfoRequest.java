package com.example.cisco.assignment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserInfoRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String twitter;
    private String instagram;
    private List<String> devEnvironmentLists;
    private String city;
    private List<String> techLists;
    private String profession;
}
