package com.example.cisco.assignment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@Embeddable
public class EmailId implements Serializable {
    public UUID userId;
    public String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailId eid = (EmailId) o;
        return userId.equals(eid.userId) &&
               email.equals(eid.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email);
    }
}
