package es.codeurjc.mokaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    //ReDirect only routes of navigation (without extension) to index.html
    //tHIS AVOIDS INFINITE LOOPS WITH index.html ITSELF OR .js/.css FILES
    @GetMapping({"/new/{path:[^\\.]*}", "/new", "/new/"})
    public String forwardToSpa() {
        return "forward:/new/index.html";
    }
}
