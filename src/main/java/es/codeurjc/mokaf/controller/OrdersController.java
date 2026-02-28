package es.codeurjc.mokaf.controller;

import es.codeurjc.mokaf.model.Order;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.OrdersService;
import es.codeurjc.mokaf.service.UserService;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OrdersController {

    private final OrdersService ordersService;
    private final UserService userService;

    public OrdersController(OrdersService ordersService, UserService userService) {
        this.ordersService = ordersService;
        this.userService = userService;
    }

    @GetMapping("/orders")
    public String showOrders(Authentication authentication,
            Model model,
            @RequestParam(defaultValue = "0") int page) {

        User user = getCurrentUser(authentication);
        if (user == null) {
            return "redirect:/login";
        }

        int pageSize = 3; // order for page
        Page<Order> ordersPage;

        if (user.getRole() == User.Role.ADMIN) {
            // Admin see all orders
            ordersPage = ordersService.getPaidOrders(page, pageSize);
            model.addAttribute("isAdmin", true);
        } else {
            // User only see their own orders
            ordersPage = ordersService.getPaidOrdersByUser(user.getId(), page, pageSize);
            model.addAttribute("isAdmin", false);
        }

        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());

        // Vars for pagination
        boolean hasPrev = page > 0;
        boolean hasNext = page < ordersPage.getTotalPages() - 1;

        model.addAttribute("hasPrev", hasPrev);
        model.addAttribute("hasNext", hasNext);
        model.addAttribute("prevPage", page - 1);
        model.addAttribute("nextPage", page + 1);

        // Building list of pages with mustache
        List<Map<String, Object>> pages = new ArrayList<>();
        for (int i = 0; i < ordersPage.getTotalPages(); i++) {
            Map<String, Object> p = new HashMap<>();
            p.put("number", i);
            p.put("numberPlusOne", i + 1); // showing page starting with 1
            p.put("isCurrent", i == page);
            pages.add(p);
        }
        model.addAttribute("pages", pages);

        return "orders";
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }

        if (principal instanceof String) {
            String email = (String) principal;
            return userService.findByEmail(email).orElse(null);
        }

        return null;
    }
}