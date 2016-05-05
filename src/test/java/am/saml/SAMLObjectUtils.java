package am.saml;

import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObjectBuilderFactory;

import javax.xml.namespace.QName;

public class SAMLObjectUtils {

  static {
    try {
      DefaultBootstrap.bootstrap();
    } catch (ConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  private static final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

  @SuppressWarnings({"unused", "unchecked"})
  public static <T> T buildSAMLObject(final Class<T> objectClass, QName qName) {
    return (T) builderFactory.getBuilder(qName).buildObject(qName);
  }
}
