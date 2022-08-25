package code.roadEstimator.repositories;

import code.roadEstimator.entities.NodesCity;
import code.roadEstimator.entities.Noduri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NodesCityRepository extends JpaRepository<NodesCity, UUID> {
}
