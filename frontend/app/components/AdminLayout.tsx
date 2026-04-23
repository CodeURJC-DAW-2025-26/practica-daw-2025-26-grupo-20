import { Outlet, useNavigate } from "react-router";
import { useEffect } from "react";
import { useAuthStore } from "../store/authStore";

/**
 * Layout para rutas exclusivas de administrador.
 * Si el usuario no es ADMIN lo redirige a su perfil de cliente.
 */
export default function AdminLayout() {
  const { user, isLogged, isInitialized } = useAuthStore();
  const navigate = useNavigate();

  useEffect(() => {
    if (!isInitialized) return;

    if (!isLogged || !user) {
      navigate("/login", { replace: true });
      return;
    }

    if (user.role !== "ADMIN") {
      navigate("/profile", { replace: true });
    }
  }, [isInitialized, isLogged, user, navigate]);

  if (!isInitialized) return <AuthLoader />;
  if (!isLogged || !user) return null;
  if (user.role !== "ADMIN") return null;

  return <Outlet />;
}

function AuthLoader() {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-[#050404] gap-6">
      <div className="w-16 h-16 bg-[#0c0b0b] border border-[#d4b88d]/20 rounded-[1.5rem] flex items-center justify-center text-[#d4b88d] text-2xl animate-pulse">
        <i className="fas fa-mug-hot"></i>
      </div>
      <p className="text-[10px] font-bold uppercase tracking-[0.5em] text-stone-700">
        Preparando tu café…
      </p>
    </div>
  );
}
