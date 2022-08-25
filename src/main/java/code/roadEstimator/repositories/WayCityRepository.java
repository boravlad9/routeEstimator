package code.roadEstimator.repositories;

import code.roadEstimator.entities.WayCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WayCityRepository extends JpaRepository<WayCity, UUID> {
}
