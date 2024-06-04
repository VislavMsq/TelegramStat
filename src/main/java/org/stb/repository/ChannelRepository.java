package org.stb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.stb.entity.Channel;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    boolean existsByChannelId(Long chatId);

    Channel findFirstByChannelId(Long chatId);

    Channel findFirsByChatId(Long chatId);

    boolean existsByChatId(Long chatId);

//    @Query("SELECT c FROM Channel c JOIN FETCH c.post")
//    List<Channel> findAllWithPosts();

    Channel findFirstByChatId(Long chatId);

    Channel findFirstById(UUID id);

    Channel findFirsById(UUID channelId);

    @Query("SELECT c FROM Channel c JOIN FETCH c.users")
    List<Channel> findAllWithUsers();
}
