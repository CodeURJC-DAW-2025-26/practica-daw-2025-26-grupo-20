import { useLoaderData, Form, useActionData } from "react-router";
import { useState, useRef, useEffect } from "react";
import { Link } from "react-router";
import { API_BASE_URL } from "../config";

export async function clientLoader() {
  try {
    const [teamRes, faqRes] = await Promise.all([
      fetch(`${API_BASE_URL}/api/v1/about-us`, { credentials: "include" }),
      fetch(`${API_BASE_URL}/api/v1/faqs`, { credentials: "include" }),
    ]);
    const team = teamRes.ok ? await teamRes.json() : [];
    const faqs = faqRes.ok ? await faqRes.json() : [];
    return { team, faqs };
  } catch (error) {
    return { team: [], faqs: [] };
  }
}

export async function clientAction({ request }: { request: Request }) {
  const formData = await request.formData();
  const data = Object.fromEntries(formData);
  const payload = { ...data, newsletter: data.newsletter === "on" || data.newsletter === "true" };

  const response = await fetch(`${API_BASE_URL}/api/v1/contact`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify(payload),
  });

  if (!response.ok) return { error: "Hubo un error al enviar el mensaje. Por favor, inténtalo de nuevo." };
  return { success: true };
}

export default function Contact() {
  const { team, faqs } = useLoaderData<typeof clientLoader>();
  const actionData = useActionData<typeof clientAction>();
  const [openFaq, setOpenFaq] = useState<number | null>(null);
  const formRef = useRef<HTMLFormElement>(null);

  useEffect(() => {
    if (actionData?.success && formRef.current) formRef.current.reset();
  }, [actionData]);

  return (
    <div className="bg-[#050404] min-h-screen text-white pb-32 pt-10 font-sans">
      <div className="container mx-auto px-4 max-w-7xl">
        <div className="border border-[#d4b88d]/30 rounded-[1.5rem] p-6 md:p-8 lg:p-10 relative">
          <div className="grid lg:grid-cols-12 gap-8 relative z-10">
            <div className="lg:col-span-7 bg-[rgba(45,35,25,0.9)] border border-[#d4b88d]/30 rounded-2xl p-8 md:p-10 flex flex-col">
              <div className="flex items-center gap-4 mb-6">
                <i className="fas fa-envelope text-4xl text-white/90"></i>
                <h1 className="text-4xl text-white/90 font-medium tracking-tight">Contáctanos</h1>
              </div>
              <p className="text-[#d4b88d]/60 text-[13px] leading-relaxed mb-10 max-w-xl">¿Tienes alguna pregunta, sugerencia o comentario? Estamos aquí para ayudarte.</p>

              {actionData?.success && (
                <div className="bg-green-500/10 border border-green-500/30 text-green-400 p-4 rounded-xl mb-8 flex items-center gap-4 text-sm">
                  <i className="fas fa-check-circle text-lg"></i><span>¡Mensaje enviado correctamente!</span>
                </div>
              )}
              {actionData?.error && (
                <div className="bg-red-500/10 border border-red-500/30 text-red-400 p-4 rounded-xl mb-8 flex items-center gap-4 text-sm">
                  <i className="fas fa-exclamation-triangle text-lg"></i><span>{actionData.error}</span>
                </div>
              )}

              <Form ref={formRef} method="post" className="space-y-6 flex-grow flex flex-col">
                <div className="grid md:grid-cols-2 gap-8">
                  <div className="space-y-2">
                    <label className="text-white text-xs font-medium ml-1">Nombre *</label>
                    <input name="firstName" required className="w-full bg-transparent border border-[#d4b88d]/20 rounded-lg px-4 py-3 focus:border-[#d4b88d] focus:bg-white/5 outline-none transition-all text-white text-sm" />
                  </div>
                  <div className="space-y-2">
                    <label className="text-white text-xs font-medium ml-1">Apellidos</label>
                    <input name="lastName" className="w-full bg-transparent border border-[#d4b88d]/20 rounded-lg px-4 py-3 focus:border-[#d4b88d] focus:bg-white/5 outline-none transition-all text-white text-sm" />
                  </div>
                </div>
                <div className="grid md:grid-cols-2 gap-8">
                  <div className="space-y-2">
                    <label className="text-white text-xs font-medium ml-1">Email *</label>
                    <input name="email" type="email" required className="w-full bg-transparent border border-[#d4b88d]/20 rounded-lg px-4 py-3 focus:border-[#d4b88d] focus:bg-white/5 outline-none transition-all text-white text-sm" />
                  </div>
                  <div className="space-y-2">
                    <label className="text-white text-xs font-medium ml-1">Teléfono</label>
                    <input name="phone" type="tel" className="w-full bg-transparent border border-[#d4b88d]/20 rounded-lg px-4 py-3 focus:border-[#d4b88d] focus:bg-white/5 outline-none transition-all text-white text-sm" />
                  </div>
                </div>
                <div className="space-y-2">
                  <label className="text-white text-xs font-medium ml-1">Asunto *</label>
                  <div className="relative">
                    <select name="subject" required className="w-full bg-transparent border border-[#d4b88d]/20 rounded-lg px-4 py-3 focus:border-[#d4b88d] outline-none transition-all text-white text-sm appearance-none">
                      <option value="Trabaja con nosotros" className="bg-[#050404]">Trabaja con nosotros</option>
                      <option value="Consulta general" className="bg-[#050404]">Consulta general</option>
                      <option value="Reserva" className="bg-[#050404]">Reserva</option>
                      <option value="Eventos" className="bg-[#050404]">Eventos y catering</option>
                    </select>
                    <div className="absolute right-4 top-1/2 -translate-y-1/2 pointer-events-none text-[#d4b88d]/60"><i className="fas fa-chevron-down text-xs"></i></div>
                  </div>
                </div>
                <div className="space-y-2">
                  <label className="text-white text-xs font-medium ml-1">Mensaje *</label>
                  <textarea name="message" required rows={6} placeholder="Escribe tu mensaje aquí..." className="w-full bg-transparent border border-[#d4b88d]/20 rounded-lg px-4 py-3 focus:border-[#d4b88d] outline-none transition-all text-white text-sm resize-none"></textarea>
                </div>
                <div className="flex items-start gap-3 mt-4">
                  <input name="newsletter" id="newsletter" type="checkbox" className="w-4 h-4 mt-0.5 bg-transparent border border-[#d4b88d]/30 rounded accent-[#d4b88d]" />
                  <label htmlFor="newsletter" className="text-[#d4b88d]/70 text-xs leading-tight">Quiero suscribirme a la newsletter de Mokaf.</label>
                </div>
                <div className="mt-8">
                  <button type="submit" className="w-full sm:w-auto px-8 bg-[#d4b88d] text-[#050404] font-bold text-sm py-3 rounded-lg hover:bg-white transition-all duration-300">Enviar Mensaje</button>
                </div>
              </Form>
            </div>

            <div className="lg:col-span-5 flex flex-col gap-6">
              <div className="bg-[rgba(45,35,25,0.9)] border border-[#d4b88d]/30 rounded-2xl p-8 text-center flex flex-col items-center">
                <i className="fas fa-store text-[#2563eb] text-3xl mb-3"></i>
                <h3 className="text-white font-bold text-lg mb-2">Nuestras Sucursales</h3>
                <p className="text-[#d4b88d]/70 text-xs mb-6 px-4 leading-relaxed">Descubre dónde encontrarnos y nuestros horarios de atención.</p>
                <Link to="/branches" className="w-full bg-[#2563eb] hover:bg-blue-600 text-white font-medium py-2.5 rounded-lg transition-colors flex justify-center items-center gap-2 text-sm">
                  <i className="fas fa-map-marked-alt"></i> Ver Sucursales
                </Link>
              </div>
              <div className="bg-[rgba(45,35,25,0.9)] border border-[#d4b88d]/30 rounded-2xl p-8 text-center flex flex-col items-center">
                <i className="fas fa-phone text-[#d4b88d]/70 text-3xl mb-3 transform rotate-[20deg]"></i>
                <h3 className="text-white font-bold text-lg mb-4">Contacto Directo</h3>
                <div className="space-y-1.5 text-[#d4b88d]/60 text-sm">
                  <p className="flex items-center justify-center gap-2"><i className="fas fa-phone-alt opacity-80"></i> +34 910 123 456</p>
                  <p className="flex items-center justify-center gap-2"><i className="fas fa-envelope opacity-80"></i> info@mokaf.com</p>
                </div>
              </div>
              <div className="bg-[rgba(45,35,25,0.9)] border border-[#d4b88d]/30 rounded-2xl p-8 flex flex-col items-center">
                <h3 className="text-white font-medium text-base mb-6">Síguenos en Redes</h3>
                <div className="flex gap-4 mb-4">
                  {[{ bg: "#1877F2", icon: "fab fa-facebook-f" }, { bg: "gradient-to-tr from-[#f9ce34] via-[#ee2a7b] to-[#6228d7]", icon: "fab fa-instagram" }, { bg: "#1DA1F2", icon: "fab fa-twitter" }].map((s, i) => (
                    <a key={i} href="#" className="w-10 h-10 rounded-full flex items-center justify-center hover:-translate-y-1 transition-transform" style={{ background: s.bg }}>
                      <i className={`${s.icon} text-white text-lg`}></i>
                    </a>
                  ))}
                </div>
                <p className="text-[#d4b88d]/70 text-[11px] font-medium mt-1">#MokafCafe #CafeDeEspecialidad</p>
              </div>
              <div className="mt-2">
                <div className="flex items-center gap-2 text-white/80 font-medium text-sm mb-4 px-1">
                  <i className="fas fa-question-circle"></i><span>Preguntas Frecuentes</span>
                </div>
                <div className="space-y-3">
                  {(faqs.length > 0 ? faqs.slice(0, 3) : [{ id: 0, question: "¿Hacen envíos a domicilio?", answer: "" }]).map((faq: any) => (
                    <div key={faq.id} className="bg-[rgba(45,35,25,0.9)] border border-[#d4b88d]/20 rounded-lg overflow-hidden">
                      <button onClick={() => setOpenFaq(openFaq === faq.id ? null : faq.id)} className="w-full px-4 py-3 flex items-center gap-2 text-left">
                        <i className={`fas fa-chevron-${openFaq === faq.id ? 'down' : 'right'} text-[10px] text-[#d4b88d]`}></i>
                        <span className="text-[#d4b88d]/80 text-sm font-medium pr-4">{faq.question}</span>
                      </button>
                      {faq.answer && (
                        <div className={`transition-all duration-300 px-4 overflow-hidden ${openFaq === faq.id ? 'max-h-40 pb-4 opacity-100' : 'max-h-0 opacity-0'}`}>
                          <p className="text-stone-400 text-xs leading-relaxed border-l border-[#d4b88d]/30 pl-3">{faq.answer}</p>
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
