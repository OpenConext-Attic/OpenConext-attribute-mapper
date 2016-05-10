package am.web.mock;

import am.domain.User;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import java.io.IOException;
import java.util.Date;

public class MockAuthenticationFilter extends GenericFilterBean {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    String step = request.getParameter("step");
    if (step == null) {
      chain.doFilter(request, response);
      return;
    }
    User user = new User();
    user.setUsername("J.Doe");
    user.setCentralIdp("http://central-idp");
    user.setGrantedAuthorities("ROLE_USER");
    user.setCreated(new Date());
    switch (step) {
      case "2":
        user.setMapped(true);
        break;
      case "3":
        user.setInviteHash("hash");
        user.setEmail("local@test.org");
        break;
      case "4":
        user.setInviteHash(null);
        user.setConfirmed(true);
        user.setEmail("local@test.org");
        user.setInstitution("example.com");
        break;
      default :
    }
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(user, "N/A", "ROLE_USER"));
    chain.doFilter(request, response);
  }
}
