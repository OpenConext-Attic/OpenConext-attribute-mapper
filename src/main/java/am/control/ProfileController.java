package am.control;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProfileController {

  @RequestMapping({"/profile"})
  public String profile(Authentication authentication, ModelMap modelMap) {
    modelMap.put("menu", "profile");
    modelMap.put("user", authentication.getPrincipal());
    return "profile";
  }

}
