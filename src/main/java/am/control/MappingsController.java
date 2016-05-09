package am.control;

import am.domain.User;
import am.mail.MailBox;
import am.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;


@Controller
public class MappingsController {

  private static final Logger LOG = LoggerFactory.getLogger(MappingsController.class);

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MailBox mailBox;

  @RequestMapping("/")
  public String index(Authentication authentication, ModelMap modelMap) {
    return path(authentication, modelMap, true);
  }

  @RequestMapping("/mappings")
  public String mappings(Authentication authentication, ModelMap modelMap) {
    return path(authentication, modelMap, false);
  }

  @RequestMapping("/confirmation")
  public String confirm(@RequestParam("inviteHash") String inviteHash, ModelMap modelMap) throws UnsupportedEncodingException {
    String decoded = URLDecoder.decode(inviteHash, "UTF-8");
    Optional<User> userOptional = userRepository.findByInviteHash(decoded);
    if (!userOptional.isPresent()) {
      LOG.info("User with inviteHash {} not found", inviteHash);
      return "404";
    }

    User user = userOptional.get();
    user.setConfirmed(true);
    user.setInviteHash(null);
    userRepository.save(user);

    TestingAuthenticationToken authentication = new TestingAuthenticationToken(user, "N/A", "ROLE_USER");
    SecurityContextHolder.getContext().setAuthentication(authentication);
    return path(authentication, modelMap, true);
  }

  @RequestMapping(method = RequestMethod.POST, path = {"/email", "/profile/update"})
  public String email(Authentication authentication, @RequestBody MultiValueMap<String,String> formData, ModelMap modelMap) throws UnsupportedEncodingException {
    User user = (User) authentication.getPrincipal();

    String email = formData.getFirst("email");
    if (email.equals(user.getEmail()) && user.isConfirmed()) {
      // profile/update
      return path(authentication, modelMap, true);
    }

    user.setEmail(email);
    user.setInviteHash(generateInvitationHash());
    user.setConfirmed(false);
    userRepository.save(user);

    mailBox.sendConfirmationMail(user);

    return path(authentication, modelMap, false);
  }

  private String path(Authentication authentication, ModelMap modelMap, boolean redirect) {
    if (authentication == null) {
      return "index";
    }
    User user = (User) authentication.getPrincipal();
    modelMap.put("step", user.isConfirmed() ? 4 : user.getInviteHash() != null ? 3 : user.isMapped() ? 2 : 1);
    modelMap.put("menu", "mappings");
    modelMap.put("user", user);

    return redirect ? "redirect:/mappings" : "mappings";
  }

  protected String generateInvitationHash() throws UnsupportedEncodingException {
    Random secureRandom = new SecureRandom();
    byte[] aesKey = new byte[512];
    secureRandom.nextBytes(aesKey);
    return Base64.getEncoder().encodeToString(aesKey);
  }



}
