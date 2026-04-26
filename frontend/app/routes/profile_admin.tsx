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
  const [editing, setEditing] = useState(false);

  useEffect(() => {
    if (isUnauthorized || !isLogged) navigate("/login");
  }, [isUnauthorized, isLogged, navigate]);

  useEffect(() => {
    if (actionData?.success && actionData.user) {
      setUser(actionData.user);
      setEditing(false);
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

  return (
    <div className="container mx-auto px-4 py-20 max-w-5xl animate-fade-in">
      <div className="bg-white rounded-[3rem] shadow-2xl shadow-stone-200/60 overflow-hidden border border-stone-100">

        {/* ── Header / Cover ───────────────────────────────────────────── */}
        <div className="h-64 bg-stone-900 relative">
          <div className="absolute inset-0 opacity-20 pointer-events-none overflow-hidden">
            <i className="fas fa-mug-hot text-[300px] text-white -rotate-12 translate-x-2/3 translate-y-1/2" />
          </div>

          <div className="absolute -bottom-16 left-12 flex items-end gap-8">
            <div className="relative group">
              <img
                src={avatarSrc}
                className="w-44 h-44 rounded-[2.5rem] border-8 border-white shadow-2xl object-cover transform transition-transform group-hover:scale-105"
                alt={initialUser.name}
              />
              <Form method="post" encType="multipart/form-data" className="absolute bottom-4 right-4">
                <input type="hidden" name="intent" value="upload-image" />
                <label className="w-12 h-12 bg-amber-800 text-white rounded-2xl flex items-center justify-center cursor-pointer hover:bg-amber-900 transition-colors shadow-xl">
                  <i className="fas fa-camera" />
                  <input type="file" name="image" accept="image/png,image/jpeg,image/jpg" className="hidden" onChange={e => e.target.form?.requestSubmit()} />
                </label>
              </Form>
            </div>
            <div className="pb-4 space-y-1">
              <h1 className="text-4xl font-black text-white drop-shadow-md uppercase tracking-tight">{initialUser.name}</h1>
              <p className="text-amber-500 font-black text-xs uppercase tracking-[0.3em]">ADMIN</p>
            </div>
          </div>

          <div className="absolute top-8 right-8 flex gap-4">
            <button
              onClick={() => { logout(); navigate("/"); }}
              className="bg-white/10 hover:bg-red-500/20 text-white border border-white/20 px-6 py-2.5 rounded-2xl text-[10px] font-black uppercase tracking-widest transition-all backdrop-blur-md"
            >
              Cerrar Sesión
            </button>
            <button
              onClick={() => setEditing(!editing)}
              className="bg-amber-800 hover:bg-amber-700 text-white px-8 py-2.5 rounded-2xl text-[10px] font-black uppercase tracking-widest transition-all shadow-xl shadow-amber-900/40"
            >
              {editing ? "Cancelar" : "Editar Perfil"}
            </button>
          </div>
        </div>

        <div className="pt-24 px-12 pb-16">
          {actionData?.success && actionData.user && (
            <div className="mb-8 bg-green-50 border border-green-200 text-green-700 p-4 rounded-2xl flex items-center gap-3 text-sm font-bold">
              <i className="fas fa-check-circle" /> Perfil actualizado correctamente
            </div>
          )}
          {actionData?.error && (
            <div className="mb-8 bg-red-50 border border-red-200 text-red-600 p-4 rounded-2xl flex items-center gap-3 text-sm font-bold">
              <i className="fas fa-exclamation-circle" /> {actionData.error}
            </div>
          )}

          {editing ? (
            /* ── Formulario de edición ─────────────────────────────────── */
            <Form method="post" className="space-y-10 animate-fade-in">
              <input type="hidden" name="intent" value="update" />
              <div className="grid md:grid-cols-2 gap-10">
                <div className="space-y-2">
                  <label className="text-[10px] font-black uppercase tracking-widest text-stone-400 ml-4">Nombre completo</label>
                  <input name="name" defaultValue={initialUser.name} required className="w-full bg-stone-50 border-2 border-stone-100 rounded-2xl px-6 py-4 focus:border-amber-800 outline-none transition-all font-bold text-stone-800" />
                </div>
                <div className="space-y-2">
                  <label className="text-[10px] font-black uppercase tracking-widest text-stone-400 ml-4">ID de Empleado</label>
                  <input value={initialUser.id || "Sin ID asignado"} readOnly className="w-full bg-stone-100 border-2 border-stone-100 rounded-2xl px-6 py-4 outline-none font-bold text-stone-400 cursor-not-allowed" />
                </div>
                <div className="space-y-2">
                  <label className="text-[10px] font-black uppercase tracking-widest text-stone-400 ml-4">Correo electrónico</label>
                  <input name="email" type="email" defaultValue={initialUser.email} required className="w-full bg-stone-50 border-2 border-stone-100 rounded-2xl px-6 py-4 focus:border-amber-800 outline-none transition-all font-bold text-stone-800" />
                </div>
                <div className="space-y-2">
                  <label className="text-[10px] font-black uppercase tracking-widest text-stone-400 ml-4">Nueva contraseña</label>
                  <input name="password" type="password" placeholder="Dejar en blanco para mantener la actual" className="w-full bg-stone-50 border-2 border-stone-100 rounded-2xl px-6 py-4 focus:border-amber-800 outline-none transition-all font-bold text-stone-800 placeholder:font-normal placeholder:text-stone-400" />
                </div>
              </div>
              <div className="flex items-center gap-4">
                <button type="submit" className="bg-stone-900 text-white px-12 py-4 rounded-2xl font-black uppercase tracking-[0.2em] text-xs hover:bg-stone-800 transition-all shadow-xl active:scale-95">
                  Guardar Cambios
                </button>
                <button
                  type="button"
                  onClick={() => setShowDeleteModal(true)}
                  className="text-red-400 hover:text-red-600 font-black text-xs uppercase tracking-widest transition-colors flex items-center gap-2"
                >
                  <i className="fas fa-trash-alt" /> Eliminar cuenta
                </button>
              </div>
            </Form>
          ) : (
            /* ── Vista normal con panel de control ─────────────────────── */
            <div className="grid md:grid-cols-3 gap-16 animate-fade-in">

              {/* Columna izquierda — info personal */}
              <div className="md:col-span-2 space-y-12">
                <section className="space-y-6">
                  <h3 className="text-xl font-black text-stone-800 uppercase tracking-tight border-b-4 border-amber-100 pb-2 w-fit">Resumen Personal</h3>
                  <p className="text-stone-500 font-medium leading-relaxed italic text-lg">
                    {initialUser.description || "Gestor general de la plataforma Mokaf."}
                  </p>
                </section>

                {/* Información de contacto */}
                <section className="space-y-4">
                  <h3 className="text-sm font-black text-stone-800 uppercase tracking-[0.2em]">Información de Contacto</h3>
                  <div className="space-y-3">
                    <div className="flex items-center gap-4 group">
                      <div className="w-10 h-10 bg-stone-50 rounded-xl flex items-center justify-center text-amber-800 group-hover:bg-amber-100 transition-colors">
                        <i className="fas fa-envelope text-xs" />
                      </div>
                      <span className="text-stone-500 font-bold text-sm truncate">{initialUser.email}</span>
                    </div>
                    <div className="flex items-center gap-4 group">
                      <div className="w-10 h-10 bg-stone-50 rounded-xl flex items-center justify-center text-amber-800 group-hover:bg-amber-100 transition-colors">
                        <i className="fas fa-user-tag text-xs" />
                      </div>
                      <span className="text-stone-500 font-bold text-sm">@{initialUser.name}</span>
                    </div>
                  </div>
                </section>
              </div>

              {/* Columna derecha — Panel de control rápido */}
              <div className="space-y-6">
                <div className="bg-stone-900 rounded-[2rem] p-6 space-y-4 relative overflow-hidden">
                  <i className="fas fa-mug-hot absolute -bottom-4 -right-4 text-8xl text-white/5 -rotate-12" />
                  <p className="text-[10px] font-black uppercase tracking-widest text-stone-400 relative z-10">Panel de Control</p>

                  {[
                    { to: "/gestion-menu",      icon: "fa-coffee",         label: "Gestión de Productos" },
                    { to: "/statistics",         icon: "fa-chart-bar",      label: "Ver Estadísticas" },
                    { to: "/gestion-usuarios",   icon: "fa-users",          label: "Gestionar Usuarios" },
                    { to: "/orders",             icon: "fa-clipboard-list", label: "Ver Pedidos" },
                  ].map(item => (
                    <Link
                      key={item.to}
                      to={item.to}
                      className="flex items-center gap-3 bg-white/5 hover:bg-amber-800/30 border border-white/5 hover:border-amber-800/50 rounded-2xl px-4 py-3 text-sm font-bold text-stone-300 hover:text-white transition-all relative z-10"
                    >
                      <i className={`fas ${item.icon} text-amber-500 w-4`} />
                      {item.label}
                    </Link>
                  ))}
                </div>
              </div>

            </div>
          )}
        </div>
      </div>

      {/* ── Modal de confirmación de borrado ──────────────────────────── */}
      {showDeleteModal && (
        <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center z-50 px-4">
          <div className="bg-white rounded-[2rem] p-8 max-w-md w-full shadow-2xl">
            <h5 className="text-lg font-black text-stone-800 uppercase tracking-tight mb-3">Eliminar cuenta de administrador</h5>
            <p className="text-stone-500 text-sm leading-relaxed mb-8">
              <strong className="text-red-500">Advertencia:</strong> Estás a punto de eliminar una cuenta de administrador. Esta acción no se puede deshacer y podría afectar la gestión del sistema. ¿Estás absolutamente seguro?
            </p>
            <div className="flex gap-4">
              <button
                onClick={() => setShowDeleteModal(false)}
                className="flex-1 px-6 py-3 border-2 border-stone-200 text-stone-500 hover:text-stone-800 rounded-2xl text-sm font-black transition-all"
              >
                Cancelar
              </button>
              <Form method="post" className="flex-1">
                <input type="hidden" name="intent" value="delete" />
                <button type="submit" className="w-full px-6 py-3 bg-red-500 hover:bg-red-600 text-white rounded-2xl text-sm font-black transition-all active:scale-95">
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