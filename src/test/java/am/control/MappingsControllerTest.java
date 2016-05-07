package am.control;

import am.domain.User;
import org.junit.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.ui.ModelMap;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;

import static org.junit.Assert.*;

public class MappingsControllerTest {

  private MappingsController subject = new MappingsController();

  @Test
  public void testIndex() throws Exception {
    String view = subject.index(null, null);
    assertEquals("index", view);
  }

  @Test
  public void testMappings() throws Exception {
    String view = subject.index(new TestingAuthenticationToken(new User(), "N/A"), new ModelMap());
    assertEquals("redirect:/mappings", view);
  }

  @Test
  public void testLanding() throws Exception {
    String view = subject.mappings(new TestingAuthenticationToken(new User(), "N/A"), new ModelMap());
    assertEquals("mappings", view);
  }

  @Test
  public void testInviteHash() throws Exception {
    String s = "8+10=";
    System.out.println(URLEncoder.encode(s, "UTF-8"));
    System.out.println(URLDecoder.decode(s, "UTF-8"));
    System.out.println(" ");

    String replaced = s.replaceAll("\\+","%2B");
    System.out.println(replaced);
    System.out.println(URLEncoder.encode(replaced, "UTF-8"));
    System.out.println(URLDecoder.decode(replaced, "UTF-8"));

  }
}
