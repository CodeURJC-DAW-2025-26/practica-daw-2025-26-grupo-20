import { useLoaderData } from "react-router";
import { API_BASE_URL } from "../config";

interface Branch {
  id: number;
  name: string;
  description: string;
  purchaseDiscountPercent?: number;
}

export async function loader() {
  try {
    const response = await fetch(`${API_BASE_URL}/api/v1/branches`, { credentials: "include" });
    if (!response.ok) return { branches: [] };
    const branches = await response.json();
    return { branches };
  } catch (error) {
    console.error("Error fetching branches:", error);
    return { branches: [] };
  }
}

export default function Branches() {
  const { branches } = useLoaderData<typeof loader>();

  return (
    <div className="bg-[#050404] min-h-screen text-white pb-32">
      <div className="container mx-auto px-4 sm:px-8 lg:px-12 pt-12 max-w-7xl">
        {/* Main Boutique Frame */}
        <div className="animate-fade-in bg-[#080707] border border-[#d4b88d]/10 rounded-[2rem] p-8 sm:p-12 lg:p-20 shadow-[0_40px_100px_rgba(0,0,0,0.8)] relative overflow-hidden flex flex-col gap-24">
          
          {/* Subtle background glow */}
          <div className="absolute -top-24 -right-24 w-96 h-96 bg-[#d4b88d]/5 blur-[120px] rounded-full pointer-events-none"></div>
          <div className="absolute -bottom-24 -left-24 w-96 h-96 bg-[#d4b88d]/5 blur-[120px] rounded-full pointer-events-none"></div>

          {/* Header Section */}
          <div className="text-center relative">
             <div className="inline-flex items-center gap-3 mb-6">
                <span className="h-[1px] w-8 bg-[#d4b88d]/30"></span>
                <span className="text-[10px] text-[#d4b88d] font-bold uppercase tracking-[0.5em]">Nuestra Presencia</span>
                <span className="h-[1px] w-8 bg-[#d4b88d]/30"></span>
             </div>
             <h1 className="text-5xl md:text-7xl font-serif text-[#d4b88d] italic tracking-tighter mb-8 drop-shadow-sm leading-tight">Mokaf en <br className="hidden md:block"/> <span className="text-white opacity-90">tu ciudad</span></h1>
          </div>

          {/* Branches Grid */}
          <div className="grid md:grid-cols-2 gap-10 relative z-10">
            {branches.map((branch: Branch) => (
              <div key={branch.id} className="group flex flex-col bg-[#0c0b0b] border border-[#d4b88d]/10 rounded-[2.5rem] p-10 transition-all duration-1000 hover:border-[#d4b88d]/40 hover:bg-[#0e0d0d] shadow-2xl relative overflow-hidden">
                
                {/* Visual Accent */}
                <div className="absolute top-0 right-0 w-32 h-32 bg-[#d4b88d]/5 blur-3xl -translate-y-1/2 translate-x-1/2 group-hover:bg-[#d4b88d]/10 transition-all duration-1000"></div>

                <div className="flex flex-col h-full gap-8">
                  <div className="flex justify-between items-start">
                    <div className="flex items-center gap-6">
                      <div className="w-14 h-14 bg-[#d4b88d]/10 text-[#d4b88d] rounded-2xl flex items-center justify-center text-xl group-hover:bg-[#d4b88d] group-hover:text-black transition-all duration-500 shadow-inner ring-1 ring-[#d4b88d]/20 group-hover:ring-[#d4b88d]">
                        <i className="fas fa-location-dot text-sm"></i>
                      </div>
                      <h3 className="text-3xl font-serif italic text-white/90 group-hover:text-[#d4b88d] transition-colors">{branch.name}</h3>
                    </div>
                  </div>

                  <div className="flex-grow">
                    <p className="text-stone-400 text-[16px] leading-relaxed font-light line-clamp-4 italic opacity-80 group-hover:opacity-100 transition-opacity">
                      "{branch.description}"
                    </p>
                  </div>

                  <div className="pt-8 border-t border-white/5 flex flex-wrap justify-between items-center gap-6">
                    {branch.purchaseDiscountPercent ? (
                      <div className="flex items-center gap-3 bg-[#d4b88d]/10 text-[#d4b88d] px-6 py-2.5 rounded-full border border-[#d4b88d]/20 font-black text-[10px] uppercase tracking-[0.2em] shadow-[0_0_20px_rgba(212,184,141,0.1)] group-hover:shadow-[0_0_30px_rgba(212,184,141,0.3)] transition-all">
                        <i className="fas fa-tag text-[9px]"></i>
                        <span>Beneficio Exclusivo: {branch.purchaseDiscountPercent}%</span>
                      </div>
                    ) : (
                      <div className="w-1 h-1"></div>
                    )}
                    <div className="flex items-center gap-2 text-stone-600 text-[9px] font-bold uppercase tracking-[0.4em] transform group-hover:text-[#d4b88d]/40 transition-all">
                      <span className="w-1.5 h-1.5 rounded-full bg-[#d4b88d]/30"></span>
                      Filial Oficial
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
                <div className="space-y-2">
                   <h4 className="text-xl font-serif italic text-stone-500">Buscando nuevas tierras para Mokaf...</h4>
                   <p className="text-stone-600 text-sm font-light">Pronto estaremos más cerca de ti.</p>
                </div>
              </div>
            )}
          </div>

          {/* Global CTA section for Branches */}
          <section className="mt-12 text-center py-24 bg-gradient-to-b from-[#0c0b0b] to-transparent rounded-[4rem] border-t border-white/5 relative">
             <div className="relative z-10 space-y-12">
                <div className="space-y-4">
                  <h2 className="text-4xl md:text-5xl font-serif italic text-white/90">¿Buscas una ubicación específica?</h2>
                  <p className="text-stone-400 max-w-2xl mx-auto font-light leading-relaxed">Cada sucursal de Mokaf mantiene nuestros estándares de calidad pero con una atmósfera única inspirada en su arquitectura local.</p>
                </div>
                <div className="flex flex-wrap justify-center gap-6">
                   <button className="bg-[#d4b88d] text-black px-12 py-5 rounded-full font-black text-xs uppercase tracking-[0.3em] hover:bg-white transition-all duration-700 shadow-xl overflow-hidden group">
                      <span className="relative z-10 flex items-center gap-2">Ver Mapa Global <i className="fas fa-chevron-right text-[8px]"></i></span>
                   </button>
                </div>
             </div>
          </section>

        </div>
      </div>
    </div>
  );
}
