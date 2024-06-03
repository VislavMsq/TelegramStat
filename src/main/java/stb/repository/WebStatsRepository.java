package stb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stb.entity.WebStats;

import java.util.UUID;

@Repository
public interface WebStatsRepository extends JpaRepository<WebStats, UUID> {
    WebStats findByChannelIdAndGlobalId(Long channelId, Long messageId);

//    @Query("SELECT c FROM Channel c JOIN FETCH c.users")
//@Query("SELECT w FROM WebStats w JOIN FETCH w.channelId WHERE w.channelId = :channelId AND w.localId = :localId")
    WebStats findFirstByChannelIdAndLocalId(Long channelId, Integer localId);

}
