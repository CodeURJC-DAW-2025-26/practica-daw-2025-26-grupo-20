import { useLoaderData, Form, useActionData, Link } from "react-router";
import { useState } from "react";
import { API_BASE_URL } from "../config";

export async function loader() {
  try {
    const teamRes = await fetch(`${API_BASE_URL}/api/v1/about-us`, { credentials: "include" });
    const faqRes = await fetch(`${API_BASE_URL}/api/v1/faqs`, { credentials: "include" });
    
    const team = teamRes.ok ? await teamRes.json() : [];
    const faqs = faqRes.ok ? await faqRes.json() : [];
    
    return { team, faqs };
  } catch (error) {
    console.error("Error fetching contact data:", error);
    return { team: [], faqs: [] };
  }
}

export async function action({ request }: { request: Request }) {
  const formData = await request.formData();
  const data = Object.fromEntries(formData);
  
  const response = await fetch(`${API_BASE_URL}/api/v1/contact`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify(data),
  });
  
  if (!response.ok) {
    return { error: "Hubo un error al enviar el mensaje. Por favor, inténtalo de nuevo." };
  }
  
  return { success: true };
}

export default function Contact() {
  const { team, faqs } = useLoaderData<typeof loader>();
  const actionData = useActionData<typeof action>();
  const [openFaq, setOpenFaq] = useState<number | null>(null);

  return (
    <div className="bg-[#050404] min-h-screen text-white pb-32">
      <div className="container mx-auto px-4 sm:px-8 lg:px-12 pt-12 max-w-7xl font-sans">
        
        {/* Main Boutique Frame */}
        <div className="animate-fade-in bg-[#080707] border border-[#d4b88d]/10 rounded-[2rem] p-8 sm:p-12 lg:p-20 shadow-[0_40px_100px_rgba(0,0,0,0.8)] relative overflow-hidden flex flex-col gap-24">
          
          {/* Subtle background glow */}
          <div className="absolute -top-24 -right-24 w-96 h-96 bg-[#d4b88d]/5 blur-[120px] rounded-full pointer-events-none"></div>
          <div className="absolute -bottom-24 -left-24 w-96 h-96 bg-[#d4b88d]/5 blur-[120px] rounded-full pointer-events-none"></div>

          {/* Header Section */}
          <div className="text-center relative">
             <div className="inline-flex items-center gap-3 mb-6">
                <span className="h-[1px] w-8 bg-[#d4b88d]/30"></span>
                <span className="text-[10px] text-[#d4b88d] font-bold uppercase tracking-[0.5em]">Conectemos</span>
                <span className="h-[1px] w-8 bg-[#d4b88d]/30"></span>
             </div>
             <h1 className="text-5xl md:text-7xl font-serif text-[#d4b88d] italic tracking-tighter mb-8 drop-shadow-sm leading-tight">Estamos a un <br/><span className="text-white opacity-90">sorbo de distancia</span></h1>
          </div>

          <div className="grid lg:grid-cols-2 gap-20 relative z-10">
            {/* Left Column - Contact Form Boutique */}
            <div className="bg-[#0c0b0b] rounded-[2.5rem] p-10 md:p-14 border border-[#d4b88d]/10 shadow-2xl relative">
              <div className="space-y-6 mb-12">
                <h2 className="text-3xl font-serif italic text-white/90">Escríbenos</h2>
                <div className="w-12 h-[2px] bg-[#d4b88d]"></div>
              </div>

              {actionData?.success && (
                <div className="bg-green-500/10 border border-green-500/30 text-green-400 p-8 rounded-2xl mb-10 flex items-center gap-5 animate-bounce-in">
                  <div className="w-12 h-12 bg-green-500/20 rounded-full flex items-center justify-center">
                    <i className="fas fa-check text-xl"></i>
                  </div>
                  <div>
                    <p className="font-bold text-lg leading-none mb-1 text-white">¡Mensaje enviado!</p>
                    <p className="text-sm opacity-80">Te responderemos a la brevedad.</p>
                  </div>
                </div>
              )}

              {actionData?.error && (
                <div className="bg-red-500/10 border border-red-500/30 text-red-400 p-8 rounded-2xl mb-10 flex items-center gap-5 animate-shake text-white">
                  <div className="w-12 h-12 bg-red-500/20 rounded-full flex items-center justify-center">
                    <i className="fas fa-exclamation text-xl"></i>
                  </div>
                  <p className="font-bold">{actionData.error}</p>
                </div>
              )}

              <Form method="post" className="space-y-8">
                <div className="grid md:grid-cols-2 gap-8">
                  <div className="space-y-3">
                    <label className="text-[10px] font-bold uppercase tracking-[0.3em] text-stone-500 ml-1">Nombre</label>
                    <input name="firstName" required className="w-full bg-white/[0.03] border border-white/10 rounded-2xl px-6 py-4.5 focus:border-[#d4b88d] focus:bg-white/[0.05] outline-none transition-all text-white placeholder:text-stone-700 font-light" placeholder="Juan" />
                  </div>
                  <div className="space-y-3">
                    <label className="text-[10px] font-bold uppercase tracking-[0.3em] text-stone-500 ml-1">Apellidos</label>
                    <input name="lastName" className="w-full bg-white/[0.03] border border-white/10 rounded-2xl px-6 py-4.5 focus:border-[#d4b88d] focus:bg-white/[0.05] outline-none transition-all text-white placeholder:text-stone-700 font-light" placeholder="Pérez" />
                  </div>
                </div>

                <div className="grid md:grid-cols-2 gap-8">
                  <div className="space-y-3">
                    <label className="text-[10px] font-bold uppercase tracking-[0.3em] text-stone-500 ml-1">Email</label>
                    <input name="email" type="email" required className="w-full bg-white/[0.03] border border-white/10 rounded-2xl px-6 py-4.5 focus:border-[#d4b88d] focus:bg-white/[0.05] outline-none transition-all text-white placeholder:text-stone-700 font-light" placeholder="juan@example.com" />
                  </div>
                  <div className="space-y-3">
                    <label className="text-[10px] font-bold uppercase tracking-[0.3em] text-stone-500 ml-1">Teléfono</label>
                    <input name="phone" type="tel" className="w-full bg-white/[0.03] border border-white/10 rounded-2xl px-6 py-4.5 focus:border-[#d4b88d] focus:bg-white/[0.05] outline-none transition-all text-white placeholder:text-stone-700 font-light" placeholder="+34 600 000 000" />
                  </div>
                </div>

                <div className="space-y-3">
                  <label className="text-[10px] font-bold uppercase tracking-[0.3em] text-stone-500 ml-1">Asunto</label>
                  <div className="relative">
                    <select name="subject" required className="w-full bg-white/[0.03] border border-white/10 rounded-2xl px-6 py-4.5 focus:border-[#d4b88d] focus:bg-white/[0.05] outline-none transition-all text-white font-light appearance-none">
                      <option value="" disabled className="bg-[#0c0b0b]">Selecciona un asunto</option>
                      <option value="general" className="bg-[#0c0b0b]">Consulta general</option>
                      <option value="reservation" className="bg-[#0c0b0b]">Reserva de mesa</option>
                      <option value="event" className="bg-[#0c0b0b]">Eventos y catering</option>
                      <option value="product" className="bg-[#0c0b0b]">Información sobre productos</option>
                    </select>
                    <div className="absolute right-6 top-1/2 -translate-y-1/2 pointer-events-none text-[#d4b88d]">
                      <i className="fas fa-chevron-down text-[10px]"></i>
                    </div>
                  </div>
                </div>

                <div className="space-y-3">
                  <label className="text-[10px] font-bold uppercase tracking-[0.3em] text-stone-500 ml-1">Mensaje</label>
                  <textarea name="message" required rows={6} className="w-full bg-white/[0.03] border border-white/10 rounded-2xl px-6 py-5 focus:border-[#d4b88d] focus:bg-white/[0.05] outline-none transition-all text-white placeholder:text-stone-700 font-light resize-none" placeholder="Escribe tus pensamientos aquí..."></textarea>
                </div>

                <button type="submit" className="w-full bg-[#d4b88d] text-black font-black uppercase tracking-[0.35em] text-[11px] py-6 rounded-2xl shadow-[0_20px_60px_rgba(212,184,141,0.2)] hover:bg-white transition-all duration-700 flex items-center justify-center gap-4 group mt-4 transform active:scale-95">
                  <span>Enviar Mensaje</span>
                  <i className="fas fa-paper-plane group-hover:translate-x-1 group-hover:-translate-y-1 transition-transform text-[10px]"></i>
                </button>
              </Form>
            </div>

            {/* Right Column - Contact Info & FAQ */}
            <div className="flex flex-col gap-16">
              
              {/* Quick Contact Grid */}
              <div className="grid sm:grid-cols-2 gap-8">
                <div className="bg-[#0c0b0b] border border-[#d4b88d]/10 p-10 rounded-[2.5rem] space-y-6 group transition-all duration-500 hover:border-[#d4b88d]/40 shadow-2xl">
                  <div className="w-14 h-14 bg-[#d4b88d]/10 text-[#d4b88d] rounded-2xl flex items-center justify-center text-xl transition-all duration-500 group-hover:bg-[#d4b88d] group-hover:text-black">
                    <i className="fas fa-shop text-sm"></i>
                  </div>
                  <h5 className="text-[11px] font-bold uppercase tracking-[0.4em] text-stone-500">Ubicaciones</h5>
                  <Link to="/branches" className="inline-flex items-center gap-3 text-white font-serif italic text-2xl group-hover:text-[#d4b88d] transition-colors">
                    Ver Mapa <i className="fas fa-arrow-right text-[10px]"></i>
                  </Link>
                </div>

                <div className="bg-[#0c0b0b] border border-[#d4b88d]/10 p-10 rounded-[2.5rem] space-y-6 group transition-all duration-500 hover:border-[#d4b88d]/40 shadow-2xl">
                  <div className="w-14 h-14 bg-[#d4b88d]/10 text-[#d4b88d] rounded-2xl flex items-center justify-center text-xl transition-all duration-500 group-hover:bg-[#d4b88d] group-hover:text-black">
                    <i className="fas fa-phone-volume text-sm"></i>
                  </div>
                  <h5 className="text-[11px] font-bold uppercase tracking-[0.4em] text-stone-500">Directo</h5>
                  <div className="space-y-1">
                    <p className="text-xl font-bold text-white tracking-tight">+34 910 123 456</p>
                    <p className="text-sm font-light text-stone-500 italic">info@mokaf.com</p>
                  </div>
                </div>
              </div>

              {/* Social Networking Section */}
              <div className="bg-gradient-to-br from-[#0c0b0b] to-[#080707] border border-white/5 p-12 rounded-[2.5rem] shadow-2xl space-y-10 relative overflow-hidden group">
                <div className="absolute top-0 right-0 w-32 h-32 bg-[#d4b88d]/5 blur-3xl pointer-events-none"></div>
                <h5 className="text-center text-[10px] font-bold uppercase tracking-[0.6em] text-[#d4b88d]">Comunidad Mokaf</h5>
                <div className="flex justify-center gap-8">
                  {['instagram', 'facebook-f', 'twitter', 'tiktok', 'whatsapp'].map(icon => (
                    <a key={icon} href="#" className="w-14 h-14 rounded-2xl bg-white/5 flex items-center justify-center text-stone-400 hover:bg-[#d4b88d] hover:text-black hover:-translate-y-2 transition-all duration-500 shadow-xl border border-white/5">
                      <i className={`fab fa-${icon} text-lg`}></i>
                    </a>
                  ))}
                </div>
              </div>

              {/* FAQ Section - Clean & Elegant */}
              <div className="space-y-10">
                <div className="flex items-center gap-5">
                  <h3 className="text-[11px] font-bold uppercase tracking-[0.5em] text-stone-500">Consultas Comunes</h3>
                  <div className="h-[1px] flex-grow bg-white/10"></div>
                </div>
                
                <div className="space-y-6">
                  {faqs.map((faq: any) => (
                    <div key={faq.id} className="border-b border-white/5 group overflow-hidden">
                      <button 
                        onClick={() => setOpenFaq(openFaq === faq.id ? null : faq.id)}
                        className="w-full py-6 flex justify-between items-center text-left transition-all group-hover:pl-2"
                      >
                        <span className={`text-lg font-serif italic tracking-tight font-medium ${openFaq === faq.id ? 'text-[#d4b88d]' : 'text-white/80 group-hover:text-white'}`}>{faq.question}</span>
                        <div className={`w-8 h-8 rounded-full border border-white/10 flex items-center justify-center transition-all ${openFaq === faq.id ? 'bg-[#d4b88d] border-[#d4b88d] text-black rotate-180' : 'text-[#d4b88d]'}`}>
                           <i className={`fas fa-chevron-down text-[10px]`}></i>
                        </div>
                      </button>
                      <div className={`transition-all duration-700 ease-in-out ${openFaq === faq.id ? 'max-h-96 opacity-100 mb-8' : 'max-h-0 opacity-0 pointer-events-none'}`}>
                        <p className="text-stone-500 leading-relaxed font-light text-[15px] pl-1 border-l border-[#d4b88d]/30 italic">
                          "{faq.answer}"
                        </p>
                      </div>
                    </div>
                  ))}
                  
                  {faqs.length === 0 && (
                    <div className="flex flex-col items-center gap-4 py-12 bg-white/[0.01] rounded-3xl border border-dashed border-white/10 text-stone-600">
                       <i className="fas fa-sparkles fa-2x opacity-20"></i>
                       <p className="text-[11px] font-bold uppercase tracking-[0.4em]">Preparando respuestas maestros...</p>
                    </div>
                  )}
                </div>
              </div>

            </div>
          </div>

          {/* Support Team Section - Small Polish */}
          <section className="pt-16 border-t border-white/5 space-y-20">
            <div className="text-center group">
               <h2 className="text-[11px] font-bold uppercase tracking-[0.6em] text-stone-500 mb-4 group-hover:text-[#d4b88d] transition-colors">Soporte Concierge</h2>
               <div className="w-12 h-0.5 bg-[#d4b88d] mx-auto opacity-40"></div>
            </div>

            <div className="grid sm:grid-cols-2 md:grid-cols-4 gap-12">
              {team.map((member: any) => (
                <div key={member.id} className="text-center group space-y-6 p-8 rounded-[2.5rem] transition-all duration-500 hover:bg-white/[0.02]">
                  <div className="relative inline-block mx-auto">
                    <div className="absolute inset-0 bg-[#d4b88d]/20 rounded-full blur-3xl opacity-0 group-hover:opacity-100 transition-opacity"></div>
                    <img 
                      src={member.profileImageUrl || `https://i.pravatar.cc/150?u=${member.id}`} 
                      alt={member.firstName} 
                      className="relative w-32 h-32 rounded-full mx-auto object-cover ring-2 ring-white/5 group-hover:ring-[#d4b88d]/50 transition-all duration-500 shadow-2xl" 
                    />
                  </div>
                  <div className="space-y-1.5">
                    <h5 className="text-xl font-serif italic text-white/90 group-hover:text-[#d4b88d] transition-colors">{member.firstName} {member.lastName}</h5>
                    <p className="text-[#d4b88d]/60 text-[9px] uppercase font-bold tracking-[0.4em] mb-3">{member.position}</p>
                    <p className="text-stone-500 text-[13px] italic font-light line-clamp-2 opacity-80 group-hover:opacity-100 transition-opacity px-2 line-clamp-2">"{member.description}"</p>
                  </div>
                </div>
              ))}
            </div>
          </section>

        </div>
      </div>
    </div>
  );
}
