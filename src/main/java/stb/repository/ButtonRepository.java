package stb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stb.entity.Button;

import java.util.UUID;
@Repository
public interface ButtonRepository extends JpaRepository<Button, Long> {
    Button findFirstById(UUID uuid);
}
