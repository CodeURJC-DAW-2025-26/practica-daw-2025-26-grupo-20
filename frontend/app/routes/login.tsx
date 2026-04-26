import { Form, useActionData, useNavigate, Link } from "react-router";
import { useEffect } from "react";
import { useAuthStore } from "../store/authStore";
import { API_BASE_URL } from "../config";


export async function clientAction({ request }: { request: Request }) {
  try {
    const formData = await request.formData();
    const intent = formData.get("intent") as string;
    const email = formData.get("email") as string;
    const password = formData.get("password") as string;

    if (intent === "login") {
      const loginResponse = await fetch(`${API_BASE_URL}/api/v1/auth/sessions`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ email, password }),
      });

      if (!loginResponse.ok) {
        return { error: "Credenciales incorrectas. Inténtalo de nuevo.", form: "login" };
      }

      const meResponse = await fetch(`${API_BASE_URL}/api/v1/users/me`, {
        credentials: "include",
      });

      if (!meResponse.ok) {
        return { error: "Login correcto pero no se pudo obtener el perfil.", form: "login" };
      }

      const user = await meResponse.json();
      return { success: true, user, action: "login" };

    } else if (intent === "register") {
      const name = formData.get("name") as string;
      
      // Client validation
      const emailRegex = /^[A-Za-z0-9+_.-]+@(.+)$/;
      const passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d).+$/;

      if (!name?.trim()) return { error: "El nombre es obligatorio.", form: "register" };
      if (!emailRegex.test(email)) return { error: "El formato del email no es válido.", form: "register" };
      if (password.length < 6) return { error: "La contraseña debe tener al menos 6 caracteres.", form: "register" };
      if (!passwordRegex.test(password)) return { error: "La contraseña debe contener letras y números.", form: "register" };

      const registerResponse = await fetch(`${API_BASE_URL}/api/v1/auth/registrations`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ name, email, password }),
      });

      if (!registerResponse.ok) {
        const errorData = await registerResponse.json().catch(() => ({}));
        return { error: errorData.message || "No se pudo crear la cuenta.", form: "register" };
      }

      // Auto-login after register
      const loginResponse = await fetch(`${API_BASE_URL}/api/v1/auth/sessions`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ email, password }),
      });

      if (loginResponse.ok) {
        const meResponse = await fetch(`${API_BASE_URL}/api/v1/users/me`, { credentials: "include" });
        if (meResponse.ok) {
          const user = await meResponse.json();
          return { success: true, user, action: "register" };
        }
      }
      return { success: true, action: "register", message: "Cuenta creada. Por favor, inicia sesión." };
    }
  } catch (err) {
    console.error("Auth error:", err);
    return { error: "Error de conexión con el servidor." };
  }
}

