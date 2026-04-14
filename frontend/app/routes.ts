import { type RouteConfig, index, route } from "@react-router/dev/routes";

export default [
    index("routes/home.tsx"),
    route("login", "routes/login.tsx"),
    route("register", "routes/register.tsx"),
    route("menu", "routes/menu.tsx"),
    route("cart", "routes/cart.tsx"),
    route("about", "routes/about.tsx"),
    route("branches", "routes/branches.tsx"),
    route("contact", "routes/contact.tsx"),
    route("product/:id", "routes/product_detail.tsx"),
    route("orders", "routes/orders.tsx"),
    route("profile", "routes/profile.tsx"),
    route("statistics", "routes/statistics.tsx"),
    route("gestion-menu", "routes/gestion_menu.tsx"),
] satisfies RouteConfig;
