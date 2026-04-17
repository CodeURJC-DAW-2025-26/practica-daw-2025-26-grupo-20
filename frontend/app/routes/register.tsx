import { Form, useActionData, useNavigate, Link } from "react-router";
import { useEffect } from "react";
import { useAuthStore } from "../store/authStore";
import { API_BASE_URL } from "../config";

export async function action({ request }: { request: Request }) {
  try {
    const formData = await request.formData();
    const name     = formData.get("name")     as string;
    const email    = formData.get("email")    as string;
    const password = formData.get("password") as string;

    // Validación cliente igual que el Mustache y AuthController.java
    const emailRegex    = /^[A-Za-z0-9+_.-]+@(.+)$/;
    const passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d).+$/;

    if (!emailRegex.test(email)) {
      return { error: "El formato del email no es válido." };
    }
    if (!passwordRegex.test(password)) {
      return { error: "La contraseña debe contener letras y números." };
    }
    if (password.length < 4) {
      return { error: "La contraseña debe tener al menos 4 caracteres." };
    }

    // POST /api/v1/auth/users — crea el usuario
    const registerResponse = await fetch(`${API_BASE_URL}/api/v1/auth/users`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify({ name, email, password }),
    });

    if (!registerResponse.ok) {
      const errorData = await registerResponse.json().catch(() => ({}));
      // Mensajes de error del AuthController.java
      if (errorData.message?.includes("Email already registered") || registerResponse.status === 409) {
        return { error: "Este email ya está registrado." };
      }
      return { error: errorData.message || "No se pudo crear la cuenta. Inténtalo de nuevo." };
    }

    // Auto-login tras registro
    const loginResponse = await fetch(`${API_BASE_URL}/api/v1/auth/sessions`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify({ email, password }),
    });

    if (loginResponse.ok) {
      const user = await loginResponse.json();
      return { success: true, user };
    }

    // Si el auto-login falla mandamos al login manual
    return { success: true, needsLogin: true };

  } catch (err) {
    console.error("Register error:", err);
    return { error: "Error de conexión con el servidor." };
  }
}

