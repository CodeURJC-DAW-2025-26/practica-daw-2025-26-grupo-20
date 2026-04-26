import { Form, useActionData, useNavigate, Link } from "react-router";
import { useEffect } from "react";
import { useAuthStore } from "../store/authStore";
import { API_BASE_URL } from "../config";

export async function clientAction({ request }: { request: Request }) {
  try {
    const formData = await request.formData();
    const intent = formData.get("intent") as string;

    if (intent === "login") {
      const email = formData.get("email") as string;
      const password = formData.get("password") as string;

      const loginResponse = await fetch(`${API_BASE_URL}/api/v1/auth/sessions`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ email, password }),
      });

      if (!loginResponse.ok) {
        return { loginError: "Credenciales incorrectas. Inténtalo de nuevo." };
      }

      const meResponse = await fetch(`${API_BASE_URL}/api/v1/users/me`, {
        credentials: "include",
      });

      if (!meResponse.ok) {
        return { loginError: "Login correcto pero no se pudo obtener el perfil." };
      }

      const user = await meResponse.json();
      return { success: true, user };
    }

    if (intent === "register") {
      const name = formData.get("name") as string;
      const email = formData.get("email-register") as string;
      const password = formData.get("password-register") as string;

      const emailRegex = /^[A-Za-z0-9+_.-]+@(.+)$/;
      const passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d).+$/;

      if (!name?.trim()) return { registerError: "El nombre es obligatorio." };
      if (!emailRegex.test(email)) return { registerError: "El formato del email no es válido." };
      if (password.length < 6) return { registerError: "La contraseña debe tener al menos 6 caracteres." };
      if (!passwordRegex.test(password)) return { registerError: "La contraseña debe contener letras y números." };

      const registerResponse = await fetch(`${API_BASE_URL}/api/v1/auth/registrations`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ name, email, password }),
      });

      if (!registerResponse.ok) {
        const errorData = await registerResponse.json().catch(() => ({}));
        if (registerResponse.status === 400 && errorData.message?.includes("Email already registered")) {
          return { registerError: "Este email ya está registrado." };
        }
        return { registerError: errorData.message || "No se pudo crear la cuenta. Inténtalo de nuevo." };
      }

      const loginResponse = await fetch(`${API_BASE_URL}/api/v1/auth/sessions`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ email, password }),
      });

      if (!loginResponse.ok) return { registerSuccess: true };

      const meResponse = await fetch(`${API_BASE_URL}/api/v1/users/me`, {
        credentials: "include",
      });

      if (meResponse.ok) {
        const user = await meResponse.json();
        return { success: true, user };
      }

      return { registerSuccess: true };
    }

    return null;
  } catch (err) {
    console.error("Auth error:", err);
    return { loginError: "Error de conexión con el servidor." };
  }
}

