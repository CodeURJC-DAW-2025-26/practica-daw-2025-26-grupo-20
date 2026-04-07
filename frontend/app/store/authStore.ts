import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface User {
  id: number;
  name: string;
  email: string;
  role: 'CUSTOMER' | 'ADMIN';
  profileImageUrl?: string;
}

interface AuthState {
  user: User | null;
  isLogged: boolean;
  setUser: (user: User | null) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      isLogged: false,
      setUser: (user) => set({ user, isLogged: !!user }),
      logout: () => set({ user: null, isLogged: false }),
    }),
    {
      name: 'mokaf-auth',
    }
  )
);
