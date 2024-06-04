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
@Table(name = "polls")
public class Poll {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "text")
    private String text;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}
    )
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @OneToMany(
            mappedBy = "poll",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY
    )
    private List<Button> buttons;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Poll poll = (Poll) o;
        return Objects.equals(id, poll.id) && Objects.equals(createdAt, poll.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt);
    }

    @Override
    public String toString() {
        return "Polls{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                '}';
    }
}
