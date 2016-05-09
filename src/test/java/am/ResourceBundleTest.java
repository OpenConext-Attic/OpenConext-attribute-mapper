package am;


import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class ResourceBundleTest {

  @Test
  public void testResourceBundle() throws IOException {
    List<String> nlKeys = properties("messages_nl.properties");
    List<String> enKeys = properties("messages.properties");
    assertEquals(nlKeys, enKeys);
  }

  @SuppressWarnings("unchecked")
  private List<String> properties(String path) throws IOException {
    Properties properties = new Properties();
    properties.load(new ClassPathResource(path).getInputStream());
    List<String> sorted = new ArrayList(properties.keySet());
    Collections.sort(sorted);
    return sorted;
  }
}
