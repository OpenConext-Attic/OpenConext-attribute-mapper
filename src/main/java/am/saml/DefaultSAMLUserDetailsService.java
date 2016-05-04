package am.saml;

import am.model.User;
import am.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

/**
 * The user can log in with the central IdP and we lookup the user or provision her for the first time based on the
 * unspecified urn which we create from the central IdP schacHome and name.
 * <p>
 * If the user has logged in using SURFConext then we assume an authenticated user in the SecurityContextHolder
 */
@Service
public class DefaultSAMLUserDetailsService implements SAMLUserDetailsService {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultSAMLUserDetailsService.class);

  private final UserRepository userRepository;

  private final String urnFormat = "urn:collab:person:%s:%s";

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

  public Object loadUserBySAML(SAMLCredential credential) {
    String remoteEntityID = credential.getRemoteEntityID();

    if (isCentralIdpAuthnResponse(remoteEntityID)) {
      //TODO check against the real iDEN IdP
      String localUsername = credential.getNameID().getValue();
      String username = String.format(urnFormat, centralIdpSchacHome, localUsername);
    }
    //urn:collab:person:example.com:admin
    String localUsername = credential.getNameID().getValue();
    String username = String.format(urnFormat, centralIdpSchacHome, localUsername);
    User user;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (centralIdpEntityId.equals(remoteEntityID)) {
      user = userRepository.findByUsername(username).orElseGet(() -> parseUser(username, credential));
    } else if (surfConextIdpEntityId.equals(remoteEntityID)) {
      user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException(String.format("User %s not found prior to %s login", username, surfConextIdpEntityId)));

    } else {
      throw new IllegalArgumentException(String.format("Unknown remoteEntityID {}", remoteEntityID));
    }
    user.setAuthorities(user.isMapped() ? createAuthorityList("ROLE_USER", "ROLE_MAPPED") : createAuthorityList("ROLE_USER"));
    return user;
  }

  private User parseUser(String username, SAMLCredential credential) {
    User user = new User();
    user.setUsername(username);
    user.setCentralIdp(centralIdpEntityId);
    //TODO what do we want to save from the user
    LOG.debug("Provisioning {} after successful login", user);
    return userRepository.save(user);
  }

  private User mapUser(User user, SAMLCredential credential) {
    String schacHome = credential.getAttributeAsString("urn:mace:terena.org:attribute-def:schacHomeOrganization");
    user.setInstitution(schacHome);
    user.setUnspecifiedId("");

    return userRepository.save(user);
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
