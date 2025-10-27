package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // Spring Data понимает "owner.id" из названия метода (поле owner указывает на User)
    Page<Item> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("""
            select i from Item i
            where (lower(i.name) like lower(concat('%', :text, '%'))
                or lower(i.description) like lower(concat('%', :text, '%')))
              and i.available = true
            """)
    Page<Item> search(@Param("text") String text, Pageable pageable);

    List<Item> findByRequest_IdIn(Iterable<Long> requestIds);

    List<Item> findByRequest_Id(Long requestId);
}
