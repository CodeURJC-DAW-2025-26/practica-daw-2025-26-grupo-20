import { reactRouter } from "@react-router/dev/vite";
import tailwindcss from "@tailwindcss/vite";
import { defineConfig, loadEnv } from "vite";

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  const target = env.VITE_API_BASE_URL;

  return {
    base: "/new/",
    plugins: [tailwindcss(), reactRouter()],
    resolve: {
      tsconfigPaths: true,
    },
    server:{
      port: 5174,
      proxy:{
        "/api": {
          target,
          changeOrigin: true,
          secure: false
        },
        "/images": {
          target,
          changeOrigin: true,
          secure: false
        }
      }
    }
  };
});
