import { Link, NavLink, useNavigate } from "react-router";
import { useAuthStore } from "../store/authStore";
import { useCartStore } from "../store/cartStore";
import { API_BASE_URL } from "../config";

export default function Header() {
  const { isLogged, user, logout } = useAuthStore();
  const { itemCount } = useCartStore();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  return (
    <header className="bg-black py-6 border-b border-white/5 transition-all duration-300">
      <div className="container mx-auto px-10 flex items-center justify-between">
        {/* Logo */}
        <Link to="/" className="flex items-center gap-3">
          <i className="fas fa-mug-hot text-[#d4b88d] text-3xl"></i>
          <span className="text-3xl font-serif font-bold text-[#d4b88d] tracking-tight">Mokaf</span>
        </Link>

        {/* Navigation */}
        <nav className="hidden lg:flex items-center gap-12">
          {[
            { to: "/", label: "Inicio" },
            { to: "/menu", label: "Menú" },
            { to: "/about", label: "Nosotros" },
            { to: "/branches", label: "Sucursales" },
            { to: "/contact", label: "Contacto" },
          ].map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              className={({ isActive }) =>
                `text-[12px] font-bold uppercase tracking-[0.25em] transition-all px-4 py-2 rounded-lg hover:text-[#d4b88d] hover:bg-white/[0.05] ${
                  isActive ? "text-[#d4b88d] bg-white/[0.03]" : "text-stone-300"
                }`
              }
            >
              {link.label}
            </NavLink>
          ))}

          {/* Gestión — solo para ADMIN */}
          {isLogged && user?.role === "ADMIN" && (
            <NavLink
              to="/gestion-menu"
              className={({ isActive }) =>
                `text-[12px] font-bold uppercase tracking-[0.25em] transition-all px-4 py-2 rounded-lg ${
                  isActive ? "text-amber-400 bg-white/[0.05]" : "text-amber-500"
                } hover:text-amber-400 hover:bg-white/[0.05] border border-amber-500/30`
              }
            >
              Gestión
            </NavLink>
          )}

          {/* Cart Button */}
          <Link
            to="/cart"
            className="relative text-[12px] font-bold uppercase tracking-[0.25em] transition-all px-4 py-2 rounded-lg text-stone-300 hover:text-[#d4b88d] hover:bg-white/[0.05]"
          >
            <i className="fas fa-shopping-cart text-xl"></i>
            <span className="absolute top-0 right-0 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
              {itemCount}
            </span>
          </Link>
        </nav>

        {/* Account Button */}
        <div className="flex items-center gap-6">
          {isLogged ? (
            <div className="flex items-center gap-5">
              {/* Perfil — ruta dinámica según rol */}
              <Link
                to={user?.role === "ADMIN" ? "/profile-admin" : "/profile"}
                className="flex items-center gap-3 group"
              >
                <div className="text-right flex flex-col items-end">
                  <span className="text-[11px] text-stone-300 font-bold uppercase tracking-widest leading-none mb-1">
                    {user?.name}
                  </span>
                  <span className="text-[12px] text-[#d4b88d] font-serif italic">Mi Perfil</span>
                </div>
                <div className="w-12 h-12 rounded-full border-2 border-[#d4b88d]/40 flex items-center justify-center text-[#d4b88d] group-hover:border-[#d4b88d] group-hover:bg-[#d4b88d] group-hover:text-black transition-all duration-500 shadow-xl overflow-hidden">
                  {user?.profileImageUrl ? (
                    <img src={user.profileImageUrl} alt="user" className="w-full h-full object-cover" />
                  ) : (
                    <i className="fas fa-user text-[14px]"></i>
                  )}
                </div>
              </Link>
              <button
                onClick={handleLogout}
                className="w-10 h-10 flex items-center justify-center text-stone-400 hover:text-white hover:bg-white/5 rounded-full transition-all"
              >
                <i className="fas fa-sign-out-alt text-[14px]"></i>
              </button>
            </div>
          ) : (
            <Link
              to="/login"
              className="bg-transparent border border-[#d4b88d]/50 px-8 py-3 rounded-full flex items-center gap-3 text-[#d4b88d] hover:bg-[#d4b88d] hover:text-black transition-all duration-700 font-bold text-[11px] uppercase tracking-widest shadow-lg"
            >
              <i className="fas fa-user text-[10px]"></i>
              <span>Ingresar</span>
            </Link>
          )}
        </div>
      </div>
    </header>
  );
}