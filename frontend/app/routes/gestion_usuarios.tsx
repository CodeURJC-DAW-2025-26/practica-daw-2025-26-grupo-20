import { useState, useEffect, useRef } from "react";
import { API_BASE_URL } from "../config";
import { UserService, User } from "../services/userService";

export default function GestionUsuarios() {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [filter, setFilter] = useState("ALL");
  const [errorMsg, setErrorMsg] = useState("");
  
  const formRef = useRef<HTMLFormElement>(null);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const data = await UserService.getUsers();
      setUsers(data);
    } catch (err) {
      console.error(err);
      setErrorMsg("No se pudieron cargar los usuarios.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMsg("");
    if (!formRef.current) return;
    
    const formData = new FormData(formRef.current);
    const data = Object.fromEntries(formData) as any;
    
    try {
      if (editingId) {
        // En edición, si el password está vacío lo borramos para que el backend no lo toque
        if (!data.password) delete data.password;
        await UserService.updateUser(editingId, data);
      } else {
        await UserService.createUser(data);
      }
      setShowForm(false);
      setEditingId(null);
      formRef.current.reset();
      fetchUsers();
    } catch (err: any) {
      setErrorMsg(err.message || "Error al guardar el usuario.");
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm("¿Seguro que deseas eliminar este usuario?")) return;
    try {
      await UserService.deleteUser(id);
      fetchUsers();
    } catch (err: any) {
      alert(err.message);
    }
  };

  const handleEdit = (user: User) => {
    setShowForm(true);
    setEditingId(user.id);
    setErrorMsg("");
    setTimeout(() => {
      if (!formRef.current) return;
      const elements = formRef.current.elements as any;
      elements.name.value = user.name || "";
      elements.email.value = user.email || "";
      elements.role.value = user.role || "CUSTOMER";
      elements.firstName.value = user.firstName || "";
      elements.lastName.value = user.lastName || "";
      elements.position.value = user.position || "";
      elements.department.value = user.department || "";
      if (elements.password) elements.password.value = "";
    }, 0);
  };

  const handleAddNew = () => {
    setEditingId(null);
    setShowForm(!showForm);
    setErrorMsg("");
    if (formRef.current) formRef.current.reset();
  };

  const filteredUsers = filter === "ALL" ? users : users.filter(u => u.role === filter);

  const getRoleTheme = (role: string) => {
    switch(role) {
      case "ADMIN": return "bg-red-500/20 text-red-400 border-red-500/30";
      case "EMPLOYEE": return "bg-amber-500/20 text-amber-400 border-amber-500/30";
      default: return "bg-blue-500/20 text-blue-400 border-blue-500/30";
    }
  };

  const roles = [
    { id: "ALL", label: "Todos" },
    { id: "ADMIN", label: "Administradores" },
    { id: "EMPLOYEE", label: "Empleados" },
    { id: "CUSTOMER", label: "Clientes" }
  ];

  return (
    <div className="bg-[#050404] min-h-screen text-white pb-32 pt-10 font-sans">
      <div className="container mx-auto px-4 max-w-7xl">
        
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-8 gap-4">
          <div>
            <h1 className="text-3xl font-medium tracking-tight text-white mb-2">Gestión de Usuarios</h1>
            <p className="text-[#d4b88d]/60 text-sm">Administra los accesos y roles del sistema</p>
          </div>
          <button 
            onClick={handleAddNew}
            className="px-6 py-2.5 bg-[#d4b88d] text-[#050404] font-bold text-sm rounded-lg hover:bg-white transition-all flex items-center gap-2"
          >
            <i className={`fas ${showForm && !editingId ? 'fa-minus' : 'fa-plus'}`}></i>
            {showForm && !editingId ? "Ocultar Formulario" : "Nuevo Usuario"}
          </button>
        </div>

        {errorMsg && (
          <div className="bg-red-500/10 border border-red-500/30 text-red-400 p-4 rounded-xl mb-8 flex items-center gap-4 text-sm">
            <i className="fas fa-exclamation-triangle text-lg"></i>
            <span>{errorMsg}</span>
          </div>
        )}

        {/* User Form */}
        {showForm && (
          <div className="bg-[rgba(45,35,25,0.9)] border border-[#d4b88d]/30 rounded-2xl p-6 md:p-8 mb-10 transition-all">
            <h3 className="text-xl font-medium text-white mb-6">
              {editingId ? "Editar Usuario" : "Crear Nuevo Usuario"}
            </h3>
            <form ref={formRef} onSubmit={handleSubmit} className="space-y-6">
              <div className="grid md:grid-cols-3 gap-6">
                
                <div className="space-y-2">
                  <label className="text-white text-xs font-medium ml-1">Nombre Público *</label>
                  <input name="name" required className="w-full bg-transparent border border-[#d4b88d]/20 rounded-lg px-4 py-3 focus:border-[#d4b88d] focus:bg-white/5 outline-none transition-all text-white text-sm" placeholder="Ej: Juan Gómez" />
                </div>
                
                <div className="space-y-2">
                  <label className="text-white text-xs font-medium ml-1">Email *</label>
                  <input name="email" type="email" required className="w-full bg-transparent border border-[#d4b88d]/20 rounded-lg px-4 py-3 focus:border-[#d4b88d] focus:bg-white/5 outline-none transition-all text-white text-sm" placeholder="juan@ejemplo.com" />
                </div>

                <div className="space-y-2">
                  <label className="text-white text-xs font-medium ml-1">Rol *</label>
                  <div className="relative">
                    <select name="role" required className="w-full bg-transparent border border-[#d4b88d]/20 rounded-lg px-4 py-3 focus:border-[#d4b88d] focus:bg-white/5 outline-none transition-all text-white text-sm appearance-none">
                      <option value="CUSTOMER" className="bg-[#050404]">Cliente</option>
                      <option value="EMPLOYEE" className="bg-[#050404]">Empleado</option>
                      <option value="ADMIN" className="bg-[#050404]">Administrador</option>
                    </select>
                    <div className="absolute right-4 top-1/2 -translate-y-1/2 pointer-events-none text-[#d4b88d]/60">
                      <i className="fas fa-chevron-down text-xs"></i>
                    </div>
                  </div>
                </div>

                <div className="space-y-2">
                  <label className="text-white text-xs font-medium ml-1">Nombre Real</label>
                  <input name="firstName" className="w-full bg-transparent border border-[#d4b88d]/20 rounded-lg px-4 py-3 focus:border-[#d4b88d] focus:bg-white/5 outline-none transition-all text-white text-sm" />
                </div>

                <div className="space-y-2">
                  <label className="text-white text-xs font-medium ml-1">Apellidos</label>
                  <input name="lastName" className="w-full bg-transparent border border-[#d4b88d]/20 rounded-lg px-4 py-3 focus:border-[#d4b88d] focus:bg-white/5 outline-none transition-all text-white text-sm" />
                </div>

                <div className="space-y-2">
                  <label className="text-white text-xs font-medium ml-1">Contraseña {editingId ? "(Opcional)" : "*"}</label>
                  <input name="password" type="password" required={!editingId} className="w-full bg-transparent border border-[#d4b88d]/20 rounded-lg px-4 py-3 focus:border-[#d4b88d] focus:bg-white/5 outline-none transition-all text-white text-sm" placeholder="••••••••" />
                </div>
              </div>

              <div className="grid md:grid-cols-2 gap-6">
                <div className="space-y-2">
                  <label className="text-white text-xs font-medium ml-1">Posición (Solo Empleados)</label>
                  <input name="position" className="w-full bg-transparent border border-[#d4b88d]/20 rounded-lg px-4 py-3 focus:border-[#d4b88d] focus:bg-white/5 outline-none transition-all text-white text-sm" placeholder="Ej: Barista Senior" />
                </div>
                <div className="space-y-2">
                  <label className="text-white text-xs font-medium ml-1">Departamento</label>
                  <input name="department" className="w-full bg-transparent border border-[#d4b88d]/20 rounded-lg px-4 py-3 focus:border-[#d4b88d] focus:bg-white/5 outline-none transition-all text-white text-sm" placeholder="Ej: Operaciones" />
                </div>
              </div>

              <div className="flex justify-end gap-3 pt-4 border-t border-[#d4b88d]/10">
                <button type="button" onClick={() => setShowForm(false)} className="px-6 py-2.5 bg-transparent border border-[#d4b88d]/30 text-[#d4b88d] font-bold text-sm rounded-lg hover:bg-[#d4b88d]/10 transition-all">
                  Cancelar
                </button>
                <button type="submit" className="px-6 py-2.5 bg-[#d4b88d] text-[#050404] font-bold text-sm rounded-lg hover:bg-white transition-all">
                  {editingId ? "Actualizar Usuario" : "Crear Usuario"}
                </button>
              </div>
            </form>
          </div>
        )}

        {/* Roles Filter Bar */}
        <div className="flex flex-wrap gap-2 mb-8">
          {roles.map(r => (
            <button
              key={r.id}
              onClick={() => setFilter(r.id)}
              className={`px-4 py-1.5 rounded-full text-xs font-bold transition-all ${filter === r.id ? 'bg-[#d4b88d] text-[#050404]' : 'bg-transparent border border-[#d4b88d]/30 text-[#d4b88d] hover:bg-[#d4b88d]/10'}`}
            >
              {r.label}
            </button>
          ))}
        </div>

        {/* Users List Table */}
        <div className="bg-[rgba(45,35,25,0.9)] border border-[#d4b88d]/30 rounded-2xl overflow-hidden shadow-2xl">
          {loading ? (
             <div className="p-20 text-center text-[#d4b88d]/60"><i className="fas fa-spinner fa-spin text-3xl"></i></div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm text-stone-300">
                <thead className="bg-[#050404]/50 text-xs uppercase font-medium text-[#d4b88d]/80 border-b border-[#d4b88d]/20">
                  <tr>
                    <th className="px-6 py-4 w-16">Avatar</th>
                    <th className="px-6 py-4">Usuario</th>
                    <th className="px-6 py-4">Email</th>
                    <th className="px-6 py-4">Rol</th>
                    <th className="px-6 py-4">Puesto</th>
                    <th className="px-6 py-4 text-right">Acciones</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#d4b88d]/10">
                  {filteredUsers.map((u) => (
                    <tr key={u.id} className="hover:bg-white/[0.02] transition-colors group">
                      <td className="px-6 py-4">
                        <div className="w-10 h-10 rounded-full border border-[#d4b88d]/30 flex items-center justify-center bg-white/5 overflow-hidden">
                           {u.profileImageUrl ? (
                             <img src={u.profileImageUrl} alt="avatar" className="w-full h-full object-cover" />
                           ) : (
                             <i className="fas fa-user text-[#d4b88d]/40"></i>
                           )}
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex flex-col">
                          <span className="font-medium text-white">{u.name}</span>
                          <span className="text-[10px] text-stone-500 uppercase tracking-wider">{u.firstName} {u.lastName}</span>
                        </div>
                      </td>
                      <td className="px-6 py-4 text-stone-400">{u.email}</td>
                      <td className="px-6 py-4">
                        <span className={`px-3 py-1 rounded-full text-[10px] uppercase font-bold border ${getRoleTheme(u.role)}`}>
                          {u.role}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-stone-500 italic text-xs">
                        {u.position || "-"}
                      </td>
                      <td className="px-6 py-4 text-right opacity-60 group-hover:opacity-100 transition-opacity">
                        <button onClick={() => handleEdit(u)} className="text-[#d4b88d] hover:text-white transition-colors mr-4" title="Editar">
                          <i className="fas fa-user-edit text-lg"></i>
                        </button>
                        <button onClick={() => handleDelete(u.id)} className="text-red-400 hover:text-red-500 transition-colors" title="Borrar">
                          <i className="fas fa-user-times text-lg"></i>
                        </button>
                      </td>
                    </tr>
                  ))}
                  {filteredUsers.length === 0 && (
                     <tr>
                       <td colSpan={6} className="px-6 py-16 text-center text-[#d4b88d]/50 italic">
                         <i className="fas fa-users-slash text-4xl mb-4 block opacity-20"></i>
                         No se encontraron usuarios en esta categoría.
                       </td>
                     </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
