package jpabook.jpashop.repository.order.simplequery;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSimpleQueryDto {
    private Long id;
    private String name;
    private LocalDateTime orderTime;
    private OrderStatus orderStatus;
    private Address address;

    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderTime, OrderStatus orderStatus, Address address) {
        this.id = orderId;
        this.name = name;    // Lazy 초기화
        this.orderTime = orderTime;
        this.orderStatus = orderStatus;
        this.address = address; // Lazy 초기화
    }
}
