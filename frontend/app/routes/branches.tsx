import { useLoaderData } from "react-router";
import { API_BASE_URL } from "../config";

interface Branch {
  id: number;
  name: string;
  description: string;
  purchaseDiscountPercent?: number;
}

export async function clientLoader({ request }: { request: Request }) {
  try {
    const res = await fetch(`${API_BASE_URL}/api/v1/branches`, { credentials: "include" });
    if (!res.ok) return { branches: [] };
    const data = await res.json();
    const branches = Array.isArray(data) ? data : (Array.isArray(data?.content) ? data.content : []);
    return { branches };
  } catch (error) {
    console.error("Error fetching branches:", error);
    return { branches: [] };
  }
}

export default function Branches() {
  const data = useLoaderData<typeof clientLoader>();
  const branches: Branch[] = data?.branches || [];

  return (
    <div className="bg-[#050404] min-h-screen text-white pb-32">
      <div className="container mx-auto px-4 sm:px-8 lg:px-12 pt-12 max-w-7xl">
        <div className="animate-fade-in bg-[#080707] border border-[#d4b88d]/10 rounded-[2rem] p-8 sm:p-12 lg:p-20 shadow-[0_40px_100px_rgba(0,0,0,0.8)] relative overflow-hidden flex flex-col gap-24">
          <div className="absolute -top-24 -right-24 w-96 h-96 bg-[#d4b88d]/5 blur-[120px] rounded-full pointer-events-none"></div>
          <div className="absolute -bottom-24 -left-24 w-96 h-96 bg-[#d4b88d]/5 blur-[120px] rounded-full pointer-events-none"></div>

          <div className="text-center relative">
            <div className="flex items-center justify-center gap-6 mb-6">
              <div className="w-16 h-16 rounded-lg flex items-center justify-center text-[#d4b88d]">
                <i className="fas fa-shop text-4xl"></i>
              </div>
              <h1 className="text-5xl md:text-6xl font-serif text-[#d4b88d] tracking-tight font-medium">Nuestras Sucursales</h1>
            </div>
            <div className="mx-auto w-40 h-1 bg-gradient-to-r from-transparent via-[#d4b88d]/60 to-transparent rounded-full"></div>
          </div>

          <div className="grid md:grid-cols-2 gap-10 relative z-10">
            {branches.map((branch) => (
              <div key={branch.id} className="group flex flex-col bg-[#0c0b0b] border border-[#d4b88d]/10 rounded-[2.5rem] p-10 transition-all duration-1000 hover:border-[#d4b88d]/40 hover:bg-[#0e0d0d] shadow-2xl relative overflow-hidden">
                <div className="absolute top-0 right-0 w-32 h-32 bg-[#d4b88d]/5 blur-3xl -translate-y-1/2 translate-x-1/2 group-hover:bg-[#d4b88d]/10 transition-all duration-1000"></div>
                <div className="flex flex-col h-full gap-8">
                  <div className="flex items-center gap-6">
                    <div className="w-14 h-14 bg-[#d4b88d]/10 text-[#d4b88d] rounded-2xl flex items-center justify-center text-xl group-hover:bg-[#d4b88d] group-hover:text-black transition-all duration-500">
                      <i className="fas fa-location-dot text-sm"></i>
                    </div>
                    <h3 className="text-3xl font-serif italic text-white/90 group-hover:text-[#d4b88d] transition-colors">{branch.name}</h3>
                  </div>
                  <div className="flex-grow">
                    <p className="text-stone-400 text-[16px] leading-relaxed font-light line-clamp-4 italic opacity-80 group-hover:opacity-100 transition-opacity">"{branch.description}"</p>
                  </div>
                  <div className="pt-8 border-t border-white/5 flex flex-wrap justify-between items-center gap-6">
                    {branch.purchaseDiscountPercent ? (
                      <div className="flex items-center gap-3 bg-[#d4b88d]/10 text-[#d4b88d] px-6 py-2.5 rounded-full border border-[#d4b88d]/20 font-black text-[10px] uppercase tracking-[0.2em]">
                        <i className="fas fa-tag text-[9px]"></i>
                        <span>Beneficio Exclusivo: {branch.purchaseDiscountPercent}%</span>
                      </div>
                    ) : <div className="w-1 h-1"></div>}
                    <div className="flex items-center gap-2 text-stone-600 text-[9px] font-bold uppercase tracking-[0.4em] group-hover:text-[#d4b88d]/40 transition-all">
                      <span className="w-1.5 h-1.5 rounded-full bg-[#d4b88d]/30"></span>Filial Oficial
                    </div>
                  </div>
                </div>
              </div>
            ))}
            {branches.length === 0 && (
              <div className="col-span-full py-40 text-center bg-white/[0.01] rounded-[3rem] border border-dashed border-white/10 flex flex-col items-center gap-6">
                <div className="w-20 h-20 bg-white/5 rounded-full flex items-center justify-center text-stone-700 animate-pulse">
                  <i className="fas fa-map-pin fa-2x"></i>
                </div>
                <h4 className="text-xl font-serif italic text-stone-500">Buscando nuevas tierras para Mokaf...</h4>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
