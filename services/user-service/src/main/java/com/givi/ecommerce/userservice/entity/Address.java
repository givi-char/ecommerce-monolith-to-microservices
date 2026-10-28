package com.givi.ecommerce.userservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.givi.ecommerce.userservice.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
public class Address extends BaseEntity {

    private String line1;

    private String line2;

    private String city;

    private String state;

    private String country;

    @Column(name = "postal_code")
    private String postalCode;

    @JsonBackReference
    @ManyToOne
    private User user;
}