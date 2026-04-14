import { Link } from "react-router";

export default function Footer() {
  return (
    <footer className="bg-[#050404] pt-24 pb-12 text-[#d1d5db] border-t border-[#d4b88d]/10">
      <div className="max-w-7xl mx-auto px-8 grid sm:grid-cols-2 lg:grid-cols-4 gap-20 mb-20 animate-fade-in">
        {/* Brand Column */}
        <div className="space-y-8 flex flex-col items-start">
          <Link to="/" className="flex items-center gap-3 group transition-all duration-700">
             <i className="fas fa-mug-hot text-[#d4b88d] text-3xl transition-transform group-hover:rotate-12 group-hover:scale-110"></i>
             <span className="text-3xl font-serif font-bold text-[#d4b88d] tracking-tighter">Mokaf</span>
          </Link>
          <p className="text-[14px] leading-relaxed text-stone-400 font-light italic">
            "El arte del café en su máxima expresión. Disfruta de una experiencia sensorial única con nuestros blends exclusivos en Madrid."
          </p>
          <div className="flex gap-4">
            {['instagram', 'facebook-f', 'twitter', 'tiktok'].map((social, idx) => (
              <a 
                key={idx} 
                href="#" 
                className="w-11 h-11 rounded-full bg-white/[0.03] border border-white/5 flex items-center justify-center text-stone-400 hover:bg-[#d4b88d] hover:text-black transition-all duration-500 shadow-xl"
              >
                <i className={`fab fa-${social} text-sm`}></i>
              </a>
            ))}
          </div>
        </div>

        {/* Links Column */}
        <div>
          <div className="flex items-center gap-3 mb-10">
             <h4 className="text-[#d4b88d] font-bold uppercase tracking-[0.4em] text-[11px]">Explora</h4>
          </div>
          <nav className="flex flex-col gap-5">
            {[
              { to: "/", label: "Inicio" },
              { to: "/menu", label: "Menú" },
              { to: "/about", label: "Nosotros" },
              { to: "/branches", label: "Sucursales" },
              { to: "/contact", label: "Contacto" }
            ].map(link => (
              <Link key={link.label} to={link.to} className="text-[13px] text-stone-300 hover:text-white transition-all flex items-center gap-3 group">
                <span className="w-1 h-1 rounded-full bg-[#d4b88d]/30 group-hover:bg-[#d4b88d] transition-colors"></span>
                {link.label}
              </Link>
            ))}
          </nav>
        </div>

        {/* Contact Column */}
        <div>
          <div className="flex items-center gap-3 mb-10">
             <h4 className="text-[#d4b88d] font-bold uppercase tracking-[0.4em] text-[11px]">Concierge</h4>
          </div>
          <ul className="flex flex-col gap-6 text-[13px] text-stone-300 font-light italic">
            <li className="flex items-start gap-4">
              <i className="fas fa-location-dot text-[#d4b88d] mt-1 text-[11px]"></i>
              <span className="leading-relaxed">Av. Principal 123, <br/>Distrito Elegante, Madrid</span>
            </li>
            <li className="flex items-center gap-4">
              <i className="fas fa-phone-volume text-[#d4b88d] text-[11px]"></i>
              <span className="font-bold tracking-tight text-white">+34 910 123 456</span>
            </li>
            <li className="flex items-center gap-4">
              <i className="fas fa-envelope text-[#d4b88d] text-[11px]"></i>
              <span className="group-hover:text-white">info@mokaf.com</span>
            </li>
          </ul>
        </div>

        {/* Hours Column */}
        <div>
          <div className="flex items-center gap-3 mb-10">
             <h4 className="text-[#d4b88d] font-bold uppercase tracking-[0.4em] text-[11px]">Tiempos</h4>
          </div>
          <div className="space-y-4 text-[13px] text-stone-400 font-light">
             {[
               { day: 'Lunes - Viernes', time: '07:00 — 20:00' },
               { day: 'Sábados', time: '08:00 — 22:00' },
               { day: 'Domingos', time: '08:00 — 18:00' },
               { day: 'Festivos', time: '09:00 — 16:00' }
             ].map(item => (
                <div key={item.day} className="flex justify-between border-b border-white/5 pb-3">
                   <span className="tracking-tight">{item.day}</span>
                   <span className="text-white font-serif italic">{item.time}</span>
                </div>
             ))}
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-8 pt-12 border-t border-white/5 text-center">
         <div className="flex flex-col md:flex-row items-center justify-between gap-6">
            <p className="text-[10px] text-stone-400 font-bold uppercase tracking-[0.4em]">
               &copy; 2026 Mokaf Specialty &mdash; Destilando Pasión
            </p>
            <div className="flex items-center gap-6 text-stone-500 text-[10px] font-bold uppercase tracking-[0.2em]">
               <a href="#" className="hover:text-[#d4b88d] transition-colors">Cookies</a>
               <a href="#" className="hover:text-[#d4b88d] transition-colors">Privacidad</a>
               <a href="#" className="hover:text-[#d4b88d] transition-colors">Términos</a>
            </div>
         </div>
      </div>
    </footer>
  );
}
