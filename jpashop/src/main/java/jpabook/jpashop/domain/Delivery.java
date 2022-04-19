package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    // Delivery -> Order 조회가 아닌 Order -> Delivery 조회 방식을 선택
    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)    // Enum의 경우 STRING type을 꼭 선택 (ORDER의 경우 추가적인 ENUM이 생겼을 시 숫자가 밀려서 장애가 발생할 수 있음)
    private DeliveryStatus deliveryStatus;  // [READY, COMP]

}
