package backend.academy.linktracker.scrapper.models.entities;

import jakarta.persistence.Entity;
import java.util.List;

@Entity
public class TagEntity {
    private String name;
    private List<LinkEntity> links;
}
