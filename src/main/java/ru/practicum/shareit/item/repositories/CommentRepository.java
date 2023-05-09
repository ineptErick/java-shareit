package ru.practicum.shareit.item.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;
import java.util.Set;

// Аннотация @Repository не имеет смысла, так как наследуясь от JpaRepository мы уже объявляем бин
// Аналогично в других местах
// - done
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Set<Comment>> findByItem(Item item);

}
