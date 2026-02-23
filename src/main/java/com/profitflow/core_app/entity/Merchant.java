package com.profitflow.core_app.entity;

import com.profitflow.core_app.entity.basic.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "merchants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Merchant extends SoftDeletableEntity {
    @Column(name = "company_name", nullable = false)
    private String companyName; // можно их как то там на ТОО, ОП классифицировать а не просто стринг,может сделать типо ENUM + name?

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;
}