export default function Register() {
  const actionData = useActionData<typeof action>();
  const setUser    = useAuthStore((state) => state.setUser);
  const navigate   = useNavigate();

  useEffect(() => {
    if (actionData?.success && actionData.user) {
      setUser(actionData.user);
      navigate("/menu", { replace: true });
    } else if (actionData?.success && actionData.needsLogin) {
      navigate("/login", { replace: true });
    }
  }, [actionData, setUser, navigate]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#050404] px-4 py-20 animate-fade-in relative overflow-hidden">

      {/* Halos decorativos */}
      <div className="absolute top-0 left-0 w-[500px] h-[500px] bg-[#d4b88d]/5 blur-[120px] rounded-full -translate-y-1/2 -translate-x-1/2 pointer-events-none" />
      <div className="absolute bottom-0 right-0 w-[500px] h-[500px] bg-[#d4b88d]/5 blur-[120px] rounded-full translate-y-1/2 translate-x-1/2 pointer-events-none" />

      <div className="w-full max-w-lg relative z-10 p-4">
        <div className="bg-[#080707] border border-[#d4b88d]/10 rounded-[3rem] p-10 md:p-16 shadow-[0_40px_100px_rgba(0,0,0,0.8)] backdrop-blur-3xl flex flex-col items-center relative overflow-hidden">

          {/* Línea dorada superior */}
          <div className="absolute inset-x-0 top-0 h-[1px] bg-gradient-to-r from-transparent via-[#d4b88d]/30 to-transparent" />

          {/* Logo */}
          <Link to="/" className="mb-12 group transition-all duration-700">
            <div className="w-24 h-24 bg-[#0c0b0b] border border-[#d4b88d]/20 rounded-[2.5rem] flex items-center justify-center text-[#d4b88d] text-4xl shadow-2xl group-hover:bg-[#d4b88d] group-hover:text-black group-hover:scale-110 transition-all duration-700">
              <i className="fas fa-mug-hot" />
            </div>
          </Link>

          <div className="text-center mb-16 space-y-4">
            <h1 className="text-4xl md:text-5xl font-serif text-[#d4b88d] italic tracking-tighter drop-shadow-sm">
              Únete a Mokaf
            </h1>
            <p className="text-stone-500 font-bold text-[10px] uppercase tracking-[0.5em] italic">
              Comienza tu viaje en el café de especialidad
            </p>
          </div>

          {/* Error */}
          {actionData?.error && (
            <div className="w-full bg-red-500/10 border border-red-500/30 text-red-400 p-6 rounded-2xl mb-12 flex items-center gap-4">
              <i className="fas fa-exclamation-circle text-lg" />
              <p className="text-[11px] font-bold uppercase tracking-widest">
                {actionData.error}
              </p>
            </div>
          )}

          <Form method="post" className="w-full space-y-10">

            {/* Nombre completo — campo "name" igual que el backend */}
            <div className="space-y-4">
              <label className="text-[10px] font-bold uppercase tracking-[0.4em] text-stone-600 ml-6">
                Nombre completo
              </label>
              <div className="relative group">
                <i className="fas fa-user absolute left-8 top-1/2 -translate-y-1/2 text-stone-600 group-focus-within:text-[#d4b88d] transition-colors" />
                <input
                  name="name"
                  type="text"
                  required
                  placeholder="Tu nombre completo"
                  autoComplete="name"
                  className="w-full bg-white/[0.02] border border-white/10 rounded-[2rem] px-16 py-6 focus:bg-white/[0.05] focus:border-[#d4b88d] outline-none transition-all text-white placeholder:text-stone-800 font-light lg:text-lg"
                />
              </div>
            </div>

            {/* Email */}
            <div className="space-y-4">
              <label className="text-[10px] font-bold uppercase tracking-[0.4em] text-stone-600 ml-6">
                Email
              </label>
              <div className="relative group">
                <i className="fas fa-envelope absolute left-8 top-1/2 -translate-y-1/2 text-stone-600 group-focus-within:text-[#d4b88d] transition-colors" />
                <input
                  name="email"
                  type="email"
                  required
                  placeholder="tu@esencia.com"
                  autoComplete="email"
                  className="w-full bg-white/[0.02] border border-white/10 rounded-[2rem] px-16 py-6 focus:bg-white/[0.05] focus:border-[#d4b88d] outline-none transition-all text-white placeholder:text-stone-800 font-light lg:text-lg"
                />
              </div>
            </div>

            {/* Contraseña */}
            <div className="space-y-4">
              <label className="text-[10px] font-bold uppercase tracking-[0.4em] text-stone-600 ml-6">
                Contraseña
              </label>
              <div className="relative group">
                <i className="fas fa-lock absolute left-8 top-1/2 -translate-y-1/2 text-stone-600 group-focus-within:text-[#d4b88d] transition-colors" />
                <input
                  name="password"
                  type="password"
                  required
                  minLength={4}
                  placeholder="Mín. 4 caracteres"
                  autoComplete="new-password"
                  className="w-full bg-white/[0.02] border border-white/10 rounded-[2rem] px-16 py-6 focus:bg-white/[0.05] focus:border-[#d4b88d] outline-none transition-all text-white placeholder:text-stone-800 font-light lg:text-lg"
                />
              </div>
              <p className="text-[10px] text-stone-700 ml-6">
                Debe contener letras y números
              </p>
            </div>

            {/* Botón */}
            <button
              type="submit"
              className="w-full group relative h-20 bg-transparent border border-[#d4b88d]/30 rounded-[2rem] overflow-hidden shadow-2xl active:scale-95 transition-all mt-10"
            >
              <div className="absolute inset-0 bg-[#d4b88d] translate-y-full group-hover:translate-y-0 transition-transform duration-700" />
              <div className="relative z-10 flex items-center justify-center gap-4 text-[#d4b88d] group-hover:text-black font-black uppercase tracking-[0.4em] text-[11px] transition-colors duration-700">
                <span>Crear Cuenta</span>
                <i className="fas fa-sparkles text-[10px] group-hover:rotate-45 transition-transform" />
              </div>
            </button>
          </Form>

          <div className="mt-20 text-center space-y-6">
            <p className="text-stone-600 text-[10px] font-bold uppercase tracking-[0.4em]">
              ¿Ya eres parte de la familia?
            </p>
            <Link
              to="/login"
              className="inline-block text-[#d4b88d] hover:text-white font-bold uppercase text-[12px] tracking-[0.25em] transition-all border-b border-[#d4b88d]/30 hover:border-white pb-2"
            >
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
