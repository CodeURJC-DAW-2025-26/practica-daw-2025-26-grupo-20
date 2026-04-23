import { create } from 'zustand';
import { API_BASE_URL } from '../config';

interface CartState {
  itemCount: number;
  // Actualiza el contador pidiendo el carrito actual al servidor
  updateItemCount: () => Promise<void>;
  // Permite establecer un número directamente (útil tras una respuesta de la API)
  setItemCount: (count: number) => void;
}

export const useCartStore = create<CartState>((set) => ({
  itemCount: 0,
  
  updateItemCount: async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/api/v1/cart`, { credentials: 'include' });
      if (response.ok) {
        const data = await response.json();
        // Usamos totalUnits para contar unidades totales, o itemCount para líneas de producto
        set({ itemCount: data.totalUnits || 0 });
      } else {
        set({ itemCount: 0 });
      }
    } catch (error) {
      console.error("Error al actualizar el contador del carrito:", error);
      set({ itemCount: 0 });
    }
  },

  setItemCount: (count: number) => set({ itemCount: count }),
}));
