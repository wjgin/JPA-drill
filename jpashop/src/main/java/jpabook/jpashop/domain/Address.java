package jpabook.jpashop.domain;

import lombok.Getter;
import javax.persistence.Embeddable;

@Embeddable // JPA 스펙상 임베드디 타입은 기본 생성자를 public이나 protected로 설정
@Getter // 값 타입은 변경 불가능하게 설계
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
