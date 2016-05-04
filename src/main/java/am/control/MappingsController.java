package am.control;

import am.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MappingsController {

  @RequestMapping("/")
  public String index(Authentication authentication, ModelMap modelMap) {
    return path(authentication, modelMap);
  }

  @RequestMapping({"/mappings"})
  public String landing(Authentication authentication, ModelMap modelMap) {
    return path(authentication,modelMap);
  }

  private String path(Authentication authentication, ModelMap modelMap) {
    if (authentication == null) {
      return "index";
    }
    modelMap.put("menu", "mappings");
    return "mappings";
  }
}
