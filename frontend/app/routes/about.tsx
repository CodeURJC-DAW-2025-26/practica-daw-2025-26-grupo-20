import { useLoaderData, Link } from "react-router";
import { API_BASE_URL } from "../config";

export async function clientLoader() {
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
  const { team } = useLoaderData<typeof clientLoader>();

  return (
    <main className="container my-5 pb-5">
      <div
        className="p-4 p-md-5 rounded-4 shadow-lg mx-auto"
        style={{
          backgroundColor: "transparent",
          border: "1px solid rgba(198, 168, 125, 0.3)",
          maxWidth: "1100px",
        }}
      >

        <section className="mb-5">
          <h2 className="mb-2" style={{ color: "var(--dorado)", fontWeight: 500, letterSpacing: "0.5px" }}>
            Nuestra Historia
          </h2>
          <div
            className="mb-4"
            style={{ width: "40px", height: "2px", backgroundColor: "var(--dorado)" }}
          />

          <div className="row align-items-center g-5">
            
            <div className="col-lg-6">
              <div className="d-flex flex-column gap-3">
                <p style={{ color: "var(--beige)", opacity: 0.9, lineHeight: 1.7, fontSize: "0.95rem" }}>
                  Más que una cafetería, somos una{" "}
                  <span style={{ color: "var(--dorado)", fontWeight: 600 }}>familia apasionada</span>{" "}
                  por el buen café.
                </p>
                <p style={{ color: "var(--beige)", opacity: 0.8, lineHeight: 1.7, fontSize: "0.9rem" }}>
                  Fundada en 2010,{" "}
                  <span style={{ color: "var(--dorado)", fontWeight: 600 }}>Mokaf</span> nació con una
                  misión simple: transformar la forma en que las personas experimentan el café. Lo que
                  comenzó como un pequeño rincón para los amantes del espresso se ha convertido en un
                  referente del café de especialidad.
                </p>
                <p style={{ color: "var(--beige)", opacity: 0.8, lineHeight: 1.7, fontSize: "0.9rem" }}>
                  Nos enorgullecemos de trabajar directamente con agricultores locales y de comercio
                  justo, asegurando que cada grano cuente una historia de sostenibilidad y excelencia.
                  Nuestros baristas no solo sirven café; crean experiencias, perfeccionando cada taza
                  con técnica y pasión.
                </p>
                <div className="mt-2">
                  <Link
                    to="/menu"
                    className="btn"
                    style={{
                      backgroundColor: "var(--cafe-oscuro)",
                      color: "var(--beige)",
                      border: "1px solid rgba(198,168,125,0.4)",
                      fontSize: "0.85rem",
                      padding: "0.5rem 1.4rem",
                      borderRadius: "6px",
                    }}
                  >
                    Descubre Nuestros Sabores{" "}
                    <i className="fas fa-arrow-right ms-2" style={{ fontSize: "0.75rem" }}></i>
                  </Link>
                </div>
              </div>
            </div>

            <div className="col-lg-6">
              <img
                src="https://images.unsplash.com/photo-1509042239860-f550ce710b93?ixlib=rb-1.2.1&auto=format&fit=crop&w=1200&q=90"
                alt="Interior de Mokaf"
                className="img-fluid rounded-3 shadow w-100"
                style={{
                  border: "1px solid rgba(198,168,125,0.15)",
                  objectFit: "cover",
                  maxHeight: "320px",
                }}
              />
            </div>
          </div>
        </section>

        <div
          className="my-5"
          style={{
            height: "1px",
            background: "linear-gradient(90deg, transparent, rgba(198,168,125,0.4), transparent)",
          }}
        />

        <div className="row g-4 text-center mb-5">
          {[
            { label: "Años de Experiencia", value: "10+" },
            { label: "Premios Nacionales",  value: "5"   },
            { label: "Clientes Felices",    value: "15k+" },
            { label: "Café Orgánico",       value: "100%" },
          ].map((stat, idx) => (
            <div key={idx} className="col-6 col-md-3">
              <div
                className="p-3 rounded-3"
                style={{
                  backgroundColor: "var(--cafe-oscuro)",
                  border: "1px solid rgba(198,168,125,0.12)",
                }}
              >
                <div style={{ color: "var(--dorado)", fontSize: "2rem", fontWeight: 700, lineHeight: 1.1 }}>
                  {stat.value}
                </div>
                <div
                  style={{
                    color: "var(--beige)",
                    opacity: 0.6,
                    fontSize: "0.7rem",
                    textTransform: "uppercase",
                    letterSpacing: "0.2em",
                    marginTop: "0.4rem",
                  }}
                >
                  {stat.label}
                </div>
              </div>
            </div>
          ))}
        </div>

        <div
          className="my-5"
          style={{
            height: "1px",
            background: "linear-gradient(90deg, transparent, rgba(198,168,125,0.4), transparent)",
          }}
        />

        <section className="mb-5">
          <h2 className="text-center mb-2" style={{ color: "var(--dorado)", fontWeight: 500, letterSpacing: "0.5px" }}>
            Nuestros Valores
          </h2>
          <div
            className="mx-auto mb-5"
            style={{
              width: "80px",
              height: "1px",
              background: "linear-gradient(90deg, transparent, var(--dorado), transparent)",
            }}
          />

          <div className="row g-4">
            {[
              {
                icon: "fa-leaf",
                title: "Sostenibilidad",
                desc: "Comprometidos con el medio ambiente y prácticas de cultivo responsables. Utilizamos envases biodegradables y apoyamos la agricultura regenerativa.",
                featured: false,
              },
              {
                icon: "fa-award",
                title: "Calidad Premium",
                desc: "Seleccionamos cuidadosamente los mejores granos de origen único. Nuestro proceso de tostado artesanal resalta las notas más sutiles de cada variedad.",
                featured: true,
              },
              {
                icon: "fa-users",
                title: "Comunidad",
                desc: "Creemos en el poder del café para unir a las personas. Mokaf es tu espacio para conectar, trabajar o simplemente relajarte.",
                featured: false,
              },
            ].map((val, idx) => (
              <div key={idx} className="col-md-4">
                <div
                  className="p-4 h-100 rounded-3 text-center d-flex flex-column align-items-center"
                  style={{
                    backgroundColor: "var(--cafe-oscuro)",
                    border: val.featured
                      ? "1px solid rgba(198,168,125,0.45)"
                      : "1px solid rgba(198,168,125,0.12)",
                    boxShadow: val.featured ? "0 0 30px rgba(198,168,125,0.07)" : "none",
                    transition: "transform 0.3s ease",
                  }}
                  onMouseEnter={(e) => (e.currentTarget.style.transform = "translateY(-5px)")}
                  onMouseLeave={(e) => (e.currentTarget.style.transform = "translateY(0)")}
                >
                  <i
                    className={`fas ${val.icon} mb-3`}
                    style={{ color: "var(--dorado)", fontSize: "2rem" }}
                  />
                  <h5 style={{ color: "var(--dorado)", fontWeight: 400, letterSpacing: "0.3px" }}>
                    {val.title}
                  </h5>
                  <div
                    className="my-3"
                    style={{ width: "32px", height: "1px", backgroundColor: "rgba(198,168,125,0.5)" }}
                  />
                  <p style={{ color: "var(--beige)", opacity: 0.75, fontSize: "0.875rem", lineHeight: 1.65, margin: 0 }}>
                    {val.desc}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </section>

        <div
          className="my-5"
          style={{
            height: "1px",
            background: "linear-gradient(90deg, transparent, rgba(198,168,125,0.4), transparent)",
          }}
        />

        
        <section>
          <h2 className="text-center mb-2" style={{ color: "var(--dorado)", fontWeight: 500, letterSpacing: "0.5px" }}>
            Conoce al Equipo
          </h2>
          <div
            className="mx-auto mb-5"
            style={{
              width: "80px",
              height: "1px",
              background: "linear-gradient(90deg, transparent, var(--dorado), transparent)",
            }}
          />

          {team.length > 0 ? (
            <>
              <div className="row justify-content-center g-4">
                {team.slice(0, 4).map((member: any) => (
                  <div key={member.id} className="col-6 col-md-3">
                    <MemberCard member={member} />
                  </div>
                ))}
              </div>
              {team.length > 4 && (
                <div className="row justify-content-center g-4 mt-2">
                  {team.slice(4).map((member: any) => (
                    <div key={member.id} className="col-6 col-md-3">
                      <MemberCard member={member} />
                    </div>
                  ))}
                </div>
              )}
            </>
          ) : (
            <div className="text-center py-5" style={{ color: "var(--beige)", opacity: 0.5 }}>
              <i className="fas fa-users fa-3x mb-3"></i>
              <p>Preparando nuestro equipo...</p>
            </div>
          )}
        </section>

      </div>
    </main>
  );
}

function MemberCard({ member }: { member: any }) {
  return (
    <div
      className="d-flex flex-column align-items-center text-center p-3 rounded-3"
      style={{
        backgroundColor: "var(--cafe-oscuro)",
        border: "1px solid rgba(198,168,125,0.12)",
        transition: "transform 0.3s ease",
      }}
      onMouseEnter={(e) => (e.currentTarget.style.transform = "translateY(-5px)")}
      onMouseLeave={(e) => (e.currentTarget.style.transform = "translateY(0)")}
    >
      <div
        className="rounded-circle mb-3"
        style={{
          padding: "2px",
          background: "linear-gradient(135deg, rgba(198,168,125,0.8), rgba(198,168,125,0.1))",
          width: "100px",
          height: "100px",
        }}
      >
        <img
          src={member.profileImageUrl || `https://i.pravatar.cc/150?u=${member.id}`}
          alt={`${member.firstName} ${member.lastName}`}
          className="rounded-circle w-100 h-100"
          style={{ objectFit: "cover", backgroundColor: "var(--cafe-oscuro)" }}
        />
      </div>

      <h6 style={{ color: "var(--dorado)", fontWeight: 400, marginBottom: "0.3rem" }}>
        {member.firstName} {member.lastName}
      </h6>

      <div
        className="mb-2"
        style={{ width: "28px", height: "1px", backgroundColor: "rgba(198,168,125,0.5)" }}
      />

      <p
        style={{
          color: "var(--dorado)",
          opacity: 0.7,
          fontSize: "0.65rem",
          textTransform: "uppercase",
          letterSpacing: "0.25em",
          marginBottom: "0.4rem",
        }}
      >
        {member.position || "Staff"}
      </p>

      <p
        style={{
          color: "var(--beige)",
          opacity: 0.55,
          fontSize: "0.75rem",
          lineHeight: 1.5,
          margin: 0,
        }}
      >
        {member.description || "Devoto del arte del espresso."}
      </p>
    </div>
  );
}