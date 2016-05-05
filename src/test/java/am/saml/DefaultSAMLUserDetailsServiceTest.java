package am.saml;

import am.model.User;
import am.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.NameID;
import org.opensaml.xml.schema.XSString;
import org.springframework.security.saml.SAMLCredential;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static am.saml.DefaultSAMLUserDetailsService.urnFormat;
import static am.saml.SAMLObjectUtils.buildSAMLObject;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

/**
 * We don't test DefaultSAMLUserDetailsService from the outside, as this would entail posting SAML response to the
 * /saml/SSO endpoint. This is very cumbersome and only adds complexity and no additional value.
 *
 * Dealing with org.opensaml.saml2.core.* objects is also very verbose, but less complex.
 */
public class DefaultSAMLUserDetailsServiceTest {

  private DefaultSAMLUserDetailsService subject;
  private UserRepository userRepository;

  private final String centralIdpEntityId = "http://iden";
  private final String centralIdpSchacHome = "iden.nl";
  private final String surfConextIdpEntityId = "http://surfconext";

  private final String nameID = "john.doe";

  @Before
  public void setUp() throws Exception {
    this.userRepository = mock(UserRepository.class);
    subject = new DefaultSAMLUserDetailsService(userRepository);

    setField(subject, "surfConextIdpEntityId", surfConextIdpEntityId);
    setField(subject, "centralIdpEntityId", centralIdpEntityId);
    setField(subject, "centralIdpSchacHome", centralIdpSchacHome);
  }

  @Test
  public void testLoadExistingNonMappedUserBySAML() throws Exception {
    User user = new User();
    when(userRepository.findByUnspecifiedId(String.format(urnFormat, centralIdpSchacHome, nameID))).thenReturn(Optional.of(user));

    User elevatedUser = (User) subject.loadUserBySAML(samlCredential(centralIdpEntityId));
    assertFalse(elevatedUser.isMapped());
  }

  private SAMLCredential samlCredential(String entityID) {
    NameID nameId = buildSAMLObject(NameID.class, NameID.DEFAULT_ELEMENT_NAME);
    nameId.setValue(nameID);

    Assertion assertion = buildSAMLObject(Assertion.class, Assertion.DEFAULT_ELEMENT_NAME);

    Attribute attribute = buildSAMLObject(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
    attribute.setName("urn:mace:dir:attribute-def:eduPersonAffiliation");

    XSString xsString = buildSAMLObject(XSString.class, XSString.TYPE_NAME);
    xsString.setValue("teacher");

    attribute.getAttributeValues().add(xsString);
    List<Attribute> attributes = Collections.singletonList(attribute);

    return new SAMLCredential(nameId, assertion, entityID, attributes, "N/A");

  }

}
