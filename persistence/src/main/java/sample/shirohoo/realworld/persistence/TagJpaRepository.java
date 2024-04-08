package sample.shirohoo.realworld.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import sample.shirohoo.realworld.core.model.Tag;

interface TagJpaRepository extends JpaRepository<Tag, Integer> {}
