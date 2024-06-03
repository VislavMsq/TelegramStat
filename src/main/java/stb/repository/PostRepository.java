package stb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stb.entity.Post;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID>{
    boolean existsByTelegramId(Integer messageId);

    Post findFirstByTelegramId(Integer messageId);
}
