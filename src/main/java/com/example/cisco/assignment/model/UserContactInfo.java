package com.example.cisco.assignment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class UserContactInfo {

    public UUID userId;
    public String firstName;
    public String lastName;
    public String instagram;
    public String twitter;
    public String email;
}
