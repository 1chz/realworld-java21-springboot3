package sample.shirohoo.realworld.core.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface TagRepository {
    List<Tag> saveAll(Collection<Tag> tags);

    Set<Tag> findByNameIn(Collection<String> names);

    List<Tag> findAll();
}
