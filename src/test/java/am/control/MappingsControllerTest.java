package am.control;

import am.domain.User;
import am.mail.MailBox;
import am.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ModelMap;
import org.springframework.util.LinkedMultiValueMap;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class MappingsControllerTest {

  private MappingsController subject ;
  private UserRepository userRepository;
  private MailBox mailBox;

  @Before
  public void setUp() throws Exception {
    userRepository = mock(UserRepository.class);
    mailBox = mock(MailBox.class);

    subject = new MappingsController();

    setField(subject, "userRepository", userRepository);
    setField(subject, "mailBox", mailBox);
  }

  @Test
  public void testIndex() throws Exception {
    String view = subject.index(null, null);
    assertEquals("index", view);
  }

  @Test
  public void testMappings() throws Exception {
    String view = subject.index(new TestingAuthenticationToken(new User(), "N/A"), new ModelMap());
    assertEquals("redirect:/mappings", view);
  }

  @Test
  public void testLanding() throws Exception {
    String view = subject.mappings(new TestingAuthenticationToken(new User(), "N/A"), new ModelMap());
    assertEquals("mappings", view);
  }

  @Test
  public void testConfirmInvalidHash() throws Exception {
    when(userRepository.findByInviteHash("hash")).thenReturn(Optional.empty());

    String view = subject.confirm("hash", new ModelMap());

    assertEquals("404", view);
  }

  @Test
  public void testConfirmValidHash() throws Exception {
    when(userRepository.findByInviteHash("hash")).thenReturn(Optional.of(new User()));

    ModelMap modelMap = new ModelMap();
    String view = subject.confirm("hash", modelMap);
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    assertEquals("mappings", view);
    assertTrue(user.isConfirmed());
    assertEquals(4, modelMap.get("step"));
  }

  @Test
  public void testEmail() throws UnsupportedEncodingException {
    ModelMap modelMap = new ModelMap();
    User user = new User();

    String view = subject.email(new TestingAuthenticationToken(
        user,"N/A","ROLE_USER"),
      new LinkedMultiValueMap(singletonMap("email", singletonList("test@example.org"))),
      modelMap);

    assertEquals("test@example.org", user.getEmail());
    assertNotNull(user.getInviteHash());
    assertEquals("mappings", view);
    assertEquals(3, modelMap.get("step"));
  }
}
