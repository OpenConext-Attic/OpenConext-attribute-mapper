package am.saml;

import org.junit.Test;
import org.opensaml.common.binding.decoding.URIComparator;

import static org.junit.Assert.*;

public class DefaultURIComparatorTest {

  private URIComparator subject = new DefaultURIComparator();

  @Test
  public void testCompare() throws Exception {
    String https = "https://attribute-mapper.test.surfconext.nl/saml/SSO";
    String http = "http://attribute-mapper.test.surfconext.nl/saml/SSO";
    assertTrue(subject.compare(https, http));
  }
}
