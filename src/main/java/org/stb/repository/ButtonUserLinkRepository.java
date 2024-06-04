package org.stb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stb.entity.User;
import org.stb.entity.Button;
import org.stb.entity.ButtonUserLink;

import java.util.List;
import java.util.UUID;

@Repository
public interface ButtonUserLinkRepository extends JpaRepository<ButtonUserLink, UUID> {

    boolean existsByButtonAndUserTelegramId(Button button, Long id);

    List<ButtonUserLink> findAllByUser(User currentUser);
}
