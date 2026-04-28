import { Link } from "react-router";
import "../app_footer.css";

export default function Footer() {
  return (
    <footer className="site-footer">
      <div className="footer-container">
        {/* Brand Column */}
        <div className="footer-brand">
          <Link to="/" className="footer-logo">
            <i className="fas fa-mug-hot"></i>
            <span>Mokaf</span>
          </Link>
          <p className="footer-description">
            "El arte del café en su máxima expresión. Disfruta de una experiencia sensorial única con nuestros blends exclusivos en Madrid."
          </p>
          <div className="social-icons">
            {['instagram', 'facebook-f', 'twitter', 'tiktok'].map((social, idx) => (
              <a key={idx} href="#" className="social-icon">
                <i className={`fab fa-${social}`}></i>
              </a>
            ))}
          </div>
        </div>

        {/* Links Column */}
        <div className="footer-links">
          <h4 className="footer-title">Explora</h4>
          <nav className="footer-nav">
            {[
              { to: "/", label: "Inicio" },
              { to: "/menu", label: "Menú" },
              { to: "/about", label: "Nosotros" },
              { to: "/branches", label: "Sucursales" },
              { to: "/contact", label: "Contacto" }
            ].map(link => (
              <Link key={link.label} to={link.to} className="footer-link">
                <span className="footer-link-dot"></span>
                {link.label}
              </Link>
            ))}
          </nav>
        </div>

        {/* Contact Column */}
        <div className="footer-contact">
          <h4 className="footer-title">Contacto</h4>
          <ul className="contact-list">
            <li className="contact-item">
              <i className="fas fa-location-dot"></i>
              <span>Av. Principal 123, <br/>Distrito Elegante, Madrid</span>
            </li>
            <li className="contact-item">
              <i className="fas fa-phone-volume"></i>
              <span>+34 910 123 456</span>
            </li>
            <li className="contact-item">
              <i className="fas fa-envelope"></i>
              <span>info@mokaf.com</span>
            </li>
          </ul>
        </div>

        {/* Hours Column */}
        <div className="footer-hours">
          <h4 className="footer-title">Tiempos</h4>
          <div className="hours-list">
            {[
              { day: 'Lunes - Viernes', time: '07:00 — 20:00' },
              { day: 'Sábados', time: '08:00 — 22:00' },
              { day: 'Domingos', time: '08:00 — 18:00' },
              { day: 'Festivos', time: '09:00 — 16:00' }
            ].map(item => (
              <div key={item.day} className="hours-item">
                <span className="hours-day">{item.day}</span>
                <span className="hours-time">{item.time}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="footer-bottom">
        <div className="footer-bottom-container">
          <p className="copyright">
            &copy; 2026 Mokaf Specialty — Destilando Pasión
          </p>
          <div className="footer-legal">
            <a href="#" className="legal-link">Cookies</a>
            <a href="#" className="legal-link">Privacidad</a>
            <a href="#" className="legal-link">Términos</a>
          </div>
        </div>
      </div>
    </footer>
  );
}