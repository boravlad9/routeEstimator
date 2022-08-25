package code.roadEstimator.controllers;

import code.roadEstimator.dto.Coordinates;
import code.roadEstimator.dto.RoutingResult;
import code.roadEstimator.entities.NodesCity;
import code.roadEstimator.repositories.UserRepository;
import code.roadEstimator.services.RoutingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/routing")
public class Routing {

    private final RoutingService routingService = new RoutingService();

    @PostMapping(value = "/getRoute")
    public ResponseEntity<RoutingResult> getRoute (@RequestBody Coordinates coordinates) {
        return new ResponseEntity<>(routingService.getRoute(coordinates), HttpStatus.OK);
    }

    @PostMapping(value = "/demo")
    public ResponseEntity<RoutingResult> demo() {
        return new ResponseEntity(routingService.demo(), HttpStatus.OK);
    }
}
