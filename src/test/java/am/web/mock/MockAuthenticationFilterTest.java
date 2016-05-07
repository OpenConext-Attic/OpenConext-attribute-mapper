package am.web.mock;

import am.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.Assert.*;

public class MockAuthenticationFilterTest {

  private MockAuthenticationFilter subject = new MockAuthenticationFilter();

  @Before
  public void setUp() throws Exception {
    SecurityContextHolder.getContext().setAuthentication(null);
  }

  @Test
  public void testDoFilterWithoutStep() throws Exception {
    subject.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockFilterChain());
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void testDoFilterStep1() throws Exception {
    User user = doFilter("1");
    assertFalse(user.isMapped());
  }

  @Test
  public void testDoFilterStep2() throws Exception {
    User user = doFilter("2");
    assertTrue(user.isMapped());
  }

  @Test
  public void testDoFilterStep3() throws Exception {
    User user = doFilter("3");
    assertEquals("hash", user.getInviteHash());
  }

  @Test
  public void testDoFilterStep4() throws Exception {
    User user = doFilter("4");
    assertTrue(user.isConfirmed());
  }

  private User doFilter(String step) throws IOException, ServletException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setParameter("step", step);
    subject.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

}
