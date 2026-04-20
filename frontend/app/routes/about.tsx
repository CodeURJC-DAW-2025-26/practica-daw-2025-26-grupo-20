import { useLoaderData, Link } from "react-router";
import { API_BASE_URL } from "../config";

export async function loader() {
  try {
    const response = await fetch(`${API_BASE_URL}/api/v1/about-us`, { credentials: "include" });
    if (!response.ok) return { team: [] };
    const team = await response.json();
    return { team };
  } catch (error) {
    console.error("Error fetching team:", error);
    return { team: [] };
  }
}

export default function About() {
  const { team } = useLoaderData<typeof loader>();

  return (
    <div className="bg-[#050404] min-h-screen text-white pb-32">
      <div className="container mx-auto px-4 sm:px-8 lg:px-12 pt-12 max-w-7xl">
        {/* Main Boutique Frame */}
        <div className="animate-fade-in bg-[#080707] border border-[#d4b88d]/10 rounded-[2rem] p-8 sm:p-12 lg:p-20 shadow-[0_40px_100px_rgba(0,0,0,0.8)] relative overflow-hidden flex flex-col gap-32">
          
          {/* Subtle background glow */}
          <div className="absolute -top-24 -right-24 w-96 h-96 bg-[#d4b88d]/5 blur-[120px] rounded-full pointer-events-none"></div>
          <div className="absolute -bottom-24 -left-24 w-96 h-96 bg-[#d4b88d]/5 blur-[120px] rounded-full pointer-events-none"></div>

          {/* Header Section */}
          <div className="text-center relative">
             <h1 className="text-5xl md:text-7xl font-serif text-[#d4b88d] italic tracking-tighter mb-8 drop-shadow-sm">Nuestra Esencia</h1>
             <div className="w-24 h-[1px] bg-gradient-to-r from-transparent via-[#d4b88d]/40 to-transparent mx-auto"></div>
          </div>

          {/* History Section - Boutique Style */}
          <section className="grid lg:grid-cols-2 gap-20 items-center relative z-10">
            <div className="space-y-10 order-2 lg:order-1">
              <div className="space-y-4">
                <span className="text-[10px] text-[#d4b88d] font-bold uppercase tracking-[0.5em] opacity-80">Desde 2010</span>
                <h2 className="text-4xl md:text-5xl font-serif text-white/90 italic leading-tight">Mokaf nació de un sueño bajo el sol del grano.</h2>
              </div>
              
              <div className="space-y-6 text-stone-200 text-lg leading-relaxed font-light">
                <p>
                  Lo que comenzó como un pequeño rincón para los amantes del espresso en el corazón del barrio, se ha transformado en un santuario del <span className="text-[#f3e5d0] font-bold">café de especialidad</span>. En Mokaf, no solo servimos tazas; destilamos pasiones.
                </p>
                <p>
                  Cada grano es seleccionado meticulosamente, trabajando codo con codo con pequeños agricultores que comparten nuestra devoción por la tierra y el arte del tostado perfecto. Nuestra misión es simple: que cada sorbo sea un viaje sensorial inolvidable.
                </p>
              </div>

              <div className="pt-6">
                <Link to="/menu" className="group inline-flex items-center gap-4 bg-transparent border border-[#d4b88d]/30 text-[#d4b88d] px-10 py-4 rounded-full font-bold text-[13px] uppercase tracking-widest hover:bg-[#d4b88d] hover:text-black transition-all duration-700 shadow-xl">
                  Explora la Carta <i className="fas fa-arrow-right text-[10px] group-hover:translate-x-2 transition-transform"></i>
                </Link>
              </div>
            </div>

            <div className="relative group p-4 order-1 lg:order-2">
              <div className="absolute inset-0 border border-[#d4b88d]/20 rounded-[2.5rem] transform rotate-3 group-hover:rotate-0 transition-transform duration-1000"></div>
              <div className="relative aspect-[4/5] rounded-[2rem] overflow-hidden shadow-2xl ring-1 ring-white/10">
                <img 
                  src="https://images.unsplash.com/photo-1509042239860-f550ce710b93?ixlib=rb-1.2.1&auto=format&fit=crop&w=1200&q=90" 
                  alt="Mokaf Interior Design"
                  className="w-full h-full object-cover transition-transform duration-[2s] group-hover:scale-110"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent opacity-40"></div>
              </div>
            </div>
          </section>

          {/* Stats Section with Glassmorphism */}
          <section className="grid grid-cols-2 md:grid-cols-4 gap-4 sm:gap-10">
            {[
              { label: "Años de Excelencia", value: "12+" },
              { label: "Orígenes Únicos", value: "8" },
              { label: "Tazas Servidas", value: "50k+" },
              { label: "Puntuación SCA", value: "88+" }
            ].map((stat, idx) => (
              <div key={idx} className="group bg-white/[0.02] border border-white/5 rounded-3xl p-10 text-center transition-all duration-500 hover:bg-white/[0.05] hover:border-[#d4b88d]/30 hover:shadow-[0_20px_50px_rgba(0,0,0,0.3)]">
                <div className="text-4xl md:text-5xl font-black text-[#d4b88d] mb-4 tracking-tighter group-hover:scale-110 transition-transform">{stat.value}</div>
                <div className="text-[10px] uppercase tracking-[0.3em] text-stone-500 font-bold">{stat.label}</div>
              </div>
            ))}
          </section>

          {/* Values Section - Cards Boutique */}
          <section className="space-y-24">
            <div className="text-center space-y-4">
              <h2 className="text-3xl md:text-4xl font-serif text-white/90 italic tracking-tight uppercase">Nuestros Pilares</h2>
              <div className="w-12 h-[2px] bg-[#d4b88d] mx-auto rounded-full"></div>
            </div>

            <div className="grid md:grid-cols-3 gap-10">
              {[
                { 
                  icon: "fa-leaf", 
                  title: "Sostenibilidad", 
                  desc: "Desde el cultivo regenerativo hasta el packaging compostable. Cuidamos el mundo que nos regala el mejor café." 
                },
                { 
                  icon: "fa-award", 
                  title: "Artesanía", 
                  desc: "Tostamos cada lote de forma manual en nuestra tostadora Probat de 1965, respetando la identidad de cada origen." 
                },
                { 
                  icon: "fa-users", 
                  title: "Comunidad", 
                  desc: "Mokaf no es solo un local, es un punto de encuentro para mentes inquietas y amantes de lo auténtico." 
                }
              ].map((val, idx) => (
                <div key={idx} className="group bg-[#0c0b0b] border border-[#d4b88d]/5 p-12 rounded-[2.5rem] transition-all duration-700 hover:bg-[#121111] hover:border-[#d4b88d]/30 hover:-translate-y-2 shadow-2xl">
                  <div className="w-16 h-16 bg-[#d4b88d]/10 text-[#d4b88d] rounded-2xl flex items-center justify-center mb-8 text-2xl group-hover:bg-[#d4b88d] group-hover:text-black transition-all duration-500 shadow-inner">
                    <i className={`fas ${val.icon}`}></i>
                  </div>
                  <h4 className="text-2xl font-serif italic text-white mb-5 group-hover:text-[#d4b88d] transition-colors">{val.title}</h4>
                  <p className="text-stone-300 text-[16px] font-light leading-relaxed opacity-100 transition-opacity">{val.desc}</p>
                </div>
              ))}
            </div>
          </section>

          {/* Team Section - High Fidelity Dynamic */}
          <section className="space-y-24 py-16">
            <div className="flex flex-col items-center gap-6">
              <div className="flex items-center gap-4">
                <div className="h-[1px] w-12 bg-[#d4b88d]/30"></div>
                <h2 className="text-2xl font-serif text-[#d4b88d] italic">Artesanos detrás de tu taza</h2>
                <div className="h-[1px] w-12 bg-[#d4b88d]/30"></div>
              </div>
            </div>

            <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-12">
              {team.map((member: any) => (
                <div key={member.id} className="group flex flex-col items-center text-center space-y-8 bg-[#0a0909] p-10 rounded-[3rem] border border-white/5 transition-all duration-700 hover:border-[#d4b88d]/20 hover:bg-[#0f0e0d] hover:-translate-y-2">
                  <div className="relative">
                    <div className="absolute inset-x-0 bottom-0 top-1/2 bg-[#d4b88d]/20 blur-3xl opacity-0 group-hover:opacity-100 transition-opacity duration-1000"></div>
                    <div className="relative w-44 h-44 rounded-full overflow-hidden p-[2px] bg-gradient-to-tr from-[#d4b88d]/40 to-transparent group-hover:from-[#d4b88d] transition-all duration-1000">
                      <img 
                        src={member.profileImageUrl || `https://i.pravatar.cc/150?u=${member.id}`} 
                        alt={`${member.firstName} ${member.lastName}`} 
                        className="w-full h-full rounded-full object-cover transition-transform duration-[1.5s] group-hover:scale-110" 
                      />
                    </div>
                  </div>
                  <div className="space-y-3">
                    <h4 className="text-2xl font-serif italic text-white group-hover:text-[#d4b88d] transition-colors">{member.firstName} {member.lastName}</h4>
                    <p className="text-[#d4b88d] font-black text-[10px] uppercase tracking-[0.4em]">{member.position || 'Staff'}</p>
                    <p className="text-stone-300 text-[15px] leading-relaxed italic opacity-100 line-clamp-2 transition-opacity">"{member.description || 'Devoto del arte del espresso.'}"</p>
                  </div>
                </div>
              ))}
              
              {team.length === 0 && (
                <div className="col-span-full py-20 bg-white/5 rounded-3xl border border-white/5 text-center">
                   <div className="animate-pulse flex flex-col items-center gap-4 text-stone-600">
                      <i className="fas fa-coffee-bean fa-2x"></i>
                      <p className="text-[12px] font-bold uppercase tracking-[0.4em]">Preparando nuestra alma...</p>
                   </div>
                </div>
              )}
            </div>
          </section>

          {/* Join Us CTA */}
          <section className="mt-12 text-center py-24 bg-gradient-to-b from-[#0c0b0b] to-transparent rounded-[4rem] border-t border-white/5 relative overflow-hidden">
             <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?ixlib=rb-1.2.1&auto=format&fit=crop&w=1920&q=20')] opacity-5 grayscale pointer-events-none"></div>
             <div className="relative z-10 space-y-12">
                <div className="space-y-4">
                  <h2 className="text-4xl md:text-5xl font-serif italic text-white/90">¿Quieres ser parte de la historia?</h2>
                  <p className="text-stone-400 max-w-2xl mx-auto font-light leading-relaxed">Constantemente buscamos baristas y apasionados que quieran elevar el estándar del café de especialidad junto a nosotros.</p>
                </div>
                <Link to="/contact" className="inline-block bg-[#d4b88d] text-black px-12 py-5 rounded-full font-black text-xs uppercase tracking-[0.3em] hover:bg-white transition-all duration-700 shadow-[0_20px_60px_rgba(212,184,141,0.2)] hover:scale-105 active:scale-95">
                  Contáctanos Ahora
                </Link>
             </div>
          </section>

        </div>
      </div>
    </div>
  );
}
