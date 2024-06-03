package stb.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stb.entity.enums.InteractionType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "messages")
public class Message {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "text")
    private String text;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}
    )
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}
    )
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}
    )
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "message_time")
    private LocalDateTime messageTime;

    @Column(name = "interaction_type")
    @Enumerated(EnumType.STRING)
    private InteractionType interactionType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id) && Objects.equals(messageTime, message.messageTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, messageTime);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", messageTime=" + messageTime +
                '}';
    }
}
