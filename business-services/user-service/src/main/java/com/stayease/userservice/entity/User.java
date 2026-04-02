package com.stayease.userservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false,unique=true)
    private String email;
    @Column(nullable=false)
    private String password;
    @Column(nullable=false)
    private String fullName;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Role role;
    @Column(nullable=false,updatable = false)
    private LocalDateTime createdAt;
    @PrePersist
    public void prePersist(){
        this.createdAt=LocalDateTime.now();
        if(this.role==null){
            this.role=Role.USER;
        }
    }
    public enum Role{
        USER,ADMIN
    }


}
