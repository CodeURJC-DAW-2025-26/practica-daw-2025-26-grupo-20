import type { Config } from "@react-router/dev/config";

export default {
  basename: "/new/",
  // SPA mode: el action/loader se ejecuta en el cliente, no en el servidor
  ssr: false,
} satisfies Config;
