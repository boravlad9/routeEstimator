package code.roadEstimator.services;

import code.roadEstimator.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import code.roadEstimator.entities.Utilizator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Utilizator> findPersons() {
        List<Utilizator> userList = userRepository.findAll();
        return userList;
    }

    public Utilizator findPersonById(UUID id) {
        Optional<Utilizator> prosumerOptional = userRepository.findById(id);
        if (!prosumerOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
        }
        return prosumerOptional.get();
    }

    public Utilizator findUserByEmailAndPassword(Utilizator user) {
        List<Utilizator> userList = userRepository.findUserByEmailAndPassword(user.getEmail(), user.getPassword());
        if (userList.size() == 0)
        {
            return null;
        }
        return userList.get(0);
    }

    public Utilizator insert(Utilizator user) {
        return userRepository.save(user);
    }

}
