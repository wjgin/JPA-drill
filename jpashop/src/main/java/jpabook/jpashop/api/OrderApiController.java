package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class OrderApiController {


    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();    // Lazy 강제 초기화
            order.getDelivery().getAddress();   // Lazy 강제 초기화
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().map(o -> o.getItem().getName());    // Lazy 강제 초기화
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }

    @Getter
    static class OrderDto {
         private Long orderId;
         private String name;
         private LocalDateTime orderDate;
         private OrderStatus orderStatus;
         private Address address;
         // private List<OrderItem> orderItems;
        private List<OrderItemDto> orderItems;

         public OrderDto(Order order){
             orderId = order.getId();
             name = order.getMember().getName();
             orderDate = order.getDateTime();
             orderStatus = order.getOrderStatus();
             address = order.getDelivery().getAddress();
             order.getOrderItems().stream().forEach(o -> o.getItem().getName());    // Lazy 초기화
             // orderItems = order.getOrderItems(); // orderItems는 dto가 아닌 entity가 직접 노출 됨
             orderItems = order.getOrderItems().stream()
                     .map(o -> new OrderItemDto(o))
                     .collect(Collectors.toList());
         }

    }

    @Getter
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }




}
