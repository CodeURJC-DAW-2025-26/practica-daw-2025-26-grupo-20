import { Form, useActionData, useNavigate, Link } from "react-router";
import { useEffect } from "react";
import { useAuthStore } from "../store/authStore";

export async function action({ request }: { request: Request }) {
  try {
    const formData = await request.formData();
    const firstName = formData.get("firstName");
    const lastName = formData.get("lastName");
    const email = formData.get("email");
    const password = formData.get("password");

    const response = await fetch("https://localhost:8443/api/v1/auth/users", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify({ firstName, lastName, email, password }),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      return { error: errorData.message || "No se pudo crear la cuenta. Inténtalo de nuevo." };
    }

    // Auto-login after registration
    const loginResponse = await fetch("https://localhost:8443/api/v1/auth/sessions", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify({ email, password }),
    });

    if (loginResponse.ok) {
        const data = await loginResponse.json();
        return { success: true, user: data.user };
    }

    return { success: true, needsLogin: true };
  } catch (error) {
    console.error("Registration error:", error);
    return { error: "Error de conexión con el servidor." };
  }
}

export default function Register() {
  const actionData = useActionData<typeof action>();
  const setUser = useAuthStore(state => state.setUser);
  const navigate = useNavigate();

  useEffect(() => {
    if (actionData?.success && actionData.user) {
      setUser(actionData.user);
      navigate("/menu");
    } else if (actionData?.success && actionData.needsLogin) {
      navigate("/login");
    }
  }, [actionData, setUser, navigate]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#050404] px-4 py-20 animate-fade-in relative overflow-hidden">
      
      {/* Premium Background Elements */}
      <div className="absolute top-0 left-0 w-[500px] h-[500px] bg-[#d4b88d]/5 blur-[120px] rounded-full -translate-y-1/2 -translate-x-1/2"></div>
      <div className="absolute bottom-0 right-0 w-[500px] h-[500px] bg-[#d4b88d]/5 blur-[120px] rounded-full translate-y-1/2 translate-x-1/2"></div>
      
      <div className="w-full max-w-2xl relative z-10 p-4">
        {/* Boutique Register Frame */}
        <div className="bg-[#080707] border border-[#d4b88d]/10 rounded-[3rem] p-10 md:p-16 shadow-[0_40px_100px_rgba(0,0,0,0.8)] backdrop-blur-3xl flex flex-col items-center relative overflow-hidden">
          
          <div className="absolute inset-x-0 top-0 h-[1px] bg-gradient-to-r from-transparent via-[#d4b88d]/30 to-transparent"></div>

          <div className="text-center mb-16 space-y-4">
            <h1 className="text-4xl md:text-5xl font-serif text-[#d4b88d] italic tracking-tighter drop-shadow-sm">Únete a Mokaf</h1>
            <p className="text-stone-500 font-bold text-[10px] uppercase tracking-[0.5em] italic">Comienza tu viaje en el café de especialidad</p>
          </div>

          {actionData?.error && (
            <div className="w-full bg-red-500/10 border border-red-500/30 text-red-400 p-6 rounded-2xl mb-12 flex items-center gap-4 animate-shake">
              <i className="fas fa-exclamation-circle text-lg"></i>
              <p className="text-[11px] font-bold uppercase tracking-widest">{actionData.error}</p>
            </div>
          )}

          <Form method="post" className="w-full space-y-10">
            <div className="grid md:grid-cols-2 gap-10">
              <div className="space-y-4">
                <label className="text-[10px] font-bold uppercase tracking-[0.4em] text-stone-600 ml-6">Nombre</label>
                <div className="relative group">
                  <input 
                    name="firstName" 
                    required 
                    placeholder="Tu nombre"
                    className="w-full bg-white/[0.02] border border-white/10 rounded-[2rem] px-8 py-5 focus:bg-white/[0.05] focus:border-[#d4b88d] outline-none transition-all text-white placeholder:text-stone-800 font-light" 
                  />
                </div>
              </div>
              <div className="space-y-4">
                <label className="text-[10px] font-bold uppercase tracking-[0.4em] text-stone-600 ml-6">Apellidos</label>
                <div className="relative group">
                  <input 
                    name="lastName" 
                    required 
                    placeholder="Tu apellido"
                    className="w-full bg-white/[0.02] border border-white/10 rounded-[2rem] px-8 py-5 focus:bg-white/[0.05] focus:border-[#d4b88d] outline-none transition-all text-white placeholder:text-stone-800 font-light" 
                  />
                </div>
              </div>
            </div>

            <div className="space-y-4">
              <label className="text-[10px] font-bold uppercase tracking-[0.4em] text-stone-600 ml-6">Email</label>
              <div className="relative group">
                <i className="fas fa-envelope absolute left-8 top-1/2 -translate-y-1/2 text-stone-600 group-focus-within:text-[#d4b88d] transition-colors"></i>
                <input 
                  name="email" 
                  type="email" 
                  required 
                  placeholder="tu@esencia.com"
                  className="w-full bg-white/[0.02] border border-white/10 rounded-[2rem] px-16 py-6 focus:bg-white/[0.05] focus:border-[#d4b88d] outline-none transition-all text-white placeholder:text-stone-800 font-light lg:text-lg" 
                />
              </div>
            </div>

            <div className="space-y-4">
              <label className="text-[10px] font-bold uppercase tracking-[0.4em] text-stone-600 ml-6">Contraseña</label>
              <div className="relative group">
                <i className="fas fa-lock absolute left-8 top-1/2 -translate-y-1/2 text-stone-600 group-focus-within:text-[#d4b88d] transition-colors"></i>
                <input 
                  name="password" 
                  type="password" 
                  required 
                  placeholder="Mínimo 8 caracteres"
                  className="w-full bg-white/[0.02] border border-white/10 rounded-[2rem] px-16 py-6 focus:bg-white/[0.05] focus:border-[#d4b88d] outline-none transition-all text-white placeholder:text-stone-800 font-light lg:text-lg"
                />
              </div>
            </div>

            <button type="submit" className="w-full group relative h-20 bg-transparent border border-[#d4b88d]/30 rounded-[2rem] overflow-hidden shadow-2xl active:scale-95 transition-all mt-10">
              <div className="absolute inset-0 bg-[#d4b88d] translate-y-full group-hover:translate-y-0 transition-transform duration-700"></div>
              <div className="relative z-10 flex items-center justify-center gap-4 text-[#d4b88d] group-hover:text-black font-black uppercase tracking-[0.4em] text-[11px] transition-colors duration-700">
                <span>Crear Cuenta</span>
                <i className="fas fa-sparkles text-[10px] group-hover:rotate-45 transition-transform"></i>
              </div>
            </button>
          </Form>

          <div className="mt-20 text-center space-y-6">
            <p className="text-stone-600 text-[10px] font-bold uppercase tracking-[0.4em]">¿Ya eres parte de la familia?</p>
            <Link to="/login" className="inline-block text-[#d4b88d] hover:text-white font-bold uppercase text-[12px] tracking-[0.25em] transition-all border-b border-[#d4b88d]/30 hover:border-white pb-2">
              Inicia sesión aquí
            </Link>
          </div>
        </div>

        <div className="mt-16 text-center">
          <p className="text-[10px] font-bold uppercase tracking-[0.6em] text-stone-800">
            &copy; 2026 Mokaf Specialty Coffee
          </p>
        </div>
      </div>
    </div>
  );
}
