package stb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stb.entity.ButtonRaw;

import java.util.UUID;

@Repository
public interface ButtonRawRepository extends JpaRepository<ButtonRaw, Long> {
    boolean existsById(UUID uuid);
}
