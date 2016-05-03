package am.control;

import am.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class LandingControllerTest extends AbstractIntegrationTest {

  @Test
  public void testLanding() throws Exception {
    ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/landing", String.class);

    Matcher matcher = Pattern.compile("name=\"SAMLRequest\" value=\"(.*?)\"").matcher(response.getBody());
    assertTrue(matcher.find());

    String saml = new String(Base64.getDecoder().decode(matcher.group(1)));

    assertTrue(saml.contains("<saml2:Issuer xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">http://attribute-mapper.test.surfconext.nl</saml2:Issuer>"));
  }
}
