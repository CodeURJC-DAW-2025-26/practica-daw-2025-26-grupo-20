import { Link, NavLink, useNavigate } from "react-router";
import { useAuthStore } from "../store/authStore";
import { useCartStore } from "../store/cartStore";
import { useEffect } from "react";
import "../app_header.css";

export default function Header() {
  const { isLogged, user, logout } = useAuthStore();
  const { itemCount, updateItemCount } = useCartStore();
  const navigate = useNavigate();

  useEffect(() => {
    if (isLogged) {
      updateItemCount();
    }
  }, [isLogged, updateItemCount]);

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  return (
    <header className="site-header">
      <div className="container">
        {/* Logo */}
        <Link to="/" className="logo">
          <i className="fas fa-mug-hot"></i>
          <span>Mokaf</span>
        </Link>

        {/* Navigation Desktop */}
        <nav className="nav-desktop">
          <NavLink to="/" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            Inicio
          </NavLink>
          <NavLink to="/menu" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            Menú
          </NavLink>
          <NavLink to="/about" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            Nosotros
          </NavLink>
          <NavLink to="/branches" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            Sucursales
          </NavLink>
          <NavLink to="/contact" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            Contacto
          </NavLink>
        </nav>

        {/* Account Section */}
        <div className="account-section">
          {isLogged ? (
            <div className="logged-in-buttons">
              <Link to={user?.role === "ADMIN" ? "/profile-admin" : "/profile"} className="account-btn profile-btn">
                <i className="fas fa-user"></i>
                <span>Mi Perfil</span>
              </Link>
              <Link to="/cart" className="account-btn cart-btn">
                <i className="fas fa-shopping-cart"></i>
                <span>Carrito</span>
                {itemCount > 0 && (
                  <span className="cart-badge">{itemCount}</span>
                )}
              </Link>
              <button onClick={handleLogout} className="logout-mini-btn" title="Cerrar Sesión">
                <i className="fas fa-sign-out-alt"></i>
              </button>
            </div>
          ) : (
            <Link to="/login" className="login-button">
              <i className="fas fa-user"></i>
              <span>Ingresar</span>
            </Link>
          )}
        </div>
      </div>
    </header>
  );
}