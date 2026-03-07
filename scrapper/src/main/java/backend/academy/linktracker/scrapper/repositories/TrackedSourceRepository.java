package backend.academy.linktracker.scrapper.repositories;

import backend.academy.linktracker.scrapper.models.TrackedSource;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TrackedSourceRepository {
    private final List<TrackedSource> array;

    public TrackedSourceRepository() {
        array = new ArrayList<>();
    }

    public List<TrackedSource> getArray() {
        return array;
    }

    public void addItem(TrackedSource item) {
        array.add(item);
    }

    public void removeItem(TrackedSource item) {
        array.remove(item);
    }
}
