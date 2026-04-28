import { Link, Form } from "react-router";

interface ProfileLayoutProps {
  title: string;
  role: string;
  roleColor?: string; // e.g. "var(--dorado)" or "#e05252"
  avatarSrc: string;
  userName: string;
  userEmail: string;
  sidebarLinks: { to: string; icon: string; label: string; isButton?: boolean; onClick?: () => void }[];
  children: React.ReactNode;
  onDeleteClick: () => void;
  accentColor?: string;
}

export function ProfileLayout({
  title,
  role,
  roleColor = "var(--dorado)",
  avatarSrc,
  userName,
  userEmail,
  sidebarLinks,
  children,
  onDeleteClick,
  accentColor = "var(--dorado)"
}: ProfileLayoutProps) {
  return (
    <div className="min-h-screen py-10 px-4 animate-fade-in">
      <div className={`max-w-6xl mx-auto border border-[${accentColor}]/30 rounded-xl overflow-hidden bg-[#1a1a1a] shadow-2xl`}>
        
        {/* Título Superior */}
        <div className="py-8 text-center border-b border-white/5">
          <h1 className="text-3xl font-light tracking-tight m-0 leading-tight" style={{ color: 'var(--dorado)' }}>{title}</h1>
          <div className="w-12 h-0.5 bg-[var(--dorado)] mx-auto mt-2" />
        </div>

        <div className="flex flex-col lg:flex-row p-6 gap-6">

          {/* Sidebar */}
          <div className="lg:w-80 flex-shrink-0">
            <div className={`border border-[${accentColor}]/20 rounded-2xl p-8 flex flex-col items-center bg-[#111] h-full shadow-lg`}>
              
              <span 
                className="text-white text-[10px] font-black uppercase tracking-widest px-6 py-2 rounded-full mb-4"
                style={{ backgroundColor: roleColor, color: roleColor === "var(--dorado)" ? "black" : "white" }}
              >
                {role}
              </span>

              {/* Avatar */}
              <div className="mb-4">
                <div className="w-28 h-28 rounded-full bg-white flex items-center justify-center p-1 overflow-hidden border-2 border-white/10">
                  <img src={avatarSrc} alt={userName} className="w-full h-full rounded-full object-cover" />
                </div>
              </div>

              <div className="text-center mb-8">
                <p className="text-[var(--dorado)] font-bold text-xl tracking-tight">{userName}</p>
                <p className="text-stone-400 text-xs">{userEmail}</p>
              </div>

              <div className="w-full border border-white/5 rounded-xl p-4 bg-black/20">
                <p className="text-[11px] font-bold uppercase tracking-widest text-stone-300 text-center mb-4">PANEL DE CONTROL</p>
                <div className="flex flex-col gap-2">
                  {sidebarLinks.map((link, idx) => (
                    link.isButton ? (
                      <button
                        key={idx}
                        onClick={link.onClick}
                        className="flex items-center gap-3 bg-[#e05252] hover:bg-red-600 text-white px-4 py-2.5 rounded-lg text-xs font-bold transition-all shadow-md"
                      >
                        <i className={`fas ${link.icon} w-4 text-center`} />
                        {link.label}
                      </button>
                    ) : (
                      <Link
                        key={idx}
                        to={link.to}
                        className="flex items-center gap-3 bg-[#e05252] hover:bg-red-600 text-white px-4 py-2.5 rounded-lg text-xs font-bold transition-all shadow-md"
                        style={{ backgroundColor: link.to === "/orders" && accentColor === "var(--dorado)" ? "var(--dorado)" : undefined, color: link.to === "/orders" && accentColor === "var(--dorado)" ? "black" : undefined }}
                      >
                        <i className={`fas ${link.icon} w-4 text-center`} />
                        {link.label}
                      </Link>
                    )
                  ))}
                </div>
              </div>
            </div>
          </div>

          {/* Main Content */}
          <div className={`flex-1 border border-[${accentColor}]/20 rounded-2xl bg-[#111] overflow-hidden shadow-lg`}>
            {children}
          </div>
        </div>

        {/* Footer Bar */}
        <div className="bg-[#e05252] hover:bg-red-600 transition-colors">
          <button
            onClick={onDeleteClick}
            className="w-full flex items-center justify-center gap-3 py-3 text-white font-bold text-xs uppercase tracking-widest transition-all"
          >
            <i className="fas fa-trash-alt" /> Eliminar cuenta
          </button>
        </div>
      </div>
    </div>
  );
}

interface ProfileFieldProps {
  label: string;
  name?: string;
  type?: string;
  defaultValue?: string;
  readOnly?: boolean;
  required?: boolean;
}

export function ProfileField({ label, name, type = "text", defaultValue, readOnly, required }: ProfileFieldProps) {
  return (
    <div className="space-y-2">
      <label className="text-[11px] text-white font-bold opacity-80 uppercase tracking-tight">{label}</label>
      <input
        name={name}
        type={type}
        defaultValue={defaultValue}
        readOnly={readOnly}
        required={required}
        className={`w-full bg-[#1a1a1a] border border-white/5 rounded-lg px-5 py-3 text-white outline-none focus:border-[var(--dorado)]/40 transition-all text-sm ${readOnly ? "text-white/40 cursor-not-allowed" : ""}`}
      />
    </div>
  );
}
