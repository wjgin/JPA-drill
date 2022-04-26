package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

// adding sample db
@Component
@AllArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct  // spring boot 켜진 후 실행
    public void init(){
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @AllArgsConstructor
    static class InitService {

        private final EntityManager em;

        public void dbInit1(){
            Member member = createMember("memberA", "서울", "11", "111-1");
            em.persist(member);

            Book book1 = createBook("JPA book1", 10000, 100);
            em.persist(book1);

            Book book2 = createBook("JPA boo2",20000, 100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }

        public void dbInit2(){
            Member member = createMember("memberB", "부산", "2", "222-1");
            em.persist(member);

            Book book1 = createBook("spring book1",10000, 100);
            em.persist(book1);

            Book book2 = createBook("spring book2",20000, 100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        private Book createBook(String name, int price, int stockQuantity) {
            Book book2 = new Book();
            book2.setName(name);
            book2.setPrice(price);
            book2.setStockQuantity(stockQuantity);
            return book2;
        }

        private Member createMember(String name, String city, String street, String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }
    }
}
