package code.roadEstimator.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import code.roadEstimator.entities.Utilizator;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<Utilizator, UUID> {
    List<Utilizator> findUserByEmailAndPassword(String email, String password);
}
