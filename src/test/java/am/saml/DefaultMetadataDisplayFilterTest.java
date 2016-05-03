package am.saml;

import am.AbstractIntegrationTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultMetadataDisplayFilterTest extends AbstractIntegrationTest{

  @Test
  public void testProcessMetadataDisplay() throws Exception {
    String metadata = restTemplate.getForObject("http://localhost:" + port + "/saml/metadata", String.class);
    assertTrue(metadata.contains("http://attribute-mapper.test.surfconext.nl"));
  }
}
