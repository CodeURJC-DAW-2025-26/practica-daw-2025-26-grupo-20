package es.codeurjc.mokaf.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.model.Category;

@Controller
public class MenuController {

    @GetMapping("/menu")
    public String showMenu(Model model) {

        List<Product> menuItems = new ArrayList<>();

        // --- Hot ---
        menuItems.add(new Product("Expreso", "Café negro fuerte y aromático.", null, new BigDecimal("2.50"), Category.HOT));
        menuItems.add(new Product("Capuccino", "Expreso con leche vaporizada y espuma.", null, new BigDecimal("3.50"), Category.HOT));
        menuItems.add(new Product("Americano", "Expreso diluido con agua caliente.", null, new BigDecimal("2.80"), Category.HOT));
        menuItems.add(new Product("Latte", "Expreso con una generosa cantidad de leche vaporizada.", null, new BigDecimal("3.20"), Category.HOT));

        // --- Cold ---
        menuItems.add(new Product("Iced Latte", "Expreso y leche fría sobre hielo.", null, new BigDecimal("4.00"), Category.COLD));
        menuItems.add(new Product("Frappe", "Café batido con hielo, refrescante y cremoso.", null, new BigDecimal("4.20"), Category.COLD));
        menuItems.add(new Product("Iced Americano", "Expreso y agua fría servido sobre hielo.", null, new BigDecimal("3.00"), Category.COLD));
        menuItems.add(new Product("Iced Vietnamese Coffe", "Café con leche condensada y hielo.", null, new BigDecimal("4.50"), Category.COLD));

        // --- Blended ---
        menuItems.add(new Product("Frapuccino", "Bebida de café mezclada con hielo y sabores.", null, new BigDecimal("4.50"), Category.BLENDED));
        menuItems.add(new Product("Chocolate Coffee Blend", "Mezcla de café y chocolate, batido con hielo.", null, new BigDecimal("4.80"), Category.BLENDED));
        menuItems.add(new Product("Hazelnut Coffee Shake", "Batido de café con sirope de avellana.", null, new BigDecimal("4.80"), Category.BLENDED));
        menuItems.add(new Product("Vanilla Frappe", "Frappé suave con un toque de vainilla.", null, new BigDecimal("4.60"), Category.BLENDED));

        // --- Desserts ---
        menuItems.add(new Product("Croissants", "Clásico hojaldre francés.", null, new BigDecimal("2.00"), Category.DESSERTS));
        menuItems.add(new Product("Chocolate Carrot Cake", "Pastel de zanahoria con cobertura de chocolate.", null, new BigDecimal("3.50"), Category.DESSERTS));
        menuItems.add(new Product("Chocolate Cupcake", "Muffin de chocolate con frosting.", null, new BigDecimal("2.80"), Category.DESSERTS));
        menuItems.add(new Product("Chocolate Green Tea Cupcake", "Muffin de té verde con corazón de chocolate.", null, new BigDecimal("3.00"), Category.DESSERTS));
        menuItems.add(new Product("Dulce De Leche Desserts", "Postre cremoso de dulce de leche.", null, new BigDecimal("3.20"), Category.DESSERTS));
        menuItems.add(new Product("Orange Cake", "Bizcocho esponjoso con sabor a naranja.", null, new BigDecimal("3.50"), Category.DESSERTS));
        menuItems.add(new Product("Red Velvet Cupcake", "Clásico muffin Red Velvet con frosting de queso.", null, new BigDecimal("3.00"), Category.DESSERTS));
        menuItems.add(new Product("Strawberry Cake", "Pastel de fresas con nata.", null, new BigDecimal("3.80"), Category.DESSERTS));
        menuItems.add(new Product("Vanilla Cupcake", "Muffin de vainilla con frosting.", null, new BigDecimal("2.80"), Category.DESSERTS));

        // --- Non-Coffee ---
        menuItems.add(new Product("Herbal Tea", "Infusión relajante sin cafeína.", null, new BigDecimal("3.00"), Category.NON_COFFEE));
        menuItems.add(new Product("Chai Tea Latte", "Té negro especiado con leche vaporizada.", null, new BigDecimal("3.80"), Category.NON_COFFEE));
        menuItems.add(new Product("Golden Milk", "Leche con cúrcuma y especias.", null, new BigDecimal("4.00"), Category.NON_COFFEE));
        menuItems.add(new Product("Hot Chocolate", "Chocolate caliente espeso y cremoso.", null, new BigDecimal("3.50"), Category.NON_COFFEE));
        menuItems.add(new Product("Matcha Latte", "Té verde matcha con leche vaporizada.", null, new BigDecimal("4.20"), Category.NON_COFFEE));

        model.addAttribute("title", "Menú");
        model.addAttribute("items", menuItems);
        model.addAttribute("currentPage", "menu");
        return "menu";
    }
}
