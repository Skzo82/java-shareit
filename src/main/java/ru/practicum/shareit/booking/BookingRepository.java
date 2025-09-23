package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // ---- для booker ----
    Page<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable p);

    Page<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long id, LocalDateTime now1, LocalDateTime now2, Pageable p); // CURRENT

    Page<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime now, Pageable p); // PAST

    Page<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long id, LocalDateTime now, Pageable p); // FUTURE

    Page<Booking> findByBookerIdAndStatusOrderByStartDesc(Long id, BookingStatus status, Pageable p); // WAIT/REJ

    // ---- для owner ----
    @Query("select b from Booking b where b.item.owner.id = :ownerId order by b.start desc")
    Page<Booking> findByOwner(@Param("ownerId") Long ownerId, Pageable p);

    @Query("select b from Booking b where b.item.owner.id = :ownerId and b.start <= :now and b.end >= :now order by b.start desc")
    Page<Booking> findOwnerCurrent(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Pageable p);

    @Query("select b from Booking b where b.item.owner.id = :ownerId and b.end < :now order by b.start desc")
    Page<Booking> findOwnerPast(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Pageable p);

    @Query("select b from Booking b where b.item.owner.id = :ownerId and b.start > :now order by b.start desc")
    Page<Booking> findOwnerFuture(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Pageable p);

    @Query("select b from Booking b where b.item.owner.id = :ownerId and b.status = :status order by b.start desc")
    Page<Booking> findOwnerByStatus(@Param("ownerId") Long ownerId, @Param("status") BookingStatus status, Pageable p);

    // ---- last/next booking для item ----
    Booking findTop1ByItemIdAndStartBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime now, BookingStatus status);

    Booking findTop1ByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime now, BookingStatus status);

    // ---- проверка права оставить комментарий ----
    @Query("""
            select count(b) > 0 from Booking b
            where b.item.id = :itemId
              and b.booker.id = :userId
              and b.end < :now
              and b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED
            """)
    boolean userHasPastApprovedBooking(@Param("userId") Long userId,
                                       @Param("itemId") Long itemId,
                                       @Param("now") LocalDateTime now);

    @Query("""
            select count(b) > 0
            from Booking b
            where b.item.id = :itemId
              and b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED
              and b.start < :end and b.end > :start
            """)
    boolean existsApprovedOverlap(@Param("itemId") Long itemId,
                                  @Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end);
}
