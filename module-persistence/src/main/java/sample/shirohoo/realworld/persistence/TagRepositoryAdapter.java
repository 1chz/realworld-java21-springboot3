package sample.shirohoo.realworld.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import sample.shirohoo.realworld.core.model.Tag;
import sample.shirohoo.realworld.core.model.TagRepository;

@Repository
@RequiredArgsConstructor
class TagRepositoryAdapter implements TagRepository {
  private final TagJpaRepository tagJpaRepository;

  @Override
  public List<Tag> saveAll(Collection<Tag> tags) {
    return tagJpaRepository.saveAll(tags);
  }

  @Override
  public List<Tag> findAll() {
    return tagJpaRepository.findAll();
  }

  @Override
  public Set<Tag> findByNameIn(Collection<String> names) {
    return tagJpaRepository.findByNameIn(names);
  }
}
