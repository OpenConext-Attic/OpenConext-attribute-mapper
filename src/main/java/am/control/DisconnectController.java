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
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;


@Controller
public class DisconnectController {

  private static final Logger LOG = LoggerFactory.getLogger(DisconnectController.class);

  @Autowired
  private UserRepository userRepository;

  @RequestMapping("/disconnect")
  public String index() {
    return "disconnect";
  }

  @RequestMapping("/disconnect/do")
  public String disconnect(Authentication authentication) {
    User principal = (User) authentication.getPrincipal();
    Optional<User> userOptional = userRepository.findByUnspecifiedId(principal.getUnspecifiedId());
    if (!userOptional.isPresent()) {
      SecurityContextHolder.getContext().setAuthentication(null);
      return "/";
    }
    User user = userOptional.get();
    user.setInstitution(null);
    user.setConfirmed(false);
    user.setMapped(false);
    userRepository.save(user);

    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(user, "N/A", "ROLE_USER"));

    return "redirect:/mappings";
  }
}
