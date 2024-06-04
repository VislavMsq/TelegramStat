package org.stb.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "td_public")
public class TdPublic {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "absolute_id")
    private Long absoluteId;

    @Column(name = "emoji_count")
    private String emojiCount;

    @Column(name = "views")
    private Long views;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TdPublic tdPublic = (TdPublic) o;
        return Objects.equals(id, tdPublic.id) && Objects.equals(absoluteId, tdPublic.absoluteId) && Objects.equals(emojiCount, tdPublic.emojiCount) && Objects.equals(views, tdPublic.views);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, absoluteId, emojiCount, views);
    }

    @Override
    public String toString() {
        return "TdPublic{" +
                "absoluteId=" + absoluteId +
                ", id=" + id +
                ", emojiCount='" + emojiCount + '\'' +
                ", views=" + views +
                '}';
    }
}

