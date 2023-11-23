package sample.shirohoo.realworld.persistence;

import java.util.Collection;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import sample.shirohoo.realworld.core.model.Tag;

interface TagJpaRepository extends JpaRepository<Tag, Integer> {
    Set<Tag> findByNameIn(Collection<String> names);
}
