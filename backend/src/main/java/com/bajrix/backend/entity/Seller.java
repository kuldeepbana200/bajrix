package com.bajrix.backend.entity;

import com.bajrix.backend.enums.SellerStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sellers")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SellerStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Seller() {
    }

    public Seller(String name, SellerStatus status) {
        this.name = name;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public SellerStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(SellerStatus status) {
        this.status = status;
    }
}