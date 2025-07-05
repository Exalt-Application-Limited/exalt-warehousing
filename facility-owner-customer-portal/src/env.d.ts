/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string
  readonly VITE_WS_URL: string
  readonly VITE_AUTH_SERVICE_URL: string
  readonly VITE_STORAGE_SERVICE_URL: string
  readonly VITE_CDN_URL: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}