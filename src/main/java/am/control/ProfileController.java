package am.control;

import am.domain.User;
import am.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Optional;

@Controller
public class ProfileController {

  @Autowired
  private Environment environment;

  @RequestMapping("/profile")
  public String profile(Authentication authentication, ModelMap modelMap) {
    modelMap.put("menu", "profile");
    modelMap.put("user", authentication.getPrincipal());
    modelMap.put("environment", environment.acceptsProfiles("test"));
    return "profile";
  }

  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }
}
