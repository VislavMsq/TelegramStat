package stb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stb.entity.PollRaw;

import java.util.UUID;

@Repository
public interface PollRawRepository extends JpaRepository<PollRaw, Long> {
    boolean existsById(UUID uuid);

    PollRaw findFirstById(UUID uuid);
}
