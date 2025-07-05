import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['./tests/setup.ts'],
  },
  server: {
    port: 3202,
    host: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8206', // warehouse-management-service
        changeOrigin: true,
        secure: false,
      },
      '/marketplace': {
        target: 'http://localhost:8230', // customer-storage-marketplace-service
        changeOrigin: true,
        secure: false,
      },
      '/auth': {
        target: 'http://localhost:8080', // auth-service
        changeOrigin: true,
        secure: false,
      }
    }
  },
  build: {
    outDir: 'dist',
    sourcemap: true,
    chunkSizeWarningLimit: 1600,
  },
  define: {
    'process.env': {}
  }
})