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
@Table(name = "web_stats_historys")
public class WebStatsHistory {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "last_view_update")
    private LocalDateTime lastUpdateView;

    @Column(name = "last_reaction_update")
    private LocalDateTime lastUpdateReaction;

    @Column(name = "last_reply_update")
    private LocalDateTime lastUpdateReply;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "reaction_count")
    private Integer reactionCount;

    @Column(name = "reply_count")
    private Integer replyCount;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}
    )
    @JoinColumn(name = "web_stats_id")
    private WebStats webStats;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebStatsHistory that = (WebStatsHistory) o;
        return Objects.equals(id, that.id) && Objects.equals(lastUpdateView, that.lastUpdateView) && Objects.equals(lastUpdateReaction, that.lastUpdateReaction) && Objects.equals(lastUpdateReply, that.lastUpdateReply) && Objects.equals(viewCount, that.viewCount) && Objects.equals(reactionCount, that.reactionCount) && Objects.equals(replyCount, that.replyCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lastUpdateView, lastUpdateReaction, lastUpdateReply, viewCount, reactionCount, replyCount);
    }

    @Override
    public String toString() {
        return "WebStatsHistory{" +
                "id=" + id +
                ", lastUpdateView=" + lastUpdateView +
                ", lastUpdateReaction=" + lastUpdateReaction +
                ", lastUpdateReply=" + lastUpdateReply +
                ", viewCount=" + viewCount +
                ", reactionCount=" + reactionCount +
                ", replyCount=" + replyCount +
                '}';
    }
}