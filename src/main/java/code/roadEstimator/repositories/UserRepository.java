package code.roadEstimator.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import code.roadEstimator.entities.Utilizator;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Utilizator, UUID> {
    List<Utilizator> findUserByEmailAndPassword(String email, String password);
}
