package code.roadEstimator.controllers;


import code.roadEstimator.entities.Utilizator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import code.roadEstimator.services.UserService;

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@CrossOrigin
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<Utilizator>> getUsers() {
        List<Utilizator> dtos = userService.findPersons();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Utilizator> getPerson(@PathVariable("id") UUID personId) {
        Utilizator dto = userService.findPersonById(personId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Utilizator> login( @RequestBody Utilizator user) {
        Utilizator myUser = userService.findUserByEmailAndPassword(user);
        if (myUser == null)
        {
            return new ResponseEntity<>(null,
                    HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(myUser,
                HttpStatus.OK);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<Utilizator> register( @RequestBody Utilizator user){
        return new ResponseEntity<>(userService.insert(user), HttpStatus.CREATED);
    }
}
