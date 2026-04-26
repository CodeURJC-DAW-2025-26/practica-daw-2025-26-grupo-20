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
    const data = Object.fromEntries(formData);
    const response = await fetch(`${API_BASE_URL}/api/v1/users/me`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify(data),
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

export default function Profile() {
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
      navigate("/");
    }
  }, [actionData, setUser, logout, navigate]);

  if (!initialUser) return null;

  const avatarSrc = actionData?.profileImageUrl
    || initialUser.profileImageUrl
    || `https://i.pravatar.cc/150?u=${initialUser.id}`;

  const sidebarLinks = [
    { to: "/orders", icon: "fa-history", label: "Historial de Pedidos" },
    { to: "#", icon: "fa-sign-out-alt", label: "Cerrar Sesión", isButton: true, onClick: () => { logout(); navigate("/"); } },
  ];

  return (
    <>
      <ProfileLayout
        title="Mi Perfil"
        role={initialUser.role}
        roleColor="var(--dorado)"
        avatarSrc={avatarSrc}
        userName={initialUser.name}
        userEmail={initialUser.email}
        sidebarLinks={sidebarLinks}
        onDeleteClick={() => setShowDeleteModal(true)}
        accentColor="var(--dorado)"
      >
        <div className="p-10">
          <h2 className="text-xl font-bold m-0 mb-1" style={{ color: 'var(--dorado)' }}>Configuración del Perfil</h2>
          <p className="text-stone-500 text-xs mb-8 uppercase tracking-widest">Gestiona tu información personal y cuenta</p>

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

            <div className="grid md:grid-cols-2 gap-6">
              <ProfileField label="Nombre de Usuario" name="name" defaultValue={initialUser.name} />
              <ProfileField label="Email" name="email" type="email" defaultValue={initialUser.email} />
              <ProfileField label="Nombre" name="firstName" defaultValue={initialUser.firstName} />
              <ProfileField label="Apellidos" name="lastName" defaultValue={initialUser.lastName} />
            </div>

            <div className="space-y-2">
              <label className="text-[11px] text-white font-bold opacity-80 uppercase tracking-tight">Biografía / Descripción</label>
              <textarea
                name="description" defaultValue={initialUser.description} rows={4}
                className="w-full bg-[#1a1a1a] border border-white/5 rounded-lg px-5 py-3 text-white outline-none focus:border-[var(--dorado)]/40 transition-all resize-none text-sm"
              />
            </div>

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
                className="px-8 py-2.5 bg-[var(--dorado)] hover:bg-[#b5966d] text-black rounded-lg text-xs font-bold transition-all shadow-lg"
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
      />
    </>
  );
}
