package com.teacat.entity;

import jakarta.persistence.*;
import java.util.List;

// 使用者資料表
@Entity
@Table(name = "users")
public class User {
    // 使用者編號
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    // 使用者信箱
    private String email;
    // 使用者密碼
    private String password;
    @OneToMany(mappedBy = "user")
    private List<Pet> pets;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }
}
