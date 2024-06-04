package org.stb.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "buttons")
public class Button {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}
    )
    @JoinColumn(name = "poll_id")
    private Poll poll;

    @Column(name = "text")
    private String text;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "callback_data")
    private String callbackData;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Button button = (Button) o;
        return Objects.equals(id, button.id) && Objects.equals(createdAt, button.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt);
    }

    @Override
    public String toString() {
        return "Button{" +
                "createdAt=" + createdAt +
                ", id=" + id +
                '}';
    }
}

