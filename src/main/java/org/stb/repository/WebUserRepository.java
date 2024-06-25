package org.stb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stb.entity.WebUser;

@Repository
public interface WebUserRepository extends JpaRepository<WebUser, Long>{

    boolean existsByTelegramId(Long id);
}
