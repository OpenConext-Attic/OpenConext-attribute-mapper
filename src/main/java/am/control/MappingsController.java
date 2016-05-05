package am.control;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MappingsController {

  @RequestMapping({"/", "/mappings"})
  public String mappings(Authentication authentication, ModelMap modelMap) {
    return path(authentication, modelMap);
  }

  private String path(Authentication authentication, ModelMap modelMap) {
    if (authentication == null) {
      return "index";
    }
    modelMap.put("menu", "mappings");
    return "mappings";
  }
}
