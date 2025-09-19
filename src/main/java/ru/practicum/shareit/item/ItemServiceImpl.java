package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.error.ForbiddenException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    // offset-пагинация (from/size) → PageRequest
    private Pageable pageOf(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Invalid pagination params: from=" + from + ", size=" + size);
        }
        return PageRequest.of(from / size, size);
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto dto) {
        // 1) проверяем пользователя и вещь
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));

        // 2) право на комментарий: должна быть прошедшая одобренная аренда
        LocalDateTime now = LocalDateTime.now();
        boolean allowed = bookingRepository.userHasPastApprovedBooking(userId, itemId, now);
        if (!allowed) {
            throw new ValidationException("User has no past approved booking for this item");
        }

        // 3) сохраняем комментарий
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(now);

        Comment saved = commentRepository.save(comment);

        // 4) маппинг в DTO
        return new CommentDto(
                saved.getId(),
                saved.getText(),
                saved.getAuthor().getName(),
                saved.getCreated()
        );
    }

    public ItemDto getItemById(Long viewerId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));

        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId);

        if (item.getOwner().getId().equals(viewerId)) {
            LocalDateTime now = LocalDateTime.now();
            Booking last = bookingRepository.findTop1ByItemIdAndStartBeforeAndStatusOrderByEndDesc(
                    itemId, now, BookingStatus.APPROVED);
            Booking next = bookingRepository.findTop1ByItemIdAndStartAfterAndStatusOrderByStartAsc(
                    itemId, now, BookingStatus.APPROVED);
            return ItemMapper.toDto(item, last, next, comments);
        } else {
            ItemDto dto = ItemMapper.toDto(item);
            dto.setComments(ItemMapper.toCommentDtoList(comments));
            return dto;
        }
    }

    public List<ItemDto> getItemsByOwner(Long ownerId, int from, int size) {
        Pageable pageable = pageOf(from, size);

        return itemRepository.findByOwnerId(ownerId, pageable)
                .stream()
                .map(item -> {
                    LocalDateTime now = LocalDateTime.now();
                    Booking last = bookingRepository.findTop1ByItemIdAndStartBeforeAndStatusOrderByEndDesc(
                            item.getId(), now, BookingStatus.APPROVED);
                    Booking next = bookingRepository.findTop1ByItemIdAndStartAfterAndStatusOrderByStartAsc(
                            item.getId(), now, BookingStatus.APPROVED);
                    List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(item.getId());
                    return ItemMapper.toDto(item, last, next, comments);
                })
                .collect(Collectors.toList());
    }

    /**
     * поиск вещей по тексту (без last/next, только доступные)
     */
    public List<ItemDto> search(String text, int from, int size) {
        if (text == null || text.isBlank()) return List.of();
        Pageable pageable = pageOf(from, size);
        return itemRepository.search(text, pageable)
                .stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.toDto(item);
                    dto.setComments(ItemMapper.toCommentDtoList(
                            commentRepository.findByItemIdOrderByCreatedDesc(item.getId())
                    ));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto create(Long ownerId, ItemDto dto) {
        // владелец существует
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("owner not found"));
        // валидации, которые ждут тесты
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ValidationException("name required");
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new ValidationException("description required");
        }
        if (dto.getAvailable() == null) {
            throw new ValidationException("available required");
        }

        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setOwner(owner);

        Item saved = itemRepository.save(item);
        return ItemMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ItemDto update(Long ownerId, ItemDto dto) {
        Item item = itemRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("item not found"));
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Only owner can update");
        }
        if (dto.getName() != null) item.setName(dto.getName());
        if (dto.getDescription() != null) item.setDescription(dto.getDescription());
        if (dto.getAvailable() != null) item.setAvailable(dto.getAvailable());
        return ItemMapper.toDto(itemRepository.save(item));
    }
}
