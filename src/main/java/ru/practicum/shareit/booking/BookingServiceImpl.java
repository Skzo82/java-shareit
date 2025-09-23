package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.error.ForbiddenException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private Pageable pageOf(int from, int size) {
        return PageRequest.of(from / size, size);
    }

    @Override
    @Transactional
    public BookingResponseDto addBooking(Long userId, BookingRequestDto dto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found: " + dto.getItemId()));

        if (dto.getStart() == null || dto.getEnd() == null || !dto.getStart().isBefore(dto.getEnd())) {
            throw new ValidationException("Invalid booking time range");
        }
        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new ValidationException("Item is not available");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Owner cannot book own item");
        }

        if (bookingRepository.existsApprovedOverlap(
                item.getId(), dto.getStart(), dto.getEnd())) {
            throw new ValidationException("Booking time overlaps with existing booking");
        }

        Booking saved = bookingRepository.save(BookingMapper.toEntity(dto, item, booker));
        return BookingMapper.toDto(saved);
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Only owner can approve/reject");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking already decided");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));

        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();

        if (!ownerId.equals(userId) && !bookerId.equals(userId)) {
            throw new NotFoundException("Booking not found"); // по условию курса
        }
        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long userId, String state, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found: " + userId));
        Pageable pageable = pageOf(from, size);
        LocalDateTime now = LocalDateTime.now();
        BookingState st = parseState(state);

        return switch (st) {
            case ALL -> bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable)
                    .map(BookingMapper::toDto).getContent();
            case CURRENT -> bookingRepository
                    .findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now, pageable)
                    .map(BookingMapper::toDto).getContent();
            case PAST -> bookingRepository
                    .findByBookerIdAndEndBeforeOrderByStartDesc(userId, now, pageable)
                    .map(BookingMapper::toDto).getContent();
            case FUTURE -> bookingRepository
                    .findByBookerIdAndStartAfterOrderByStartDesc(userId, now, pageable)
                    .map(BookingMapper::toDto).getContent();
            case WAITING -> bookingRepository
                    .findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageable)
                    .map(BookingMapper::toDto).getContent();
            case REJECTED -> bookingRepository
                    .findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, pageable)
                    .map(BookingMapper::toDto).getContent();
            case CANCELED -> bookingRepository
                    .findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.CANCELED, pageable)
                    .map(BookingMapper::toDto).getContent();
        };
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long ownerId, String state, int from, int size) {
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("User not found: " + ownerId));
        Pageable pageable = pageOf(from, size);
        LocalDateTime now = LocalDateTime.now();
        BookingState st = parseState(state);

        return switch (st) {
            case ALL -> bookingRepository.findByOwner(ownerId, pageable)
                    .map(BookingMapper::toDto).getContent();
            case CURRENT -> bookingRepository.findOwnerCurrent(ownerId, now, pageable)
                    .map(BookingMapper::toDto).getContent();
            case PAST -> bookingRepository.findOwnerPast(ownerId, now, pageable)
                    .map(BookingMapper::toDto).getContent();
            case FUTURE -> bookingRepository.findOwnerFuture(ownerId, now, pageable)
                    .map(BookingMapper::toDto).getContent();
            case WAITING -> bookingRepository.findOwnerByStatus(ownerId, BookingStatus.WAITING, pageable)
                    .map(BookingMapper::toDto).getContent();
            case REJECTED -> bookingRepository.findOwnerByStatus(ownerId, BookingStatus.REJECTED, pageable)
                    .map(BookingMapper::toDto).getContent();
            case CANCELED -> bookingRepository
                    .findOwnerByStatus(ownerId, BookingStatus.CANCELED, pageable)
                    .map(BookingMapper::toDto).getContent();
        };
    }

    private BookingState parseState(String state) {
        if (state == null || state.isBlank()) return BookingState.ALL;
        try {
            return BookingState.valueOf(state.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Unknown state: " + state);
        }
    }
}
