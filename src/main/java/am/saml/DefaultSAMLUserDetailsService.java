package am.saml;

import am.domain.User;
import am.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * The user can log in with the central IdP and we lookup the user or provision her for the first time based on the
 * unspecified urn which we create from the central IdP schacHome and nameID.
 * <p>
 * If the user has logged in using SURFConext then we assume an authenticated user in the SecurityContextHolder as
 * this is the second step.
 */
@Service
public class DefaultSAMLUserDetailsService implements SAMLUserDetailsService {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultSAMLUserDetailsService.class);
  public static final String urnFormat = "urn:collab:person:%s:%s";

  private final UserRepository userRepository;

  @Value("${surfconext_idp.entity_id}")
  private String surfConextIdpEntityId;

  @Value("${central_idp.entity_id}")
  private String centralIdpEntityId;

  @Value("${central_idp.schac_home}")
  private String centralIdpSchacHome;

  @Autowired
  public DefaultSAMLUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User loadUserBySAML(SAMLCredential credential) {
    String localUsername = credential.getNameID().getValue();
    String urn = format(urnFormat, centralIdpSchacHome, localUsername);

    String remoteEntityID = credential.getRemoteEntityID();

    return isCentralIdpAuthnResponse(remoteEntityID) ? fromCentralIdp(urn, credential) :

      (isSurfConextIdpAuthnResponse(remoteEntityID) ? fromSurfConextIdp(credential) :

        unrecognisedAuthnStatement(remoteEntityID, credential));
  }

  private User fromCentralIdp(String urn, SAMLCredential credential) {
    User user = userRepository.findByUnspecifiedId(urn).orElseGet(
      () -> parseUserFromCentralIdpResponse(urn, credential));
    return user;
  }

  private User fromSurfConextIdp(SAMLCredential credential) {
    String urn = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUnspecifiedId();
    User user = userRepository.findByUnspecifiedId(urn).orElseThrow(
      () -> new IllegalArgumentException(format("User %s not found prior to %s login", urn, centralIdpEntityId)));

    String[] affiliations = credential.getAttributeAsStringArray("urn:mace:dir:attribute-def:eduPersonAffiliation");
    user.setAffiliations(affiliations != null ? String.join(", ", affiliations) : null);

    user.setMapped(true);
    user.addAuthority(new SimpleGrantedAuthority("ROLE_MAPPED"));
    user.setInstitution(credential.getAttributeAsString("urn:mace:terena.org:attribute-def:schacHomeOrganization"));

    return userRepository.save(user);
  }

  private User unrecognisedAuthnStatement(String remoteEntityID, SAMLCredential credential) {
    throw new IllegalArgumentException(format("Unrecognised Authn Response {} {}", remoteEntityID, credential));
  }

  private User parseUserFromCentralIdpResponse(String urn, SAMLCredential credential) {
    User user = new User();
    user.setUnspecifiedId(urn);
    user.setCentralIdp(centralIdpEntityId);

    String sn = credential.getAttributeAsString("urn:mace:dir:attribute-def:sn");
    String preferredLastName = credential.getAttributeAsString("urn:nl:bvn:bankid:1.0:consumer.preferredlastname");
    String lastName = sn != null ? sn : nullSafe(preferredLastName);
    String initials = credential.getAttributeAsString("urn:nl:bvn:bankid:1.0:consumer.initials");

    user.setUsername(nullSafe(initials).concat(lastName));
    user.addAuthority(new SimpleGrantedAuthority("ROLE_USER"));

    LOG.debug("Provisioning {} after successful login", user);

    return userRepository.save(user);
  }

  private String nullSafe(String s) {
    return s != null ? s : "";
  }

  private boolean isCentralIdpAuthnResponse(String remoteEntityID) {
    return centralIdpEntityId.equals(remoteEntityID);
  }

  private boolean isSurfConextIdpAuthnResponse(String remoteEntityID) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return surfConextIdpEntityId.equals(remoteEntityID)
      && authentication != null
      && authentication.getPrincipal() instanceof User;
  }

}
