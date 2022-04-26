package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public List<Order> ordersV1(){
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
}
