package com.crimsonlogic.eventmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "userAuthentication")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthentication {
    
    @Id
    @Column(name = "user_id", length = 10)
    private String userId;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "password", length = 30)
    private String password;
}
