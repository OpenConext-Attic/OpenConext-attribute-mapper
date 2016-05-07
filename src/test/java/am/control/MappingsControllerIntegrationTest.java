package am.control;

import am.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.ResponseEntity;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

@WebIntegrationTest(randomPort = true, value = {
  "spring.profiles.active=dev",
  "central_idp.metadata_url=classpath:saml/central.idp.metadata.xml",
  "central_idp.entity_id=http://mock-idp",
  "central_idp.certificate=MIICHzCCAYgCCQD7KMJ17XQa7TANBgkqhkiG9w0BAQUFADBUMQswCQYDVQQGEwJOTDEQMA4GA1UECAwHVXRyZWNodDEQMA4GA1UEBwwHVXRyZWNodDEQMA4GA1UECgwHU3VyZm5ldDEPMA0GA1UECwwGQ29uZXh0MB4XDTEyMDMwODA4NTQyNFoXDTEzMDMwODA4NTQyNFowVDELMAkGA1UEBhMCTkwxEDAOBgNVBAgMB1V0cmVjaHQxEDAOBgNVBAcMB1V0cmVjaHQxEDAOBgNVBAoMB1N1cmZuZXQxDzANBgNVBAsMBkNvbmV4dDCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEA2slVe459WUDL4RXxJf5h5t5oUbPkPlFZ9lQysSoS3fnFTdCgzA6FzQzGRDcfRj0HnWBdA1YH+LxBjNcBIJ/nBc7Ssu4e4rMO3MSAV5Ouo3MaGgHqVq6dCD47f52b98df6QTAA3C+7sHqOdiQ0UDCAK0C+qP5LtTcmB8QrJhKmV8CAwEAATANBgkqhkiG9w0BAQUFAAOBgQCvPhO0aSbqX7g7IkR79IFVdJ/P7uSlYFtJ9cMxec85cYLmWL1aVgF5ZFFJqC25blyPJu2GRcSxoVwB3ae8sPCECWwqRQA4AHKIjiW5NgrAGYR++ssTOQR8mcAucEBfNaNdlJoy8GdZIhHZNkGlyHfY8kWS3OWkGzhWSsuRCLl78A==",
  "central_idp.schac_home=http://mock-idp"
})
public class MappingsControllerIntegrationTest extends AbstractIntegrationTest {

  @Value("${central_idp.entity_id}")
  private String centralIdpEntityId;

  @Value("${am.entity_id}")
  private String amEntityId;

  @Test
  public void testSamlInterceptorWithCentralIdp() throws Exception {
    ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/mappings", String.class);

    Matcher matcher = Pattern.compile("name=\"SAMLRequest\" value=\"(.*?)\"").matcher(response.getBody());
    assertTrue(matcher.find());

    String saml = new String(Base64.getDecoder().decode(matcher.group(1)));

    assertTrue(saml.contains("<saml2:Issuer xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">" + amEntityId + "</saml2:Issuer>"));
  }

}
