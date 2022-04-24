package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)  // 조회만
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional  // 저장의 경우 commit 필요
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional  // 변경 감지 방법 -> 변경된 필드만 수정 후 commit
    public void updateItem(Long itemId, String name, int price, int stockQuantity){
        // 영속 상태의 객체이므로 @Transactional 로 인해서 변화를 감지(dirty checking)해서 수정함 -> return 값도 필요치 않음
        Item findItem = findOne(itemId);
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long id) {
        return itemRepository.findOne(id);
    }
}
