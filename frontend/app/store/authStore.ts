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

      // Cierra sesión tanto en el cliente como en el servidor
      logout: async () => {
        try {
          await fetch(`${API_BASE_URL}/api/v1/auth/sessions`, {
            method: 'DELETE',
            credentials: 'include',
          });
        } catch {
          // Si falla el servidor, limpiamos igualmente el estado local
        }
        set({ user: null, isLogged: false });
      },

      // Llamado una sola vez al arrancar la app para sincronizar con la cookie de sesión
      initializeAuth: async () => {
        // Si ya está inicializado no volvemos a llamar al servidor
        if (get().isInitialized) return;

        try {
          const response = await fetch(`${API_BASE_URL}/api/v1/users/me`, {
            credentials: 'include',
          });

          if (response.ok) {
            const user: User = await response.json();
            set({ user, isLogged: true, isInitialized: true });
          } else {
            // 401 u otro error → sesión inválida, limpiamos el estado local
            set({ user: null, isLogged: false, isInitialized: true });
          }
        } catch {
          // Sin conexión → mantenemos el estado persistido pero marcamos inicializado
          set({ isInitialized: true });
        }
      },
    }),
    {
      name: 'mokaf-auth',
      // Solo persistimos user e isLogged. isInitialized siempre arranca en false
      partialize: (state) => ({ user: state.user, isLogged: state.isLogged }),
    }
  )
);
