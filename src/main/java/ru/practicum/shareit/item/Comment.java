package ru.practicum.shareit.item;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "created_at")
    @CreationTimestamp
    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment)) return false;
        return id != null && id.equals(((Comment) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
