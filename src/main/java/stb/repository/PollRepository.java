package stb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stb.entity.Channel;
import stb.entity.Poll;

import java.util.UUID;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    boolean existsById(UUID id);

    Poll findFirstById(UUID id);

    boolean existsByTextAndChannel(String text, Channel channel);

//    void findFirstById(UUID uuid);
}
