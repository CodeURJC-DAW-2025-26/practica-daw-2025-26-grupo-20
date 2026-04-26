import { type RouteConfig, index, route, layout } from "@react-router/dev/routes";

export default [
  // public routes
  index("routes/home.tsx"),
  route("login", "routes/login.tsx"),
  route("register", "routes/register.tsx"),
  route("menu", "routes/menu.tsx"),
  route("about", "routes/about.tsx"),
  route("branches", "routes/branches.tsx"),
  route("contact", "routes/contact.tsx"),
  route("product/:id", "routes/product_detail.tsx"),

  // protected routes (user authentication required)
  layout("components/ProtectedLayout.tsx", [
    route("cart", "routes/cart.tsx"),
    route("orders", "routes/orders.tsx"),
    route("profile", "routes/profile.tsx"),
  ]),

  // Only for admin users
  layout("components/AdminLayout.tsx", [
    route("profile-admin", "routes/profile_admin.tsx"),
    route("statistics", "routes/statistics.tsx"),
    route("gestion-menu", "routes/gestion_menu.tsx"),
    route("gestion-usuarios", "routes/gestion_usuarios.tsx"),
  ]),
] satisfies RouteConfig;
