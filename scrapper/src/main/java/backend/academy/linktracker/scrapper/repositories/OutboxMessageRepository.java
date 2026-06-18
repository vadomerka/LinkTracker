package backend.academy.linktracker.scrapper.repositories;

import backend.academy.linktracker.scrapper.models.entities.OutboxMessageEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxMessageRepository extends JpaRepository<OutboxMessageEntity, UUID> {

    @Query("SELECT o.id FROM OutboxMessageEntity o WHERE o.processedAt IS NULL ORDER BY o.createdAt ASC")
    java.util.List<UUID> findUnprocessedIds(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM OutboxMessageEntity o WHERE o.id = :id AND o.processedAt IS NULL")
    Optional<OutboxMessageEntity> findUnprocessedByIdForUpdate(@Param("id") UUID id);
}
