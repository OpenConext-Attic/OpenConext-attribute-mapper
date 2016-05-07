package am.repository;

import am.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

  Optional<User> findByUnspecifiedId(String unspecifiedId);

  Optional<User> findByInviteHash(String inviteHash);
}
