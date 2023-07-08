package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component("dbUserRepository")
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User getUserById(Long userId);

}
