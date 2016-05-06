package am.control;

import org.junit.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.ui.ModelMap;

import static org.junit.Assert.*;

public class ProfileControllerTest {

  private ProfileController subject = new ProfileController();

  @Test
  public void testProfile() throws Exception {
      String view = subject.profile(new TestingAuthenticationToken("principal", "N/A"), new ModelMap());
      assertEquals("profile", view);
  }
}
