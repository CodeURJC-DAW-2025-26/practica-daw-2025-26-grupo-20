import { useLoaderData, useActionData, Form, useNavigate } from "react-router";
import { useEffect, useState } from "react";
import { useAuthStore } from "../store/authStore";
import { API_BASE_URL } from "../config";
import { ProfileLayout, ProfileField } from "../components/ProfileLayout";
import { ConfirmDeleteModal } from "../components/ConfirmDeleteModal";

export async function clientLoader() {
  const response = await fetch(`${API_BASE_URL}/api/v1/users/me`, {
    credentials: "include",
  });
  if (response.status === 401) return { isUnauthorized: true };
  if (!response.ok) return { user: null };
  const user = await response.json();
  return { user };
}

export async function clientAction({ request }: { request: Request }) {
  const formData = await request.formData();
  const intent = formData.get("intent");

  if (intent === "update") {
    const name     = formData.get("name") as string;
    const email    = formData.get("email") as string;
    const password = formData.get("password") as string;

    const response = await fetch(`${API_BASE_URL}/api/v1/users/me`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify({
        name,
        email,
        ...(password?.trim() ? { password } : {}),
      }),
    });
    if (response.ok) {
      const user = await response.json();
      return { success: true, user };
    }
    return { error: "Error al actualizar el perfil." };
  }

  if (intent === "upload-image") {
    const file = formData.get("image") as File;
    const body = new FormData();
    body.append("image", file);
    const response = await fetch(`${API_BASE_URL}/api/v1/users/me/image`, {
      method: "POST",
      credentials: "include",
      body,
    });
    if (response.ok) {
      const data = await response.json();
      return { success: true, profileImageUrl: data.profileImageUrl };
    }
    return { error: "Error al subir la imagen." };
  }

  if (intent === "delete") {
    await fetch(`${API_BASE_URL}/api/v1/users/me`, {
      method: "DELETE",
      credentials: "include",
    });
    return { deleted: true };
  }

  return null;
}

export default function ProfileAdmin() {
  const { user: initialUser, isUnauthorized } = useLoaderData<typeof clientLoader>();
  const actionData = useActionData<typeof clientAction>();
  const { setUser, logout, isLogged } = useAuthStore();
  const navigate = useNavigate();
  const [showDeleteModal, setShowDeleteModal] = useState(false);

  useEffect(() => {
    if (isUnauthorized || !isLogged) navigate("/login");
  }, [isUnauthorized, isLogged, navigate]);

  useEffect(() => {
    if (actionData?.success && actionData.user) {
      setUser(actionData.user);
    }
    if (actionData?.deleted) {
      logout();
      navigate("/login");
    }
  }, [actionData, setUser, logout, navigate]);

  if (!initialUser) return null;

  const avatarSrc = actionData?.profileImageUrl
    || initialUser.profileImageUrl
    || `https://i.pravatar.cc/150?u=${initialUser.id}`;

  const sidebarLinks = [
    { to: "/gestion-menu",     icon: "fa-coffee",         label: "Gestión de Productos" },
    { to: "/statistics",       icon: "fa-chart-bar",      label: "Ver Estadísticas"     },
    { to: "/gestion-usuarios", icon: "fa-users",          label: "Gestionar Usuarios"   },
    { to: "/orders",           icon: "fa-clipboard-list", label: "Ver Pedidos"          },
    { to: "#", icon: "fa-sign-out-alt", label: "Cerrar Sesión", isButton: true, onClick: () => { logout(); navigate("/"); } },
  ];

  return (
    <>
      <ProfileLayout
        title="Perfil de Administrador"
        role="ADMINISTRADOR"
        roleColor="#e05252"
        avatarSrc={avatarSrc}
        userName={initialUser.name}
        userEmail={initialUser.email}
        sidebarLinks={sidebarLinks}
        onDeleteClick={() => setShowDeleteModal(true)}
        accentColor="var(--dorado)"
      >
        <div className="p-10">
          <h2 className="text-xl font-bold m-0 mb-1" style={{ color: 'var(--dorado)' }}>Configuración del Administrador</h2>
          <p className="text-stone-500 text-xs mb-8 uppercase tracking-widest">Gestiona tu cuenta de administrador</p>

          {actionData?.success && actionData.user && (
            <div className="bg-green-500/10 border border-green-500/30 text-green-400 p-4 rounded-xl mb-8 text-sm font-medium flex items-center gap-3 animate-fade-in">
              <i className="fas fa-check-circle" /> Perfil actualizado correctamente
            </div>
          )}
          {actionData?.error && (
            <div className="bg-red-500/10 border border-red-500/30 text-red-400 p-4 rounded-xl mb-8 text-sm font-medium flex items-center gap-3 animate-fade-in">
              <i className="fas fa-exclamation-circle" /> {actionData.error}
            </div>
          )}

          <Form method="post" className="space-y-6">
            <input type="hidden" name="intent" value="update" />

            <ProfileField label="Nombre completo" name="name" defaultValue={initialUser.name} required />

            <div className="grid md:grid-cols-2 gap-6">
              <ProfileField label="Nombre de usuario" defaultValue={initialUser.email} readOnly />
              <ProfileField label="Correo electrónico" name="email" type="email" defaultValue={initialUser.email} required />
            </div>

            <ProfileField label="ID de Empleado" defaultValue={initialUser.id?.toString() ?? "Sin ID asignado"} readOnly />
            <ProfileField label="Nueva contraseña" name="password" type="password" />

            <div className="space-y-2">
              <label className="text-[11px] text-white font-bold opacity-80 uppercase tracking-tight">Foto de perfil (PNG)</label>
              <div className="flex flex-col gap-2">
                <div className="flex items-center gap-4 bg-[#1a1a1a] border border-white/5 rounded-lg p-2 overflow-hidden">
                  <Form method="post" encType="multipart/form-data" className="w-full">
                    <input type="hidden" name="intent" value="upload-image" />
                    <input 
                      type="file" name="image" accept="image/png,image/jpeg,image/jpg" 
                      className="text-xs text-stone-500 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-xs file:font-bold file:bg-white file:text-black hover:file:bg-stone-200 cursor-pointer w-full"
                      onChange={e => e.target.form?.requestSubmit()}
                    />
                  </Form>
                </div>
              </div>
            </div>

            <div className="flex justify-end gap-4 pt-8">
              <button
                type="button"
                onClick={() => navigate(-1)}
                className="px-8 py-2.5 border border-white/20 text-stone-400 hover:text-white rounded-lg text-xs font-bold transition-all"
              >
                Cancelar
              </button>
              <button
                type="submit"
                className="px-8 py-2.5 bg-[#e05252] hover:bg-red-600 text-white rounded-lg text-xs font-bold transition-all shadow-lg"
              >
                Guardar Cambios
              </button>
            </div>
          </Form>
        </div>
      </ProfileLayout>

      <ConfirmDeleteModal
        isOpen={showDeleteModal}
        onClose={() => setShowDeleteModal(false)}
        title="Eliminar cuenta de administrador"
        message="¿Estás seguro de que deseas eliminar definitivamente tu cuenta de administrador? Esta acción no se puede deshacer y afectará a la gestión del sistema."
      />
    </>
  );
}
