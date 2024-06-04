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
@Table(name = "images")
public class Image {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "image_id")
    private UUID imageId;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}
    )
    @JoinColumn(name = "notification_id")
    private Notification notificationId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return Objects.equals(id, image.id) && Objects.equals(imageId, image.imageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, imageId);
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", imageId=" + imageId +
                '}';
    }
}
