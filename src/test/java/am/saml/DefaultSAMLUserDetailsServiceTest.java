package am.saml;

import am.domain.User;
import am.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.NameID;
import org.opensaml.xml.schema.XSString;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLCredential;

import java.util.List;
import java.util.Optional;

import static am.saml.DefaultSAMLUserDetailsService.urnFormat;
import static am.saml.SAMLObjectUtils.buildSAMLObject;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

/**
 * We don't test DefaultSAMLUserDetailsService from the outside, as this would entail posting SAML response to the
 * /saml/SSO endpoint. This is very cumbersome and only adds complexity and no additional value.
 * <p>
 * Dealing with org.opensaml.saml2.core.* objects is also very verbose, but less complex.
 */
public class DefaultSAMLUserDetailsServiceTest {

  private DefaultSAMLUserDetailsService subject;
  private UserRepository userRepository;

  private final String centralIdpEntityId = "http://idin";
  private final String centralIdpSchacHome = "idin.nl";
  private final String surfConextIdpEntityId = "http://surfconext";

  private final String nameID = "john.doe";
  private final String unSpecifiedId = String.format(urnFormat, centralIdpSchacHome, nameID);

  @Before
  public void setUp() throws Exception {
    this.userRepository = mock(UserRepository.class);
    subject = new DefaultSAMLUserDetailsService(userRepository);

    setField(subject, "surfConextIdpEntityId", surfConextIdpEntityId);
    setField(subject, "centralIdpEntityId", centralIdpEntityId);
    setField(subject, "centralIdpSchacHome", centralIdpSchacHome);
  }

  @Test
  public void testProvisionUserAfterCentralIdpLogin() throws Exception {
    when(userRepository.findByUnspecifiedId(unSpecifiedId)).thenReturn(Optional.empty());
    when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());

    User user = subject.loadUserBySAML(samlCredential(centralIdpEntityId, false));

    assertEquals("urn:collab:person:idin.nl:john.doe", user.getUnspecifiedId());
    assertEquals(singletonList(new SimpleGrantedAuthority("ROLE_USER")), user.getAuthorities());
    assertEquals("http://idin", user.getCentralIdp());
    assertEquals("Doe", user.getUsername());
    assertFalse(user.isMapped());
  }

  @Test
  public void testLoadExistingUserAfterCentralIdpLogin() throws Exception {
    when(userRepository.findByUnspecifiedId(unSpecifiedId)).thenReturn(Optional.of(user()));
    when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());

    User user = subject.loadUserBySAML(samlCredential(centralIdpEntityId));

    assertFalse(user.isMapped());
    assertEquals(nameID, user.getUsername());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnrecognisedRemote() {
    subject.loadUserBySAML(samlCredential("bogus"));
  }

  @Test
  public void testMapUserAfterSurfConextLogin() throws Exception {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(user(), "N/A", "ROLE_USER"));
    when(userRepository.findByUnspecifiedId(unSpecifiedId))
      .thenReturn(Optional.of(user()));
    when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());

    User user = subject.loadUserBySAML(samlCredential(surfConextIdpEntityId));

    assertTrue(user.isMapped());
    assertEquals("teacher", user.getAffiliations());
    assertEquals("example.com", user.getInstitution());
    assertEquals(singletonList(new SimpleGrantedAuthority("ROLE_MAPPED")), user.getAuthorities());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnknownUserAfterSurfConextLogin() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(user(), "N/A", "ROLE_USER"));
    when(userRepository.findByUnspecifiedId(unSpecifiedId))
      .thenReturn(Optional.empty());

    subject.loadUserBySAML(samlCredential(surfConextIdpEntityId));
  }

  private SAMLCredential samlCredential(String entityID) {
    return samlCredential(entityID, true);
  }

  private SAMLCredential samlCredential(String entityID, boolean useBankName) {
    NameID nameId = buildSAMLObject(NameID.class, NameID.DEFAULT_ELEMENT_NAME);
    nameId.setValue(nameID);

    Assertion assertion = buildSAMLObject(Assertion.class, Assertion.DEFAULT_ELEMENT_NAME);

    String name = useBankName ? "urn:nl:bvn:bankid:1.0:consumer.preferredlastname" : "urn:mace:dir:attribute-def:sn";

    List<Attribute> attributes = asList(
      attribute("urn:mace:dir:attribute-def:eduPersonAffiliation", "teacher"),
      attribute("urn:mace:terena.org:attribute-def:schacHomeOrganization", "example.com"),
      attribute("urn:nl:bvn:bankid:1.0:consumer.initials", useBankName ? "M." : ""),
      attribute(name, "Doe")
    );

    return new SAMLCredential(nameId, assertion, entityID, attributes, "N/A");
  }

  private Attribute attribute(String name, String value) {
    Attribute attribute = buildSAMLObject(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
    attribute.setName(name);

    XSString xsString = buildSAMLObject(XSString.class, XSString.TYPE_NAME);
    xsString.setValue(value);

    attribute.getAttributeValues().add(xsString);
    return attribute;
  }

  private User user() {
    User user = new User();
    user.setUsername(nameID);
    user.setUnspecifiedId(unSpecifiedId);
    return user;
  }

}
