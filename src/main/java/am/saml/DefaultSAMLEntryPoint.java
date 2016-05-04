package am.saml;

import am.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.security.saml.SAMLConstants.*;

public class DefaultSAMLEntryPoint extends SAMLEntryPoint {

  private final String centralIdpEntityId;
  private final String ebEntityId;

  public DefaultSAMLEntryPoint(String centralIdpEntityId, String ebEntityId) {
    this.centralIdpEntityId = centralIdpEntityId;
    this.ebEntityId = ebEntityId;
  }

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof User) {
      request.setAttribute(PEER_ENTITY_ID, ebEntityId);
    } else {
      request.setAttribute(PEER_ENTITY_ID, centralIdpEntityId);
    }
    super.commence(request, response, e);
  }
}
