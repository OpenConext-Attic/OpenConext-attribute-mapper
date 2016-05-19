package am.control;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.ui.ModelMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ProfileControllerTest {

  private ProfileController subject = new ProfileController();

  @Before
  public void before() throws NoSuchFieldException {
    subject.setEnvironment(mock(Environment.class));
  }

  @Test
  public void testProfile() throws Exception {
    String view = subject.profile(new TestingAuthenticationToken("principal", "N/A"), new ModelMap());
    assertEquals("profile", view);
  }
}
