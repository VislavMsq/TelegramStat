
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
@Table(name = "web_stats")
public class WebStats {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "channel_id")
    private Long channelId;

    @Column(name = "local_id")
    private Integer localId;

    @Column(name = "global_id")
    private Long globalId;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "reaction_count")
    private Integer reactionCount;

    @Column(name = "reply_count")
    private Integer replyCount;

    @Column(name = "last_view_update")
    private LocalDateTime lastUpdateView;

    @Column(name = "last_reaction_update")
    private LocalDateTime lastUpdateReaction;

    @Column(name = "last_reply_update")
    private LocalDateTime lastUpdateReply;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebStats webStats = (WebStats) o;
        return Objects.equals(id, webStats.id) && Objects.equals(channelId, webStats.channelId) && Objects.equals(localId, webStats.localId) && Objects.equals(globalId, webStats.globalId) && Objects.equals(viewCount, webStats.viewCount) && Objects.equals(reactionCount, webStats.reactionCount) && Objects.equals(replyCount, webStats.replyCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, channelId, localId, globalId, viewCount, reactionCount, replyCount);
    }

    @Override
    public String toString() {
        return "WebStats{" +
                "channelId=" + channelId +
                ", id=" + id +
                ", localId=" + localId +
                ", globalId=" + globalId +
                ", viewCount=" + viewCount +
                ", reactionCount=" + reactionCount +
                ", replyCount=" + replyCount +
                ", lastUpdateView=" + lastUpdateView +
                ", lastUpdateReaction=" + lastUpdateReaction +
                ", lastUpdateReply=" + lastUpdateReply +
                '}';
    }
}
