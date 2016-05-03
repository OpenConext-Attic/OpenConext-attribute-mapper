package am.saml;

import am.AbstractIntegrationTest;
import org.junit.Test;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;

import static org.junit.Assert.*;

public class ResourceMetadataProviderTest extends AbstractIntegrationTest {

  @Autowired
  @Qualifier("central-idp")
  private MetadataProvider metadataProvider;

  @Test
  public void test() throws MetadataProviderException, XMLParserException, ConfigurationException {
    EntityDescriptor entityDescriptor = (EntityDescriptor) metadataProvider.getMetadata();
    String entityID = entityDescriptor.getEntityID();
    assertEquals("http://mock-idp", entityID);
  }

}
