package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

// 추상 클래스로 상 구현체 생성
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();


    //==비지니스 로직==//
    // 외부에서 setter를 이용한 값 변경이 아닌 객체 안에서 핵심 비지니르 로직을 만들어 변경하는 것이 객체지향스러운 설계
    /**
     * stock 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int quantity) {
        int result = this.stockQuantity - quantity;
        if (result < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = result;
    }

}
