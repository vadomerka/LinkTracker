package backend.academy.linktracker.scrapper.services.managers.orm;

import backend.academy.linktracker.scrapper.models.entities.TagEntity;
import backend.academy.linktracker.scrapper.repositories.TagRepository;
import java.util.List;
import java.util.Optional;
import backend.academy.linktracker.scrapper.services.managers.TagManager;
import org.springframework.stereotype.Service;

@Service
public class ORMTagManager implements TagManager {

    private final TagRepository repository;

    public ORMTagManager(TagRepository repository) {
        this.repository = repository;
    }

    public void createTag(String name) {
        var tag = new TagEntity(name);
        repository.save(tag);
    }

    public Optional<TagEntity> getTag(String name) {
        return repository.findById(name);
    }

    public List<TagEntity> getAllTags() {
        return repository.findAll();
    }

    public void deleteTag(String name) {
        repository.deleteById(name);
    }

    public boolean tagExists(String name) {
        return repository.existsById(name);
    }
}
