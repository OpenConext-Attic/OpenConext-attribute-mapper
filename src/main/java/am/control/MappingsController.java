package am.control;

import am.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MappingsController {

  @RequestMapping({"/"})
  public String index(Authentication authentication, ModelMap modelMap) {
    return path(authentication, modelMap, true);
  }

  @RequestMapping({"/mappings"})
  public String mappings(Authentication authentication, ModelMap modelMap) {
    return path(authentication, modelMap, false);
  }

  private String path(Authentication authentication, ModelMap modelMap, boolean redirect) {
    if (authentication == null) {
      return "index";
    }
    if (redirect) {
      return "redirect:/mappings";
    }
    User user = (User) authentication.getPrincipal();
    modelMap.put("step", user.isConfirmed() ? 3 : user.isMapped() ? 2 : 1);
    modelMap.put("menu", "mappings");
    return "mappings";
  }
}
