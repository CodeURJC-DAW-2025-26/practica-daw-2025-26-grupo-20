package es.codeurjc.mokaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    // Redirige solo rutas de navegación (sin extensión) hacia index.html
    // Esto evita bucles infinitos con el propio index.html o archivos .js/.css
    @GetMapping({"/new/{path:[^\\.]*}", "/new", "/new/"})
    public String forwardToSpa() {
        return "forward:/new/index.html";
    }
}
