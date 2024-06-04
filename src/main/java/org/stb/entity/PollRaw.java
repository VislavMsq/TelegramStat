package org.stb.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "raw_polls")
public class PollRaw {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "text")
    private String text;

    @OneToMany(
            mappedBy = "pollRaw",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY
    )
    private List<ButtonRaw> buttons;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PollRaw pollRaw = (PollRaw) o;
        return Objects.equals(id, pollRaw.id) && Objects.equals(createdAt, pollRaw.createdAt) && Objects.equals(text, pollRaw.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, text);
    }

    @Override
    public String toString() {
        return "PollRaw{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", text='" + text + '\'' +
                '}';
    }
}
