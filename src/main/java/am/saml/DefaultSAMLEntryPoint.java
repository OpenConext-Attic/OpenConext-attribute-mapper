package am.saml;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml.SAMLEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefaultSAMLEntryPoint extends SAMLEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
    request.setAttribute(org.springframework.security.saml.SAMLConstants.PEER_ENTITY_ID, "http://mock-idp");
    super.commence(request, response, e);
  }
}
