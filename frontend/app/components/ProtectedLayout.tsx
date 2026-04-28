import { Outlet, useNavigate } from "react-router";
import { useEffect } from "react";
import { useAuthStore } from "../store/authStore";

/**
 * Layout for routes requiring authentication (any role).
 * In React Router v7 framework mode, this component acts as
 * parent for child routes and renders <Outlet /> when there is a session.
 */
export default function ProtectedLayout() {
  const { user, isLogged, isInitialized } = useAuthStore();
  const navigate = useNavigate();

  useEffect(() => {
    if (!isInitialized) return;
    if (!isLogged || !user) {
      navigate("/login", { replace: true });
    }
  }, [isInitialized, isLogged, user, navigate]);

  if (!isInitialized) return <AuthLoader />;
  if (!isLogged || !user) return null;

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
