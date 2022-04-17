package code.roadEstimator.repositories;

import code.roadEstimator.entities.Noduri;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NoduriRepository extends JpaRepository<Noduri, UUID> {
}
