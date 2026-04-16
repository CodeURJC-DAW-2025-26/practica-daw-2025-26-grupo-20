import {
  isRouteErrorResponse,
  Links,
  Meta,
  Outlet,
  Scripts,
  ScrollRestoration,
} from "react-router";
import { useEffect } from "react";
import type { Route } from "./+types/root";
import { useAuthStore } from "./store/authStore";
import stylesheet from "./app.css?url";

export const links: Route.LinksFunction = () => [
  { rel: "preconnect", href: "https://fonts.googleapis.com" },
  {
    rel: "preconnect",
    href: "https://fonts.gstatic.com",
    crossOrigin: "anonymous",
  },
  {
    rel: "stylesheet",
    href: "https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400;0,700;1,400&display=swap",
  },
  {
    rel: "stylesheet",
    href: "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css",
  },
  { rel: "stylesheet", href: stylesheet },
];

export function Layout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="es">
      <head>
        <meta charSet="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <Meta />
        <Links />
      </head>
      <body>
        {children}
        <ScrollRestoration />
        <Scripts />
      </body>
    </html>
  );
}

export default function App() {
  const initializeAuth = useAuthStore((state) => state.initializeAuth);

  // Al arrancar la app sincronizamos el estado local con la cookie de sesión del servidor.
  // Cubre: recarga de página, nueva pestaña, vuelta desde otra app.
  useEffect(() => {
    initializeAuth();
  }, [initializeAuth]);

  return <Outlet />;
}

export function ErrorBoundary({ error }: Route.ErrorBoundaryProps) {
  let message = "¡Algo salió mal!";
  let details = "Se produjo un error inesperado.";
  let stack: string | undefined;

  if (isRouteErrorResponse(error)) {
    message = error.status === 404 ? "404 — Página no encontrada" : "Error";
    details =
      error.status === 404
        ? "La página que buscas no existe."
        : error.statusText || details;
  } else if (import.meta.env.DEV && error instanceof Error) {
    details = error.message;
    stack = error.stack;
  }

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-[#050404] px-4 text-center gap-6">
      <div className="w-20 h-20 bg-[#0c0b0b] border border-[#d4b88d]/20 rounded-[2rem] flex items-center justify-center text-[#d4b88d] text-3xl">
        <i className="fas fa-mug-hot opacity-50"></i>
      </div>
      <h1 className="text-4xl font-serif italic text-[#d4b88d]">{message}</h1>
      <p className="text-stone-500 text-sm max-w-md">{details}</p>
      {stack && (
        <pre className="text-left text-xs text-stone-700 bg-white/5 rounded-2xl p-6 max-w-2xl overflow-auto">
          {stack}
        </pre>
      )}
      <a
        href="/"
        className="mt-4 text-[10px] font-bold uppercase tracking-[0.4em] text-[#d4b88d] border-b border-[#d4b88d]/30 pb-1 hover:text-white hover:border-white transition-all"
      >
        Volver al inicio
      </a>
    </div>
  );
}
