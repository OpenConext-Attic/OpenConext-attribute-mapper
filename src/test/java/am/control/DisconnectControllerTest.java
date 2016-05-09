package am.control;

import am.domain.User;
import am.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class DisconnectControllerTest {

  private DisconnectController subject ;
  private UserRepository userRepository;

  @Before
  public void setUp() throws Exception {
    userRepository = mock(UserRepository.class);

    subject = new DisconnectController();

    setField(subject, "userRepository", userRepository);
  }


  @Test
  public void testIndex() throws Exception {
    assertEquals("disconnect", subject.index());
  }

  @Test
  public void testDisconnectWithNoValidUser() throws Exception {
    String view = disconnect(Optional.empty());

    assertEquals("/", view);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void testDisconnectValidUser() throws Exception {
    String view = disconnect(Optional.of(user()));

    assertEquals("redirect:/mappings", view);
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    assertFalse(user.isConfirmed());
  }

  private String disconnect(Optional<User> userOptional) {
    User user = user();

    when(userRepository.findByUnspecifiedId(user.getUnspecifiedId())).thenReturn(userOptional);

    return subject.disconnect(new TestingAuthenticationToken(user, "N/A", "ROLE_USER"));
  }

  private User user() {
    User user = new User();
    user.setUnspecifiedId("urn");
    user.setConfirmed(true);
    return user;
  }
}
