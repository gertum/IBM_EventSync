package com.ibm.event_sync.routing;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/events")
public class EventViewController {

    @GetMapping("/{id}/feedback/")
    public String viewEvent(@PathVariable Long id, Model model) {
        model.addAttribute("event", id);
        return "event-feedback";
    }
}
