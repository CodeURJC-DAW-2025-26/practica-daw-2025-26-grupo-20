import { API_BASE_URL } from "../config";

export interface User {
  id: number;
  name: string;
  email: string;
  role: "CUSTOMER" | "ADMIN" | "EMPLOYEE";
  firstName?: string;
  lastName?: string;
  description?: string;
  position?: string;
  department?: string;
  profileImageUrl?: string;
  password?: string; // Only for creation/edit
}

export const UserService = {
  getUsers: async (): Promise<User[]> => {
    const response = await fetch(`${API_BASE_URL}/api/v1/users`, { credentials: "include" });
    if (!response.ok) throw new Error("Error al obtener usuarios");
    return response.json();
  },

  deleteUser: async (id: number): Promise<void> => {
    const response = await fetch(`${API_BASE_URL}/api/v1/users/${id}`, {
      method: "DELETE",
      credentials: "include",
    });
    if (!response.ok) throw new Error("Error al eliminar usuario");
  },

  updateUser: async (id: number, userData: Partial<User>): Promise<User> => {
    const response = await fetch(`${API_BASE_URL}/api/v1/users/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify(userData),
    });
    if (!response.ok) throw new Error("Error al actualizar usuario");
    return response.json();
  },

  createUser: async (userData: User): Promise<User> => {
    const response = await fetch(`${API_BASE_URL}/api/v1/users`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify(userData),
    });
    if (!response.ok) {
       const errorData = await response.json().catch(() => ({}));
       throw new Error(errorData.message || "Error al crear usuario");
    }
    return response.json();
  }
};
