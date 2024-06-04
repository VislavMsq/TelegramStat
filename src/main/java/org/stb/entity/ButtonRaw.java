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
@Table(name = "button_raw")
public class ButtonRaw {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}
    )
    @JoinColumn(name = "poll_id")
    private PollRaw pollRaw;

    @Column(name = "text")
    private String text;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ButtonRaw buttonRaw = (ButtonRaw) o;
        return Objects.equals(id, buttonRaw.id) && Objects.equals(text, buttonRaw.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text);
    }

    @Override
    public String toString() {
        return "ButtonRaw{" +
                ", id=" + id +
                ", text='" + text + '\'' +
                '}';
    }
}