export default function Auth() {
  const actionData = useActionData<typeof clientAction>();
  const setUser = useAuthStore((state) => state.setUser);
  const navigate = useNavigate();

  useEffect(() => {
    if (actionData?.success && actionData.user) {
      setUser(actionData.user);
      navigate(actionData.user.role === "ADMIN" ? "/profile-admin" : "/menu", { replace: true });
    }
  }, [actionData, setUser, navigate]);

  return (
    <div className="container py-5">
      <div 
        className="card mx-auto shadow-lg rounded-3"
        style={{ 
          backgroundColor: "#1c1c1c", 
          border: "1px solid #c6a87d",
          maxWidth: "1000px"
        }}
      >
        {/* Main Title */}
        <div className="text-center py-4 border-bottom border-secondary border-opacity-25">
          <h1 className="h3 mb-0" style={{ color: "#c6a87d", fontWeight: "400", letterSpacing: "1px" }}>
            Login o Registro
          </h1>
        </div>

        <div className="card-body p-4 p-md-5">
          <div className="row g-5 position-relative">
            {/* Login Column */}
            <div className="col-md-6 pe-md-5">
              <h2 className="h4 mb-4" style={{ color: "#c6a87d", borderBottom: "1px solid #c6a87d", display: "inline-block", paddingBottom: "5px" }}>
                Login
              </h2>
              
              {actionData?.error && actionData.form === "login" && (
                <div className="alert alert-danger py-2 px-3 mb-4 border-0 rounded-0" style={{ backgroundColor: "rgba(220, 53, 69, 0.1)", color: "#ff9a9a", fontSize: "0.85rem" }}>
                  {actionData.error}
                </div>
              )}

              <Form method="post" className="mt-4">
                <input type="hidden" name="intent" value="login" />
                <div className="mb-4">
                  <label className="form-label d-block mb-2 text-white opacity-75" style={{ fontSize: "0.8rem" }}>Email</label>
                  <input
                    name="email" type="email" required placeholder="Tu email"
                    className="form-control border-secondary border-opacity-50 text-white"
                    style={{ backgroundColor: "#2d2d2d", fontSize: "0.9rem", padding: "0.6rem 1rem" }}
                  />
                </div>

                <div className="mb-4">
                  <label className="form-label d-block mb-2 text-white opacity-75" style={{ fontSize: "0.8rem" }}>Contraseña</label>
                  <input
                    name="password" type="password" required placeholder="Tu contraseña"
                    className="form-control border-secondary border-opacity-50 text-white"
                    style={{ backgroundColor: "#2d2d2d", fontSize: "0.9rem", padding: "0.6rem 1rem" }}
                  />
                </div>

                <button 
                  type="submit" 
                  className="btn btn-primary w-100 py-2 mt-2 rounded-2"
                  style={{ backgroundColor: "#0d6efd", border: "none", fontWeight: "500" }}
                >
                  Login
                </button>
              </Form>
            </div>

            {/* Vertical Separator (Desktop only) */}
            <div className="d-none d-md-block position-absolute start-50 top-0 bottom-0 p-0" style={{ width: "1px", backgroundColor: "rgba(255,255,255,0.1)", transform: "translateX(-50%)" }}></div>

            {/* Register Column */}
            <div className="col-md-6 ps-md-5">
              <h2 className="h4 mb-4" style={{ color: "#c6a87d", borderBottom: "1px solid #c6a87d", display: "inline-block", paddingBottom: "5px" }}>
                Registro
              </h2>

              {actionData?.error && actionData.form === "register" && (
                <div className="alert alert-danger py-2 px-3 mb-4 border-0 rounded-0" style={{ backgroundColor: "rgba(220, 53, 69, 0.1)", color: "#ff9a9a", fontSize: "0.85rem" }}>
                  {actionData.error}
                </div>
              )}

              {actionData?.success && actionData.action === "register" && actionData.message && (
                <div className="alert alert-success py-2 px-3 mb-4 border-0 rounded-0" style={{ backgroundColor: "rgba(25, 135, 84, 0.1)", color: "#a3d977", fontSize: "0.85rem" }}>
                  {actionData.message}
                </div>
              )}

              <Form method="post" className="mt-4">
                <input type="hidden" name="intent" value="register" />
                <div className="mb-4">
                  <label className="form-label d-block mb-2 text-white opacity-75" style={{ fontSize: "0.8rem" }}>Nombre completo</label>
                  <input
                    name="name" type="text" required placeholder="Tu nombre completo"
                    className="form-control border-secondary border-opacity-50 text-white"
                    style={{ backgroundColor: "#2d2d2d", fontSize: "0.9rem", padding: "0.6rem 1rem" }}
                  />
                </div>

                <div className="mb-4">
                  <label className="form-label d-block mb-2 text-white opacity-75" style={{ fontSize: "0.8rem" }}>Email</label>
                  <input
                    name="email" type="email" required placeholder="Tu email"
                    className="form-control border-secondary border-opacity-50 text-white"
                    style={{ backgroundColor: "#2d2d2d", fontSize: "0.9rem", padding: "0.6rem 1rem" }}
                  />
                </div>

                <div className="mb-4">
                  <label className="form-label d-block mb-2 text-white opacity-75" style={{ fontSize: "0.8rem" }}>Contraseña</label>
                  <input
                    name="password" type="password" required minLength={6} placeholder="Mín. 4 caracteres"
                    className="form-control border-secondary border-opacity-50 text-white"
                    style={{ backgroundColor: "#2d2d2d", fontSize: "0.9rem", padding: "0.6rem 1rem" }}
                  />
                </div>

                <button 
                  type="submit" 
                  className="btn btn-success w-100 py-2 mt-2 rounded-2"
                  style={{ backgroundColor: "#198754", border: "none", fontWeight: "500" }}
                >
                  Registrarse
                </button>
              </Form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