export default function Login() {
  const actionData = useActionData<typeof clientAction>();
  const setUser = useAuthStore((state) => state.setUser);
  const navigate = useNavigate();

  useEffect(() => {
    if (actionData?.success && actionData.user) {
      setUser(actionData.user);
      navigate(actionData.user.role === "ADMIN" ? "/profile-admin" : "/menu", { replace: true });
    }
  }, [actionData, setUser, navigate]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#1A1A1A] px-4 py-16 animate-fade-in">

      {/* Contenedor principal */}
      <div className="w-full max-w-5xl border border-[#c6a87d]/20 rounded-2xl overflow-hidden shadow-[0_20px_60px_rgba(0,0,0,0.6)]">

        {/* Título */}
        <div className="text-center py-10 border-b border-[#c6a87d]/20 bg-[#111]/60">
          <h1 className="text-3xl font-serif text-[#c6a87d] italic tracking-tight">Login o Registro</h1>
        </div>

        {/* Columnas */}
        <div className="grid md:grid-cols-2">

          {/* ── Columna Login ── */}
          <div className="p-10 md:p-12 border-b md:border-b-0 md:border-r border-[#c6a87d]/20 bg-[#111]/40">
            <h2 className="text-2xl font-bold text-[#c6a87d] mb-1 tracking-tight font-serif italic">Login</h2>
            <div className="w-8 h-0.5 bg-[#c6a87d]/50 mb-8" />

            {actionData?.loginError && (
              <div className="bg-red-500/10 border border-red-500/30 text-red-400 p-4 rounded-xl mb-6 text-xs font-bold uppercase tracking-widest flex items-center gap-3">
                <i className="fas fa-exclamation-circle" /> {actionData.loginError}
              </div>
            )}

            <Form method="post" className="space-y-6">
              <input type="hidden" name="intent" value="login" />

              <div className="space-y-2">
                <label className="text-xs font-bold text-[#D7CCC8]/70 uppercase tracking-widest">Email</label>
                <input
                  name="email" type="email" required placeholder="Tu email" autoComplete="email"
                  className="w-full bg-white/[0.04] border border-[#c6a87d]/20 rounded-lg px-5 py-3.5 text-white placeholder:text-stone-700 outline-none focus:border-[#c6a87d]/60 transition-all font-light"
                />
              </div>

              <div className="space-y-2">
                <label className="text-xs font-bold text-[#D7CCC8]/70 uppercase tracking-widest">Contraseña</label>
                <input
                  name="password" type="password" required placeholder="Tu contraseña" autoComplete="current-password"
                  className="w-full bg-white/[0.04] border border-[#c6a87d]/20 rounded-lg px-5 py-3.5 text-white placeholder:text-stone-700 outline-none focus:border-[#c6a87d]/60 transition-all font-light"
                />
              </div>

              <button
                type="submit"
                className="w-full bg-blue-600 hover:bg-blue-700 text-white py-3.5 rounded-lg font-bold uppercase tracking-widest text-sm transition-all duration-300 mt-2"
              >
                Login
              </button>
            </Form>
          </div>

          {/* ── Columna Registro ── */}
          <div className="p-10 md:p-12 bg-[#111]/40">
            <h2 className="text-2xl font-bold text-[#c6a87d] mb-1 tracking-tight font-serif italic">Registro</h2>
            <div className="w-8 h-0.5 bg-[#c6a87d]/50 mb-8" />

            {actionData?.registerError && (
              <div className="bg-red-500/10 border border-red-500/30 text-red-400 p-4 rounded-xl mb-6 text-xs font-bold uppercase tracking-widest flex items-center gap-3">
                <i className="fas fa-exclamation-circle" /> {actionData.registerError}
              </div>
            )}

            {actionData?.registerSuccess && (
              <div className="bg-green-500/10 border border-green-500/30 text-green-400 p-4 rounded-xl mb-6 text-xs font-bold uppercase tracking-widest flex items-center gap-3">
                <i className="fas fa-check-circle" /> Cuenta creada. Inicia sesión.
              </div>
            )}

            <Form method="post" className="space-y-6">
              <input type="hidden" name="intent" value="register" />

              <div className="space-y-2">
                <label className="text-xs font-bold text-[#D7CCC8]/70 uppercase tracking-widest">Nombre completo</label>
                <input
                  name="name" type="text" required placeholder="Tu nombre completo" autoComplete="name"
                  className="w-full bg-white/[0.04] border border-[#c6a87d]/20 rounded-lg px-5 py-3.5 text-white placeholder:text-stone-700 outline-none focus:border-[#c6a87d]/60 transition-all font-light"
                />
              </div>

              <div className="space-y-2">
                <label className="text-xs font-bold text-[#D7CCC8]/70 uppercase tracking-widest">Email</label>
                <input
                  name="email-register" type="email" required placeholder="Tu email" autoComplete="off"
                  className="w-full bg-white/[0.04] border border-[#c6a87d]/20 rounded-lg px-5 py-3.5 text-white placeholder:text-stone-700 outline-none focus:border-[#c6a87d]/60 transition-all font-light"
                />
              </div>

              <div className="space-y-2">
                <label className="text-xs font-bold text-[#D7CCC8]/70 uppercase tracking-widest">Contraseña</label>
                <input
                  name="password-register" type="password" required minLength={6} placeholder="Mín. 6 caracteres, letras y números"
                  className="w-full bg-white/[0.04] border border-[#c6a87d]/20 rounded-lg px-5 py-3.5 text-white placeholder:text-stone-700 outline-none focus:border-[#c6a87d]/60 transition-all font-light"
                />
              </div>

              <button
                type="submit"
                className="w-full bg-green-600 hover:bg-green-700 text-white py-3.5 rounded-lg font-bold uppercase tracking-widest text-sm transition-all duration-300 mt-2"
              >
                Registrarse
              </button>
            </Form>
          </div>

        </div>
      </div>
    </div>
  );
}