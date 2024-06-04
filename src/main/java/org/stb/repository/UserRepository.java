package org.stb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stb.entity.Channel;
import org.stb.entity.User;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>{
    User findFirstByTelegramId(Long id);

    boolean existsByChannelAndTelegramId(Channel channel, Long telegramId);

    User findFirstByChannelAndTelegramId(Channel channel, Long id);
}
