package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // orders 에는 하나의 member // fetch를 LAZY로 변경
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id") // Order을 연관관계 주인으로 설정(FK), Order에서 Delivery를 찾는 방식으로 설계
    private Delivery delivery;

    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // [ORDER, CANCEL]

    // == 연관관계 편의 메서드  ==// -> 양방향 관계에서 세팅 (양쪽 세팅을 한 코드로 해결)
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setDateTime(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.ORDER);
        return order;
    }

    //==비지니스 로직==//

    /**
     * 주문 취소
     */
    public void cancel(){
        if (delivery.getDeliveryStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송된 상품은 취소가 불가능 합니다.");
        }
        this.setOrderStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
     public int getTotalPrice(){
         return orderItems.stream()
                 .mapToInt(OrderItem::getTotalPrice)
                 .sum();
     }
}
