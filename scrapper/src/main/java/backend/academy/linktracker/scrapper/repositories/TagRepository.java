package backend.academy.linktracker.scrapper.repositories;

import backend.academy.linktracker.scrapper.models.entities.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, String> {}
