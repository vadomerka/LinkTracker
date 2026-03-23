package backend.academy.linktracker.scrapper.services.managers;

import backend.academy.linktracker.scrapper.models.entities.TagEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public interface TagManager {
    void createTag(String name);

    Optional<TagEntity> getTag(String name);

    List<TagEntity> getAllTags();

    void deleteTag(String name);

    boolean tagExists(String name);
}
