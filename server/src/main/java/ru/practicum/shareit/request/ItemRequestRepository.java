package ru.practicum.shareit.request;

import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @NonNull
    @EntityGraph(attributePaths = "requester")
    Optional<ItemRequest> findById(@NonNull Long requestId);

    List<ItemRequest> findAllByRequesterIdOrderByCreatedAtDesc(Long requesterId);
}
