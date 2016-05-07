package am.control;

import am.AbstractIntegrationTest;
import am.PrePopulatedJsonHttpHeaders;
import am.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class ApiControllerTest extends AbstractIntegrationTest {

  @Value("${am.api.name}")
  private String name;

  @Value("${am.api.password}")
  private String password;

  private TestRestTemplate secureRestTemplate;

  private final PrePopulatedJsonHttpHeaders headers = new PrePopulatedJsonHttpHeaders();

  @Before
  public void setUp() throws Exception {
    this.secureRestTemplate = new TestRestTemplate(name, password);
  }

  @Test
  public void testUser() throws Exception {
    ResponseEntity<User> response = getUserResponseEntity("urn:collab:person:idin.nl:5E63A2B5-CB25-42F8-BDE8-F6A5CC25F018", User.class);
    assertEquals(200, response.getStatusCode().value());

    User user = response.getBody();

    assertEquals("jdoe@example.com",user.getEmail());
    assertEquals("researcher, student", user.getAffiliations());
    assertEquals("example.com", user.getInstitution());
    assertTrue(user.isMapped());
    assertTrue(user.isConfirmed());
  }

  @Test
  public void testUserNotFound() throws Exception {
    ResponseEntity<Object> response = getUserResponseEntity("urn:collab:person:idin.nl:bogus", Object.class);

    assertEquals(404, response.getStatusCode().value());
  }

  @Test
  public void testUnauthorized() throws Exception {
    RequestEntity requestEntity = new RequestEntity(headers, HttpMethod.GET, new URI("http://localhost:" + port + "/api/user/bogus"));
    ResponseEntity<Void> response = new TestRestTemplate(name, "bogus").exchange(requestEntity, Void.class);

    assertEquals(403, response.getStatusCode().value());
  }

  @Test
  public void testUrlNotFound() throws Exception {
    RequestEntity requestEntity = new RequestEntity(headers, HttpMethod.GET, new URI("http://localhost:" + port + "/api/bogus"));
    ResponseEntity<Void> response = secureRestTemplate.exchange(requestEntity, Void.class);

    assertEquals(404, response.getStatusCode().value());
  }

  private <T> ResponseEntity<T> getUserResponseEntity(String urn, Class<T> clazz) throws URISyntaxException {
    RequestEntity requestEntity = new RequestEntity(headers, HttpMethod.GET,
      new URI("http://localhost:" + port + "/api/user/" +urn)
    );
    return secureRestTemplate.exchange(requestEntity, clazz);
  }
}
