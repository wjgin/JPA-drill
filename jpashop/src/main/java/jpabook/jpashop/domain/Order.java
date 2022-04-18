package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne  // orders 에는 하나의 member
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "delivery_id") // Order을 연관관계 주인으로 설정(FK), Order에서 Delivery를 찾는 방식으로 설계
    private Delivery delivery;

    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // [ORDER, CANCEL]
}
