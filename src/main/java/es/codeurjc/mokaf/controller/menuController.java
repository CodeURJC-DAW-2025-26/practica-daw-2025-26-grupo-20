package es.codeurjc.mokaf.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.mokaf.model.Product;

@Controller
public class menuController {

    @GetMapping("/menu")
    public String showMenu(Model model) {
        
        List<Product> menuItems = new ArrayList<>();
        
        // --- Hot ---
        menuItems.add(new Product("Expreso", "Café negro fuerte y aromático.", "2.50€", "/images/MenuImages/Hot/Expreso.png", "Hot"));
        menuItems.add(new Product("Capuccino", "Expreso con leche vaporizada y espuma.", "3.50€", "/images/MenuImages/Hot/Capuccino.png", "Hot"));
        menuItems.add(new Product("Americano", "Expreso diluido con agua caliente.", "2.80€", "/images/MenuImages/Hot/Americano.png", "Hot"));
        menuItems.add(new Product("Latte", "Expreso con una generosa cantidad de leche vaporizada.", "3.20€", "/images/MenuImages/Hot/Latte.png", "Hot"));

        // --- Cold ---
        menuItems.add(new Product("Iced Latte", "Expreso y leche fría sobre hielo.", "4.00€", "/images/MenuImages/Cold/IcedLatte.png", "Cold"));
        menuItems.add(new Product("Frappe", "Café batido con hielo, refrescante y cremoso.", "4.20€", "/images/MenuImages/Cold/Frappe.png", "Cold"));
        menuItems.add(new Product("Iced Americano", "Expreso y agua fría servido sobre hielo.", "3.00€", "/images/MenuImages/Cold/IcedAmericano.png", "Cold"));
        menuItems.add(new Product("Iced Vietnamese Coffe", "Café con leche condensada y hielo.", "4.50€", "/images/MenuImages/Cold/IcedVietnameseCoffe.png", "Cold"));

        // --- Blended ---
        menuItems.add(new Product("Frapuccino", "Bebida de café mezclada con hielo y sabores.", "4.50€", "/images/MenuImages/Blended/Frapuccino.png", "Blended"));
        menuItems.add(new Product("Chocolate Coffee Blend", "Mezcla de café y chocolate, batido con hielo.", "4.80€", "/images/MenuImages/Blended/ChocolateCoffeeBlend.png", "Blended"));
        menuItems.add(new Product("Hazelnut Coffee Shake", "Batido de café con sirope de avellana.", "4.80€", "/images/MenuImages/Blended/HazelnutCoffeeShake.png", "Blended"));
        menuItems.add(new Product("Vanilla Frappe", "Frappé suave con un toque de vainilla.", "4.60€", "/images/MenuImages/Blended/VanillaFrappe.png", "Blended"));

        // --- Desserts ---
        menuItems.add(new Product("Croissants", "Clásico hojaldre francés.", "2.00€", "/images/MenuImages/Desserts/Croisants.png", "Desserts"));
        menuItems.add(new Product("Chocolate Carrot Cake", "Pastel de zanahoria con cobertura de chocolate.", "3.50€", "/images/MenuImages/Desserts/ChocolateCarrotCake.png", "Desserts"));
        menuItems.add(new Product("Chocolate Cupcake", "Muffin de chocolate con frosting.", "2.80€", "/images/MenuImages/Desserts/ChocolateCupcake.png", "Desserts"));
        menuItems.add(new Product("Chocolate Green Tea Cupcake", "Muffin de té verde con corazón de chocolate.", "3.00€", "/images/MenuImages/Desserts/ChocolateGreenTeaCupcake.png", "Desserts"));
        menuItems.add(new Product("Dulce De Leche Desserts", "Postre cremoso de dulce de leche.", "3.20€", "/images/MenuImages/Desserts/DulceDeLecheDesserts.png", "Desserts"));
        menuItems.add(new Product("Orange Cake", "Bizcocho esponjoso con sabor a naranja.", "3.50€", "/images/MenuImages/Desserts/OrangeCake.png", "Desserts"));
        menuItems.add(new Product("Red Velvet Cupcake", "Clásico muffin Red Velvet con frosting de queso.", "3.00€", "/images/MenuImages/Desserts/RedVelvetCupcake.png", "Desserts"));
        menuItems.add(new Product("Strawberry Cake", "Pastel de fresas con nata.", "3.80€", "/images/MenuImages/Desserts/StrawberryCake.png", "Desserts"));
        menuItems.add(new Product("Vanilla Cupcake", "Muffin de vainilla con frosting.", "2.80€", "/images/MenuImages/Desserts/VanillaCupcake.png", "Desserts"));

        // --- Non-Coffee ---
        menuItems.add(new Product("Herbal Tea", "Infusión relajante sin cafeína.", "3.00€", "/images/MenuImages/Non-Coffee/HerbalTea.png", "Non-Coffee"));
        menuItems.add(new Product("Chai Tea Latte", "Té negro especiado con leche vaporizada.", "3.80€", "/images/MenuImages/Non-Coffee/ChatTeaLatte.png", "Non-Coffee"));
        menuItems.add(new Product("Golden Milk", "Leche con cúrcuma y especias.", "4.00€", "/images/MenuImages/Non-Coffee/GoldenMilk.png", "Non-Coffee"));
        menuItems.add(new Product("Hot Chocolate", "Chocolate caliente espeso y cremoso.", "3.50€", "/images/MenuImages/Non-Coffee/HotChocolate.png", "Non-Coffee"));
        menuItems.add(new Product("Matcha Latte", "Té verde matcha con leche vaporizada.", "4.20€", "/images/MenuImages/Non-Coffee/MatchaLatte.png", "Non-Coffee"));

        model.addAttribute("title", "Menú");
        model.addAttribute("items", menuItems); // Añadimos la lista al modelo
        model.addAttribute("currentPage", "menu");
        return "menu"; // Devuelve el nombre de la plantilla
    }

    
}
