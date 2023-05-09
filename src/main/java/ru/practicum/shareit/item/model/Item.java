package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
// Перед тем, как использовать аннотации для автоматической генерации кода в классах-сущностях,
// хорошо было бы ознакомиться со статьей
// https://habr.com/ru/company/haulmont/blog/564682/
// Аналогично в других местах
// - done
@Entity
@Table(name = "items")
@NoArgsConstructor
public class Item {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_available")
    private Boolean available;

    @Column(name = "owner_id")
    private Long owner;

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)

    // Лучше избегать связи @OneToMany, так как по дефолту данные подтягиваются лениво,
    // то есть FetchType.LAZY, то есть при каждом вызове геттера для этого поля будет происходить обращение к БД,
    // из-за чего будут плодится n-лишние запросы.
    // Если мы будем использовать FetchType.EAGER, тогда каждый раз при получении вещи мы будем тащить
    // и ее комментарии, что тоже не совсем оптимально.
    // В сервисе добавил уточнение, как поступить
    // - done
    private Set<Comment> comments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id) &&
                Objects.equals(name, item.name) &&
                Objects.equals(description, item.description) &&
                Objects.equals(available, item.available) &&
                Objects.equals(owner, item.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, available, owner);
    }

    public Set<Comment> getComments() {
        return new HashSet<>();
    }
}
