package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne(ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    // 권장되지 않는 방식
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        // 엔티티를 직접 조회하는 방법 -> 권장 x
        // 양방향 관계로 인해서 서로 조회하며 무한루프를 형성 -> @JasonIgnore 를 한쪽에 넣어줌(더 이상 조회 x)
        // 비어있는 필드(ex. Order 엔티티 안의 Member 필드)의 경우 Hibernate의 Proxy가 들어가 있음 -> json으로 바꾸기 위한 모듈 필(hibernate5Module)
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            // getName() 시 proxy에서 실제 member.name을 jpa가 쿼리 조회함(Delivery 역시 마찬가지)
            // 원하는 데이터만 Lazy 로딩을 무시하고 쿼리 조회
            order.getMember().getName();   // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2() {

        // ORDER 2개
        // N + 1문제 -> 1개의 쿼리 + 회원(N) + 배송(N) 쿼리 추가 됨 -> 속도 저하
        // order 1번 -> member 지연 로딩 조회 N번 -> delivery 지연 로딩 N번
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        return orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
    }

    // dto 자체로 조회 -> select 절에서 원하는 data를 직접 선-> 에플리케이션 네트웤 용량 측면에서 우수
    // v3와 달리 entity를 dto 변환 과정이 없기에 속도 측명에서 우수함
    // entity를 조회한 것이 아니기에 테이블 값의 변화는 불가능, fit한 dto를 직접 제작해야함
    // 유지 보수 측면에서 v3에 비해 떨어짐 (재 사용성이 적음)
    // v3와 tradeoff 관계
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4(){
        return orderRepository.findOrderDtos();
    }


    @Data
    static class SimpleOrderDto {
        private Long id;
        private String name;
        private LocalDateTime orderTime;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order o) {
            this.id = o.getId();
            this.name = o.getMember().getName();    // Lazy 초기화
            this.orderTime = o.getOrderDate();
            this.orderStatus = o.getOrderStatus();
            this.address = o.getMember().getAddress();  // Lazy 초기화
        }
    }
}
