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
@Table(name = "nonification_button")
public class NotificationButton {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "notification_id")
    private Notification notification;

    @Column(name = "name_button")
    private String nameButton;

    @Column(name = "url_button")
    private String urlButton;

    @Column(name = "callback_data")
    private String callbackData;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationButton that = (NotificationButton) o;
        return Objects.equals(id, that.id) && Objects.equals(nameButton, that.nameButton) && Objects.equals(urlButton, that.urlButton) && Objects.equals(callbackData, that.callbackData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nameButton, urlButton, callbackData);
    }

    @Override
    public String toString() {
        return "NotificationButton{" +
                "callbackData='" + callbackData + '\'' +
                ", id=" + id +
                ", nameButton='" + nameButton + '\'' +
                ", urlButton='" + urlButton + '\'' +
                '}';
    }
}
