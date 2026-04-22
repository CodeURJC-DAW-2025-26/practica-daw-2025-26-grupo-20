import type { Config } from "@react-router/dev/config";

export default {
  // Config options...
  basename: "/new/",
  // Server-side render disabled to allow serving as SPA from Spring Boot
  ssr: false,
} satisfies Config;
