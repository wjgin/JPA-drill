package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문()throws Exception{
        // given
        Member member = createMember();

        Book book = createBook("시골 JPA", 10000, 10);

        // when
        int orderQuantity = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderQuantity);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getOrderStatus(), "주문시 상태는 ORDER 이다.");
        assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품이");
        assertEquals(10000 * orderQuantity, getOrder.getTotalPrice(), "총 가격은 가격 * 총 수량 이다.");
        assertEquals(10 - orderQuantity, book.getStockQuantity(), "주문 수량 만큼 재고가 줄어야한다.");
    }

    @Test
    public void 상품취소()throws Exception{
        // given
        Member member = createMember();
        int first_stock = 10;
        Book item = createBook("시골 JPA", 10000, first_stock);
        int orderCnt = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCnt);

        // when
        orderService.cancelOrder(orderId);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCEL, getOrder.getOrderStatus(), "상품은 취소 상태 CANCEL 이여야 합니다.");
        assertEquals(first_stock, item.getStockQuantity(), "item의 수량은 처음과 같아야 한다.");
    }

    @Test
    public void 상품주문_재고수량초과()throws Exception{
        // given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);

        // when
        int orderCnt = 11;  // 초과 주문

        // then
        assertThrows(NotEnoughStockException.class, () -> {
            orderService.order(member.getId(), item.getId(), orderCnt); // 주문시 예외 발생
        });
        // fail("재고 수량 부족 예외가 발생해야 합니다.");
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }
}