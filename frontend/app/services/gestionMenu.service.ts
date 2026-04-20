import { API_BASE_URL } from "../config";

export interface Product {
  id: number;
  name: string;
  category: string;
  priceBase: number;
  description: string;
  imageId?: number;
  allergens?: any[];
}

export const ProductService = {
  getProducts: async (): Promise<Product[]> => {
    const res = await fetch(`${API_BASE_URL}/api/v1/products?size=100`, { credentials: "include" });
    if (!res.ok) throw new Error("Error fetching products");
    const data = await res.json();
    return data.content || [];
  },

  createProduct: async (formData: FormData): Promise<void> => {
    const res = await fetch(`${API_BASE_URL}/api/v1/products`, {
      method: "POST",
      body: formData,
      credentials: "include"
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || "Error al crear el producto.");
    }
  },

  updateProduct: async (id: number, formData: FormData): Promise<void> => {
    const res = await fetch(`${API_BASE_URL}/api/v1/products/${id}`, {
      method: "PUT",
      body: formData,
      credentials: "include"
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || "Error al actualizar el producto.");
    }
  },

  deleteProduct: async (id: number): Promise<void> => {
    const res = await fetch(`${API_BASE_URL}/api/v1/products/${id}`, {
      method: "DELETE",
      credentials: "include"
    });
    if (!res.ok) {
      throw new Error("No se pudo borrar el producto (puede que esté referenciado en otro lugar).");
    }
  }
};
