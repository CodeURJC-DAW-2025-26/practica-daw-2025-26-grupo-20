import { useLoaderData, useNavigate } from "react-router";
import { useEffect } from "react";
import { useAuthStore } from "../store/authStore";
import { API_BASE_URL } from "../config";

export async function clientLoader() {
  const response = await fetch(`${API_BASE_URL}/api/v1/statistics/dashboard`, { credentials: "include" });

  if (response.status === 401) return { isUnauthorized: true };
  if (response.status === 403) return { isForbidden: true };
  if (!response.ok) return { stats: null };

  const stats = await response.json();
  return { stats };
}

export default function Statistics() {
  const { stats, isUnauthorized, isForbidden } = useLoaderData<typeof clientLoader>();
  const { user, isLogged } = useAuthStore();
  const navigate = useNavigate();

  useEffect(() => {
    if (isUnauthorized || !isLogged) navigate("/login");
    else if (isForbidden || user?.role !== 'ADMIN') navigate("/menu");
  }, [isUnauthorized, isForbidden, isLogged, user, navigate]);

  if (!stats) return null;

  return (
    <div className="container mx-auto px-4 py-20 max-w-7xl animate-fade-in space-y-16">
      <div className="flex flex-col md:flex-row justify-between items-end gap-8 border-b-2 border-stone-100 pb-12">
        <div className="space-y-4">
          <div className="inline-block bg-amber-100 text-amber-800 px-4 py-1.5 rounded-full text-[10px] font-black uppercase tracking-[0.3em]">Panel de Control</div>
          <h1 className="text-6xl font-black text-stone-800 uppercase tracking-tighter italic">Estadísticas <span className="text-amber-800">Mokaf</span></h1>
          <p className="text-stone-400 font-bold text-xs uppercase tracking-widest leading-relaxed max-w-lg">Análisis detallado de ventas, rendimiento de sucursales y preferencias de clientes.</p>
        </div>
      </div>

      <div className="grid lg:grid-cols-3 gap-10">
        <div className="lg:col-span-2 group bg-white rounded-[3rem] p-12 border border-stone-100 shadow-xl relative overflow-hidden flex flex-col md:flex-row items-center gap-10">
          <div className="absolute top-0 right-0 w-64 h-64 bg-amber-50 rounded-full blur-3xl -translate-y-1/2 translate-x-1/2 opacity-50"></div>
          <div className="relative w-64 h-64 flex-shrink-0">
            <img src={stats.bestProduct?.imageUrl ? `${API_BASE_URL}${stats.bestProduct.imageUrl}` : "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=500"} className="w-full h-full object-cover rounded-[2.5rem] shadow-2xl" alt="Best Product" />
            <div className="absolute -top-4 -left-4 bg-amber-800 text-white w-14 h-14 rounded-full flex items-center justify-center shadow-xl border-4 border-white animate-bounce">
              <i className="fas fa-crown"></i>
            </div>
          </div>
          <div className="flex-grow space-y-6 relative z-10 text-center md:text-left">
            <div>
              <p className="text-[10px] font-black uppercase tracking-[0.4em] text-amber-800 mb-2">Producto Estrella</p>
              <h2 className="text-4xl font-black text-stone-800 uppercase tracking-tight leading-tight">{stats.bestProduct?.name || 'Caramel Macchiato'}</h2>
            </div>
            <div className="grid grid-cols-2 gap-8 pt-4">
              <div className="space-y-1">
                <p className="text-[8px] font-black uppercase tracking-widest text-stone-300">Ventas Totales</p>
                <p className="text-3xl font-black text-stone-800 italic">{stats.bestProduct?.totalSales || 1540}</p>
              </div>
              <div className="space-y-1">
                <p className="text-[8px] font-black uppercase tracking-widest text-stone-300">Calificación Promedio</p>
                <div className="flex items-center gap-2 text-amber-500 font-black text-xl italic">
                  {stats.bestProduct?.averageRating || 4.9} <i className="fas fa-star text-sm"></i>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-stone-900 rounded-[3rem] p-12 text-white shadow-2xl relative overflow-hidden flex flex-col justify-between group">
          <i className="fas fa-mug-hot absolute -bottom-10 -right-10 text-[200px] opacity-[0.03]"></i>
          <div className="space-y-8 relative z-10">
            <div className="w-16 h-16 bg-white/10 rounded-2xl flex items-center justify-center text-amber-500 text-2xl"><i className="fas fa-layer-group"></i></div>
            <div>
              <p className="text-[10px] font-black uppercase tracking-[0.3em] text-stone-500 mb-2">Categoría Dominante</p>
              <h3 className="text-4xl font-black uppercase tracking-tight italic">{stats.topCategory?.name || 'Café Caliente'}</h3>
            </div>
          </div>
          <div className="mt-12 pt-12 border-t border-stone-800 relative z-10 flex justify-between items-end">
            <div>
              <p className="text-[8px] font-black uppercase tracking-widest text-stone-500 mb-1">Crecimiento Mensual</p>
              <p className="text-4xl font-black text-amber-500 italic">+18.5%</p>
            </div>
          </div>
        </div>
      </div>

      <div className="grid md:grid-cols-2 gap-10">
        <div className="bg-white rounded-[3rem] p-12 border border-stone-100 shadow-xl">
          <h3 className="text-2xl font-black text-stone-800 uppercase tracking-tight mb-10 flex items-center gap-4">
            <i className="fas fa-store text-amber-800"></i>Rendimiento por Sucursal
          </h3>
          <div className="space-y-8">
            {stats.allBranches?.map((branch: any, idx: number) => (
              <div key={branch.name} className="flex items-center gap-6">
                <div className={`w-10 h-10 rounded-xl flex items-center justify-center font-black text-xs ${idx === 0 ? 'bg-amber-800 text-white' : 'bg-stone-50 text-stone-400'}`}>{idx + 1}</div>
                <div className="flex-grow space-y-2">
                  <div className="flex justify-between items-end">
                    <span className="font-black text-stone-700 uppercase tracking-tight text-sm">{branch.name}</span>
                    <span className="text-[10px] font-black text-stone-400">{branch.totalSales || 0} Ventas</span>
                  </div>
                  <div className="h-2 bg-stone-50 rounded-full overflow-hidden border border-stone-100 shadow-inner">
                    <div className="h-full bg-amber-800 rounded-full transition-all duration-1000" style={{ width: `${(branch.totalSales / (stats.allBranches[0]?.totalSales || 1)) * 100}%` }}></div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-stone-50 rounded-[3rem] p-12 border border-stone-100">
          <h3 className="text-2xl font-black text-stone-800 uppercase tracking-tight mb-10 flex items-center gap-4">
            <i className="fas fa-pie-chart text-amber-800"></i>Distribución de Mercado
          </h3>
          <div className="grid grid-cols-2 gap-6">
            {stats.allCategories?.map((cat: any) => (
              <div key={cat.name} className="bg-white p-8 rounded-3xl border border-stone-100 shadow-sm hover:shadow-xl transition-all group">
                <div className="flex justify-between items-start mb-6">
                  <div className="w-10 h-10 bg-stone-50 text-amber-800 rounded-xl flex items-center justify-center group-hover:bg-amber-800 group-hover:text-white transition-all">
                    <i className="fas fa-mug-hot text-xs"></i>
                  </div>
                  <span className="text-amber-800 font-black text-xs italic">{cat.percentage || '15'}%</span>
                </div>
                <h4 className="text-sm font-black text-stone-800 uppercase tracking-tight truncate">{cat.name}</h4>
                <p className="text-[8px] font-black uppercase tracking-widest text-stone-400 mt-2">{cat.totalSales} Pedidos</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
