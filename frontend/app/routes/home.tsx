import { Link } from "react-router";

export default function Home() {
  return (
    <div className="bg-mokaf-bg text-stone-200">
      <section className="container mx-auto px-6 pt-6 pb-20">
        {/* Tight Gold Frame */}
        <div className="relative border border-[#c6a87d]/30 rounded-2xl p-2 md:p-4 shadow-2xl">
           
           <div className="relative w-full h-[400px] md:h-[450px] overflow-hidden rounded-xl">
              <img 
                src="https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=1600&auto=format&fit=crop&q=80" 
                className="w-full h-full object-cover" 
                alt="Banner café" 
              />
              <div className="absolute inset-0 bg-gradient-to-r from-black/80 via-black/40 to-transparent z-10"></div>
              
              <div className="absolute inset-0 z-20 flex flex-col justify-center px-12 md:px-20 space-y-8">
                 {/* Tag exactly like screenshot */}
                 <div className="bg-[#c6a87d] text-black px-4 py-1.5 rounded-full text-[9px] font-bold uppercase tracking-[0.2em] w-fit">
                    Bienvenidos a Mokaf
                 </div>

                 {/* Title with the horizontal decorative line */}
                 <div className="relative flex items-center gap-6">
                    <div className="w-16 h-[1px] bg-[#c6a87d] mt-2"></div>
                    <h1 className="text-3xl md:text-[2.6rem] font-serif font-medium text-white leading-none tracking-tight">
                       Descubre el Arte del <span className="text-[#c6a87d]">Café de Especialidad</span>
                    </h1>
                 </div>

                 <p className="text-stone-300 text-sm md:text-base font-light opacity-80 max-w-lg">
                    Donde cada taza cuenta una historia de pasión, calidad y artesanía.
                 </p>

                 <div className="pt-2">
                    <Link 
                      to="/menu" 
                      className="bg-[#c6a87d]/80 hover:bg-[#c6a87d] text-black px-8 py-3 rounded-[4px] font-bold uppercase text-[10px] tracking-widest transition-all inline-flex items-center gap-3 group"
                    >
                       Ver Menú
                       <i className="fas fa-arrow-right text-[10px] group-hover:translate-x-1 transition-transform"></i>
                    </Link>
                 </div>
              </div>
           </div>
        </div>
      </section>
    </div>
  );
}
