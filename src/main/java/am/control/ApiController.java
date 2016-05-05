package am.control;


import am.model.User;
import am.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping(headers = {"Content-Type=application/json"}, produces = {"application/json"})
public class ApiController {

  @Autowired
  private UserRepository userRepository;

  @RequestMapping(method = RequestMethod.GET, value = "/api/user/{urn:.+}")
  public ResponseEntity<Object> user(@PathVariable String urn) {
    Optional<User> user = userRepository.findByUnspecifiedId(urn);
    return user.isPresent() ? ResponseEntity.ok(user.get()) : new ResponseEntity<>(user, NOT_FOUND);
  }

}
