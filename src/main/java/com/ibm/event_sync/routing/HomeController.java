package com.ibm.event_sync.routing;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import com.ibm.event_sync.entity.Event;

@Controller
public class HomeController {

    private final RestTemplate restTemplate;

    public HomeController(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @Value("${spring.application.name}")
    String appName;

   @GetMapping("/")
   public String homePage(Model model) {
       model.addAttribute("appName", appName);

    //    List<Event> events = eventService.getEvents();

        // model.addAttribute("events", events);
       return "home";
   }
}
