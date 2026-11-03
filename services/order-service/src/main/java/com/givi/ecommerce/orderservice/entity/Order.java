package com.givi.ecommerce.orderservice.entity;

import com.givi.ecommerce.orderservice.common.BaseEntity;
import com.givi.ecommerce.orderservice.common.Money;
import com.givi.ecommerce.orderservice.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(
                        name = "idx_order_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_order_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_order_created_at",
                        columnList = "created_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {

    @Column(
            name = "user_id",
            nullable = false
    )
    private Long userId;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<OrderItem> items =
            new ArrayList<>();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "amount",
                    column = @Column(
                            name = "total_amount_amount",
                            precision = 38,
                            scale = 2
                    )
            ),
            @AttributeOverride(
                    name = "currency",
                    column = @Column(
                            name = "total_amount_currency",
                            length = 3
                    )
            )
    })
    private Money totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;
}