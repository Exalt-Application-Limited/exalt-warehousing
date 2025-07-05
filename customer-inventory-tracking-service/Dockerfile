FROM node:18-alpine
WORKDIR /app
RUN addgroup -g 1001 -S nodejs && adduser -S nodejs -u 1001
COPY package*.json ./
RUN npm ci --only=production && npm cache clean --force
COPY --chown=nodejs:nodejs . .
USER nodejs
EXPOSE 8089
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD node healthcheck.js
CMD ["node", "src/index.js"]
