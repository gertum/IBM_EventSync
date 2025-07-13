package com.ibm.event_sync.routing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    public HomeController(RestTemplateBuilder builder) {
    }

    @Value("${spring.application.name}")
    String appName;

   @GetMapping("/")
   public String homePage(Model model) {
       model.addAttribute("appName", appName);
       return "home";
   }
}
