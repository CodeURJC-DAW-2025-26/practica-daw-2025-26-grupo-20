import { useLoaderData, useActionData, Form, useNavigate, Link } from "react-router";
import { useEffect, useState } from "react";
import { useAuthStore } from "../store/authStore";
import { API_BASE_URL } from "../config";

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

  const navLinks = [
    { to: "/gestion-menu",     icon: "fa-coffee",         label: "Gestión de Productos" },
    { to: "/statistics",       icon: "fa-chart-bar",      label: "Ver Estadísticas"     },
    { to: "/gestion-usuarios", icon: "fa-users",          label: "Gestionar Usuarios"   },
    { to: "/orders",           icon: "fa-clipboard-list", label: "Ver Pedidos"          },
  ];

  return (
    <div className="min-h-screen bg-[#1a1a1a] py-10 px-4 animate-fade-in">
      <div className="max-w-6xl mx-auto">

        {/* Título */}
        <h1 className="text-center text-3xl font-serif italic text-[#c6a87d] mb-2">Perfil de Administrador</h1>
        <div className="w-16 h-0.5 bg-[#c6a87d] mx-auto mb-10" />

        <div className="flex flex-col lg:flex-row gap-6">

          {/* ── Sidebar izquierdo ── */}
          <div className="lg:w-72 flex-shrink-0">
            <div className="border border-[#e05252]/60 rounded-2xl p-6 flex flex-col items-center gap-4 bg-[#111]">

              <span className="bg-[#e05252] text-white text-[10px] font-black uppercase tracking-widest px-4 py-1.5 rounded-full">
                Administrador
              </span>

              {/* Avatar con cámara */}
              <div className="relative">
                <img
                  src={avatarSrc}
                  alt={initialUser.name}
                  className="w-28 h-28 rounded-full border-4 border-[#e05252]/60 object-cover"
                />
                <Form method="post" encType="multipart/form-data">
                  <input type="hidden" name="intent" value="upload-image" />
                  <label className="absolute bottom-1 right-1 w-8 h-8 bg-[#e05252] rounded-full flex items-center justify-center cursor-pointer hover:bg-red-600 transition-colors shadow-lg">
                    <i className="fas fa-camera text-white text-xs" />
                    <input type="file" name="image" accept="image/png,image/jpeg,image/jpg" className="hidden" onChange={e => e.target.form?.requestSubmit()} />
                  </label>
                </Form>
              </div>

              <div className="text-center">
                <p className="text-[#c6a87d] font-bold text-base">{initialUser.name}</p>
                <p className="text-stone-400 text-xs mt-1">{initialUser.email}</p>
              </div>

              <div className="w-full border-t border-white/10 pt-4 mt-2">
                <p className="text-[10px] font-black uppercase tracking-widest text-stone-500 text-center mb-3">Panel de Control</p>
                <div className="flex flex-col gap-2">
                  {navLinks.map(item => (
                    <Link
                      key={item.to}
                      to={item.to}
                      className="flex items-center gap-3 bg-[#e05252] hover:bg-red-600 text-white px-4 py-2.5 rounded-xl text-sm font-bold transition-all"
                    >
                      <i className={`fas ${item.icon} w-4 text-center`} />
                      {item.label}
                    </Link>
                  ))}
                  <button
                    onClick={() => { logout(); navigate("/"); }}
                    className="flex items-center gap-3 bg-[#e05252] hover:bg-red-600 text-white px-4 py-2.5 rounded-xl text-sm font-bold transition-all w-full mt-1"
                  >
                    <i className="fas fa-sign-out-alt w-4 text-center" />
                    Cerrar Sesión
                  </button>
                </div>
              </div>
            </div>
          </div>

          {/* ── Panel derecho — solo configuración ── */}
          <div className="flex-1 border border-[#e05252]/60 rounded-2xl bg-[#111] overflow-hidden">
            <div className="p-8">
              <h2 className="text-[#e05252] text-xl font-bold mb-1">Configuración del Administrador</h2>
              <p className="text-stone-500 text-sm mb-8">Gestiona tu cuenta de administrador</p>

              {actionData?.success && actionData.user && (
                <div className="bg-green-500/10 border border-green-500/30 text-green-400 p-4 rounded-xl mb-6 text-xs font-bold flex items-center gap-3">
                  <i className="fas fa-check-circle" /> Perfil actualizado correctamente
                </div>
              )}
              {actionData?.error && (
                <div className="bg-red-500/10 border border-red-500/30 text-red-400 p-4 rounded-xl mb-6 text-xs font-bold flex items-center gap-3">
                  <i className="fas fa-exclamation-circle" /> {actionData.error}
                </div>
              )}

              <Form method="post" className="space-y-6">
                <input type="hidden" name="intent" value="update" />

                <div>
                  <label className="text-xs text-stone-400 uppercase tracking-widest block mb-2">Nombre completo</label>
                  <input
                    name="name" defaultValue={initialUser.name} required
                    className="w-full bg-[#1a1a1a] border border-white/10 rounded-xl px-5 py-3.5 text-white outline-none focus:border-[#e05252]/60 transition-all"
                  />
                </div>

                <div className="grid md:grid-cols-2 gap-6">
                  <div>
                    <label className="text-xs text-stone-400 uppercase tracking-widest block mb-2">Nombre de usuario</label>
                    <input
                      value={initialUser.email} readOnly
                      className="w-full bg-[#222] border border-white/10 rounded-xl px-5 py-3.5 text-stone-500 outline-none cursor-not-allowed"
                    />
                  </div>
                  <div>
                    <label className="text-xs text-stone-400 uppercase tracking-widest block mb-2">Correo electrónico</label>
                    <input
                      name="email" type="email" defaultValue={initialUser.email} required
                      className="w-full bg-[#1a1a1a] border border-white/10 rounded-xl px-5 py-3.5 text-white outline-none focus:border-[#e05252]/60 transition-all"
                    />
                  </div>
                </div>

                <div>
                  <label className="text-xs text-stone-400 uppercase tracking-widest block mb-2">ID de Empleado</label>
                  <input
                    value={initialUser.id ?? "Sin ID asignado"} readOnly
                    className="w-full bg-[#222] border border-white/10 rounded-xl px-5 py-3.5 text-stone-500 outline-none cursor-not-allowed"
                  />
                </div>

                <div>
                  <label className="text-xs text-stone-400 uppercase tracking-widest block mb-2">Nueva contraseña</label>
                  <input
                    name="password" type="password" placeholder="Dejar en blanco para mantener la actual"
                    className="w-full bg-[#1a1a1a] border border-white/10 rounded-xl px-5 py-3.5 text-white placeholder:text-stone-700 outline-none focus:border-[#e05252]/60 transition-all"
                  />
                </div>

                <div className="flex justify-end gap-4 pt-4 border-t border-white/10">
                  <button
                    type="button"
                    onClick={() => navigate(-1)}
                    className="px-8 py-3 border border-white/20 text-stone-300 hover:text-white rounded-xl text-sm font-bold transition-all"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    className="px-8 py-3 bg-[#e05252] hover:bg-red-600 text-white rounded-xl text-sm font-bold transition-all"
                  >
                    Guardar Cambios
                  </button>
                </div>
              </Form>
            </div>

            {/* Eliminar cuenta */}
            <div className="border-t border-white/10">
              <button
                onClick={() => setShowDeleteModal(true)}
                className="w-full flex items-center justify-center gap-3 py-4 text-[#e05252] hover:bg-[#e05252]/10 font-bold text-sm uppercase tracking-widest transition-all"
              >
                <i className="fas fa-trash-alt" /> Eliminar cuenta
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Modal confirmación borrado */}
      {showDeleteModal && (
        <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center z-50 px-4">
          <div className="bg-[#111] border border-[#e05252]/40 rounded-2xl p-8 max-w-md w-full shadow-2xl">
            <h5 className="text-lg font-black text-white uppercase tracking-tight mb-3">Eliminar cuenta de administrador</h5>
            <p className="text-stone-400 text-sm leading-relaxed mb-8">
              <strong className="text-red-400">Advertencia:</strong> Esta acción es permanente e irreversible y podría afectar la gestión del sistema. ¿Estás absolutamente seguro?
            </p>
            <div className="flex gap-4">
              <button
                onClick={() => setShowDeleteModal(false)}
                className="flex-1 px-6 py-3 border border-white/20 text-stone-400 hover:text-white rounded-xl text-sm font-black transition-all"
              >
                Cancelar
              </button>
              <Form method="post" className="flex-1">
                <input type="hidden" name="intent" value="delete" />
                <button type="submit" className="w-full px-6 py-3 bg-[#e05252] hover:bg-red-600 text-white rounded-xl text-sm font-black transition-all">
                  Sí, eliminar definitivamente
                </button>
              </Form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}