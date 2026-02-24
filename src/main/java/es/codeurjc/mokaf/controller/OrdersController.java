package es.codeurjc.mokaf.controller;

import es.codeurjc.mokaf.service.OrdersService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrdersController {

    private final OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @GetMapping("/orders")
    public String showOrders(Model model) {

        model.addAttribute("orders", ordersService.getPaidOrders());

        return "orders";
    }
} 