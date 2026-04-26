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

  return null;
}

export default function Profile() {
  const { user: initialUser, isUnauthorized } = useLoaderData<typeof clientLoader>();
  const actionData = useActionData<typeof clientAction>();
  const { setUser, logout, isLogged } = useAuthStore();
  const navigate = useNavigate();
  const [editing, setEditing] = useState(false);

  useEffect(() => {
    if (isUnauthorized || !isLogged) {
      navigate("/login");
    }
  }, [isUnauthorized, isLogged, navigate]);

  useEffect(() => {
    if (actionData?.success && actionData.user) {
      setUser(actionData.user);
      setEditing(false);
    }
  }, [actionData, setUser]);

  if (!initialUser) return null;

  return (
    <div className="container mx-auto px-4 py-20 max-w-5xl animate-fade-in">
      <div className="bg-white rounded-[3rem] shadow-2xl shadow-stone-200/60 overflow-hidden border border-stone-100">
        <div className="h-64 bg-stone-900 relative">
          <div className="absolute inset-0 opacity-20 pointer-events-none overflow-hidden">
            <i className="fas fa-mug-hot text-[300px] text-white -rotate-12 translate-x-2/3 translate-y-1/2" />
          </div>

          <div className="absolute -bottom-16 left-12 flex items-end gap-8">
            <div className="relative group">
              <img
                src={actionData?.profileImageUrl || initialUser.profileImageUrl || `https://i.pravatar.cc/150?u=${initialUser.id}`}
                className="w-44 h-44 rounded-[2.5rem] border-8 border-white shadow-2xl object-cover transform transition-transform group-hover:scale-105"
                alt={initialUser.name}
              />
              <Form method="post" encType="multipart/form-data" className="absolute bottom-4 right-4">
                <input type="hidden" name="intent" value="upload-image" />
                <label className="w-12 h-12 bg-amber-800 text-white rounded-2xl flex items-center justify-center cursor-pointer hover:bg-amber-900 transition-colors shadow-xl">
                  <i className="fas fa-camera" />
                  <input type="file" name="image" className="hidden" onChange={(e) => e.target.form?.requestSubmit()} />
                </label>
              </Form>
            </div>
            <div className="pb-4 space-y-1">
              <h1 className="text-4xl font-black text-white drop-shadow-md uppercase tracking-tight">{initialUser.name}</h1>
              <p className="text-amber-500 font-black text-xs uppercase tracking-[0.3em]">{initialUser.role}</p>
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
          {editing ? (
            <Form method="post" className="space-y-10 animate-fade-in">
              <input type="hidden" name="intent" value="update" />
              <div className="grid md:grid-cols-2 gap-10">
                <div className="space-y-2">
                  <label className="text-[10px] font-black uppercase tracking-widest text-stone-400 ml-4">Nombre de Usuario</label>
                  <input name="name" defaultValue={initialUser.name} className="w-full bg-stone-50 border-2 border-stone-100 rounded-2xl px-6 py-4 focus:border-amber-800 outline-none transition-all font-bold text-stone-800" />
                </div>
                <div className="space-y-2">
                  <label className="text-[10px] font-black uppercase tracking-widest text-stone-400 ml-4">Email de Contacto</label>
                  <input name="email" defaultValue={initialUser.email} className="w-full bg-stone-50 border-2 border-stone-100 rounded-2xl px-6 py-4 focus:border-amber-800 outline-none transition-all font-bold text-stone-800" />
                </div>
                <div className="space-y-2">
                  <label className="text-[10px] font-black uppercase tracking-widest text-stone-400 ml-4">Nombre</label>
                  <input name="firstName" defaultValue={initialUser.firstName} className="w-full bg-stone-50 border-2 border-stone-100 rounded-2xl px-6 py-4 focus:border-amber-800 outline-none transition-all font-bold text-stone-800" />
                </div>
                <div className="space-y-2">
                  <label className="text-[10px] font-black uppercase tracking-widest text-stone-400 ml-4">Apellidos</label>
                  <input name="lastName" defaultValue={initialUser.lastName} className="w-full bg-stone-50 border-2 border-stone-100 rounded-2xl px-6 py-4 focus:border-amber-800 outline-none transition-all font-bold text-stone-800" />
                </div>
              </div>
              <div className="space-y-2">
                <label className="text-[10px] font-black uppercase tracking-widest text-stone-400 ml-4">Biografía / Descripción</label>
                <textarea name="description" defaultValue={initialUser.description} rows={4} className="w-full bg-stone-50 border-2 border-stone-100 rounded-2xl px-6 py-4 focus:border-amber-800 outline-none transition-all font-bold text-stone-800 resize-none" />
              </div>
              <button type="submit" className="bg-stone-900 text-white px-12 py-4 rounded-2xl font-black uppercase tracking-[0.2em] text-xs hover:bg-stone-800 transition-all shadow-xl active:scale-95">
                Guardar Cambios
              </button>
            </Form>
          ) : (
            <div className="grid md:grid-cols-3 gap-16 animate-fade-in">
              <div className="md:col-span-2 space-y-12">
                <section className="space-y-6">
                  <h3 className="text-xl font-black text-stone-800 uppercase tracking-tight border-b-4 border-amber-100 pb-2 w-fit">Resumen Personal</h3>
                  <p className="text-stone-500 font-medium leading-relaxed italic text-lg">
                    {initialUser.description || "Este barista entusiasta aún no ha escrito su historia en Mokaf. ¡Pero sus pedidos hablan por él!"}
                  </p>
                </section>

                {/* Botón historial de pedidos */}
                <Link
                  to="/orders"
                  className="inline-flex items-center gap-4 bg-stone-900 hover:bg-stone-800 text-white px-10 py-5 rounded-2xl font-black uppercase tracking-[0.2em] text-xs transition-all shadow-xl active:scale-95 group"
                >
                  <i className="fas fa-history text-amber-500 group-hover:rotate-180 transition-transform duration-500" />
                  Historial de Pedidos
                  <i className="fas fa-chevron-right text-[10px] group-hover:translate-x-1 transition-transform" />
                </Link>
              </div>

              <div className="space-y-10">
                <section className="space-y-6">
                  <h3 className="text-sm font-black text-stone-800 uppercase tracking-[0.2em] mb-4">Información de Contacto</h3>
                  <div className="space-y-4">
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
            </div>
          )}
        </div>
      </div>
    </div>
  );
}