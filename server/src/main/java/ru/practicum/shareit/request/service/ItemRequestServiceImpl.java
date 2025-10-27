package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestResponseDto create(Long userId, ItemRequestCreateDto dto) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        ItemRequest saved = requestRepository.save(ItemRequestMapper.toEntity(dto, requester));
        return ItemRequestMapper.toResponse(saved, List.of());
    }

    @Override
    public List<ItemRequestResponseDto> getOwn(Long userId) {
        ensureUserExists(userId);

        List<ItemRequest> requests = requestRepository
                .findByRequesterIdOrderByCreatedDesc(userId);

        return attachItems(requests);
    }

    @Override
    public List<ItemRequestResponseDto> getAll(Long userId, int from, int size) {
        ensureUserExists(userId);

        int page = from / size;
        PageRequest pageable = PageRequest.of(page, size, Sort.by("created").descending());

        List<ItemRequest> requests = requestRepository
                .findByRequesterIdNot(userId, pageable)
                .getContent();

        return attachItems(requests);
    }

    @Override
    public ItemRequestResponseDto getById(Long userId, Long requestId) {
        ensureUserExists(userId);

        ItemRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found: " + requestId));

        List<ItemShortDto> items = itemRepository.findByRequest_Id(requestId).stream()
                .map(ItemRequestMapper::toItemShort)
                .toList();

        return ItemRequestMapper.toResponse(req, items);
    }

    private void ensureUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }
    }

    // Присоединяем списки вещей ко всем запросам батчем
    private List<ItemRequestResponseDto> attachItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) return List.of();

        Set<Long> ids = requests.stream().map(ItemRequest::getId).collect(Collectors.toSet());
        List<Item> items = itemRepository.findByRequest_IdIn(ids);

        Map<Long, List<ItemShortDto>> byRequest = items.stream()
                .filter(it -> it.getRequest() != null) // safety
                .collect(Collectors.groupingBy(
                        it -> it.getRequest().getId(),
                        Collectors.mapping(ItemRequestMapper::toItemShort, Collectors.toList())
                ));

        return requests.stream()
                .map(r -> ItemRequestMapper.toResponse(r, byRequest.getOrDefault(r.getId(), List.of())))
                .toList();
    }
}