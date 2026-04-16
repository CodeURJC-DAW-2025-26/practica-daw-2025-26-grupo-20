import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { API_BASE_URL } from '../config';

export interface User {
  id: number;
  name: string;
  firstName?: string;
  lastName?: string;
  email: string;
  role: 'CUSTOMER' | 'ADMIN';
  profileImageUrl?: string;
  description?: string;
}

interface AuthState {
  user: User | null;
  isLogged: boolean;
  isInitialized: boolean;
  setUser: (user: User | null) => void;
  logout: () => Promise<void>;
  initializeAuth: () => Promise<void>;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      isLogged: false,
      isInitialized: false,

      setUser: (user) => set({ user, isLogged: !!user }),

      logout: async () => {
        try {
          await fetch(`${API_BASE_URL}/api/v1/auth/sessions`, {
            method: 'DELETE',
            credentials: 'include',
          });
        } catch {
          // Si falla el servidor limpiamos igualmente el estado local
        }
        set({ user: null, isLogged: false });
      },

      initializeAuth: async () => {
        // Solo ejecutar una vez por sesión de app
        if (get().isInitialized) return;

        try {
          const response = await fetch(`${API_BASE_URL}/api/v1/users/me`, {
            credentials: 'include',
          });

          if (response.ok) {
            const user: User = await response.json();
            set({ user, isLogged: true, isInitialized: true });
          } else {
            // 401 → sesión inválida o expirada
            set({ user: null, isLogged: false, isInitialized: true });
          }
        } catch {
          // Sin conexión → mantenemos el estado persistido
          set({ isInitialized: true });
        }
      },
    }),
    {
      name: 'mokaf-auth',
      // isInitialized NO se persiste: siempre arranca en false para re-verificar con el servidor
      partialize: (state) => ({ user: state.user, isLogged: state.isLogged }),
    }
  )
);
