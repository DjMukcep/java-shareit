package ru.practicum.shareit.item;

import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @NonNull
    @EntityGraph(attributePaths = {"owner"})
    Optional<Item> findById(@NonNull Long id);

    List<Item> findByOwnerId(Long ownerId);

    @EntityGraph(attributePaths = {"owner"})
    List<Item> findAllByRequestId(Long requestId);

    @EntityGraph(attributePaths = {"owner"})
    List<Item> findAllByRequestRequesterId(Long requesterId);

    @Query("select i from Item i " +
            "where i.owner.id = :userId " +
            "and i.isAvailable = true " +
            "and (upper(i.name) like upper(concat('%', :text, '%')) " +
            "or upper(i.description) like upper(concat('%', :text, '%')))")
    List<Item> searchItem(Long userId, String text);
}
