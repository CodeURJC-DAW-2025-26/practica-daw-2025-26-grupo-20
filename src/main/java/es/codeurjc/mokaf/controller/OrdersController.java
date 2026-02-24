package es.codeurjc.mokaf.controller;

import es.codeurjc.mokaf.model.Order;
import es.codeurjc.mokaf.service.OrdersService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

@Controller
public class OrdersController {

    private final OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @GetMapping("/orders")
    public String showOrders(Model model,
                            @RequestParam(defaultValue = "0") int page) {

        int pageSize = 3; // Pedidos por página
        Page<Order> ordersPage = ordersService.getPaidOrders(page, pageSize);

        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());

        // Variables para paginación
        boolean hasPrev = page > 0;
        boolean hasNext = page < ordersPage.getTotalPages() - 1;

        model.addAttribute("hasPrev", hasPrev);
        model.addAttribute("hasNext", hasNext);
        model.addAttribute("prevPage", page - 1);
        model.addAttribute("nextPage", page + 1);

        // Construir lista de páginas para Mustache
        List<Map<String, Object>> pages = new ArrayList<>();
        for (int i = 0; i < ordersPage.getTotalPages(); i++) {
            Map<String, Object> p = new HashMap<>();
            p.put("number", i);
            p.put("numberPlusOne", i + 1); // Mostrar página empezando en 1
            p.put("isCurrent", i == page);
            pages.add(p);
        }
        model.addAttribute("pages", pages);

        return "orders";
    }
} 