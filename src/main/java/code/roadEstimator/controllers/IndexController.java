package code.roadEstimator.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
public class IndexController {

    @GetMapping(value = "/")
    public ResponseEntity<String> checkIfApIsOn() {
        return new ResponseEntity<>("Up.", HttpStatus.OK);
    }
}
