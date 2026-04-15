import { useEffect } from 'react';
import { useNavigate, Outlet } from 'react-router';
import { useAuthStore } from '../store/authStore';

interface ProtectedRouteProps {
  /** Si se especifica, solo usuarios con ese rol pueden acceder */
  requiredRole?: 'CUSTOMER' | 'ADMIN';
  /** Ruta a la que redirigir si no hay sesión. Por defecto /login */
  redirectTo?: string;
}

/**
 * Envuelve rutas que requieren autenticación.
 *
 * Uso en routes.ts:
 *   route("profile", "routes/profile.tsx", { element: <ProtectedRoute /> })
 *
 * O como layout padre:
 *   route("admin", "layouts/AdminLayout.tsx")   ← este usa <ProtectedRoute requiredRole="ADMIN" />
 */
export default function ProtectedRoute({
  requiredRole,
  redirectTo = '/login',
}: ProtectedRouteProps) {
  const { user, isLogged, isInitialized } = useAuthStore();
  const navigate = useNavigate();

  useEffect(() => {
    // Esperamos a que initializeAuth haya terminado antes de decidir
    if (!isInitialized) return;

    if (!isLogged || !user) {
      navigate(redirectTo, { replace: true });
      return;
    }

    if (requiredRole && user.role !== requiredRole) {
      // Usuario autenticado pero sin el rol correcto → mandamos a su perfil
      navigate(user.role === 'ADMIN' ? '/profile-admin' : '/profile', { replace: true });
    }
  }, [isInitialized, isLogged, user, requiredRole, redirectTo, navigate]);

  // Mientras initializeAuth no termina mostramos un loader mínimo
  if (!isInitialized) {
    return <AuthLoader />;
  }

  // Si no hay sesión o rol incorrecto no renderizamos nada (el useEffect redirige)
  if (!isLogged || !user) return null;
  if (requiredRole && user.role !== requiredRole) return null;

  return <Outlet />;
}

// ─── Loader minimalista acorde al estilo Mokaf ────────────────────────────────
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
