package stb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stb.entity.Button;
import stb.entity.ButtonUserLink;
import stb.entity.User;

import java.util.List;
import java.util.UUID;

@Repository
public interface ButtonUserLinkRepository extends JpaRepository<ButtonUserLink, UUID> {

    boolean existsByButtonAndUserTelegramId(Button button, Long id);

    List<ButtonUserLink> findAllByUser(User currentUser);
}
