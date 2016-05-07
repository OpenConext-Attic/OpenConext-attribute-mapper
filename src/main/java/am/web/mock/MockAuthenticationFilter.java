package am.web.mock;

import am.domain.User;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import java.io.IOException;

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
    switch (step) {
      case "2":
        user.setMapped(true);
        break;
      case "3":
        user.setConfirmed(true);
        break;
      default :
    }
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(user, "N/A", "ROLE_USER"));
    chain.doFilter(request, response);

  }
}
