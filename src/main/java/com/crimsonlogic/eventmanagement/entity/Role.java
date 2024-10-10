package com.crimsonlogic.eventmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    
    @Id
    @Column(name = "role_id", length = 10)
    private String roleId;
    
    @Column(name = "role_name", length = 50)
    private String roleName;

    @OneToOne
    @JoinColumn(name = "role_for_user")
    private UserAuthentication roleForUser;
}
