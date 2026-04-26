import { Link, NavLink, useNavigate } from "react-router";
import { useAuthStore } from "../store/authStore";
import { useCartStore } from "../store/cartStore";
import "../app_header.css";

export default function Header() {
  const { isLogged, user, logout } = useAuthStore();
  const { itemCount } = useCartStore();
  const navigate = useNavigate();

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

          {/* Cart Button */}
          {isLogged && (
            <Link to="/cart" className="cart-button">
              <i className="fas fa-shopping-cart"></i>
              <span className="cart-count">{itemCount}</span>
            </Link>
          )}
        </nav>

        {/* Account Section */}
        <div className="account-section">
          {isLogged ? (
            <>
              <Link to={user?.role === "ADMIN" ? "/profile-admin" : "/profile"} className="user-profile">
                <div className="user-info">
                  <span className="user-name">{user?.name}</span>
                  <span className="user-label">Mi Perfil</span>
                </div>
                <div className="user-avatar">
                  {user?.profileImageUrl ? (
                    <img src={user.profileImageUrl} alt="user" />
                  ) : (
                    <i className="fas fa-user"></i>
                  )}
                </div>
              </Link>
              <button onClick={handleLogout} className="logout-button">
                <i className="fas fa-sign-out-alt"></i>
              </button>
            </>
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