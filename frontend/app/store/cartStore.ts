import { create } from 'zustand';
import { API_BASE_URL } from '../config';

interface CartState {
  itemCount: number;
  // Update count by fetching the latest cart data from the API (useful after adding/removing items)
  updateItemCount: () => Promise<void>;
  // Allow stablishing the count directly (useful for optimistic updates when adding/removing items)
  setItemCount: (count: number) => void;
}

export const useCartStore = create<CartState>((set) => ({
  itemCount: 0,
  
  updateItemCount: async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/api/v1/cart`, { credentials: 'include' });
      if (response.ok) {
        const data = await response.json();
        // Using totalUnits instead of items.length to get the total count of items in the cart, regardless of how many different products there are
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
