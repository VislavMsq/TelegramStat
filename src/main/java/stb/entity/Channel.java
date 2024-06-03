package stb.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "channels")
public class Channel {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "channel_id")
    private Long channelId;

    // для оплаты подписки
    @Column(name = "end_time")
    private LocalDateTime timeout;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "title")
    private String title;

    @OneToMany(
            mappedBy = "channel",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY
    )
    private Set<User> users;

    @OneToMany(
            mappedBy = "channel",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.EAGER
    )
    private Set<Post> post;

    @OneToMany(
            mappedBy = "channel",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY
    )
    private Set<Admin> admins;



    @Transactional
    public boolean containsAdmin(Long adminId) {
        return admins.stream()
                .map(Admin::getTelegramId)
                .collect(Collectors.toList())
                .contains(adminId);
    }

    @Transactional
    public boolean removeAdminIf(Long id) {
        return admins.removeIf(admin -> admin.getTelegramId().equals(id));
    }

    @Transactional
    public void addAdmin(Long userId) {
        Admin admin = new Admin();
        admin.setTelegramId(userId);
        admin.setChannel(this);
        admins.add(admin);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel aChannel = (Channel) o;
        return Objects.equals(id, aChannel.id) && Objects.equals(channelId, aChannel.channelId) && Objects.equals(timeout, aChannel.timeout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, channelId, timeout);
    }

    @Override
    public String toString() {
        return "Public{" +
                "id=" + id +
                ", telegramId=" + channelId +
                ", timeout=" + timeout +
                '}';
    }
}
