import { Link, NavLink, useNavigate } from "react-router";
import { useAuthStore } from "../store/authStore";

export default function Header() {
  const { isLogged, user, logout } = useAuthStore();
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
              className={({ isActive }) => `text-[11px] font-bold uppercase tracking-[0.2em] transition-all hover:text-[#d4b88d] ${isActive ? 'text-[#d4b88d]' : 'text-stone-400'}`}
            >
              {link.label}
            </NavLink>
          ))}
        </nav>

        {/* Account Button */}
        <div className="flex items-center gap-6">
          {isLogged ? (
             <div className="flex items-center gap-5">
                <Link to="/profile" className="flex items-center gap-3 group">
                   <div className="text-right flex flex-col items-end">
                      <span className="text-[10px] text-stone-500 font-bold uppercase tracking-widest">{user?.name}</span>
                      <span className="text-[11px] text-[#d4b88d] font-serif italic">Mi Perfil</span>
                   </div>
                   <div className="w-12 h-12 rounded-full border border-[#d4b88d]/30 flex items-center justify-center text-[#d4b88d] group-hover:bg-[#d4b88d] group-hover:text-black transition-all duration-500 shadow-xl overflow-hidden">
                      {user?.profileImageUrl ? (
                        <img src={user.profileImageUrl} alt="user" className="w-full h-full object-cover" />
                      ) : (
                        <i className="fas fa-user text-[13px]"></i>
                      )}
                   </div>
                </Link>
                <button onClick={handleLogout} className="w-10 h-10 flex items-center justify-center text-stone-600 hover:text-white transition-colors">
                   <i className="fas fa-sign-out-alt text-[11px]"></i>
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
