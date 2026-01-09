# Multi-stage build for Angular frontend
FROM node:18.18.0-alpine AS builder

WORKDIR /build

# Copy package files
COPY ahp-frontend/package.json ahp-frontend/package-lock.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY ahp-frontend .

# Build Angular application
RUN npm run build

# Production stage with Nginx
FROM nginx:alpine

# Copy nginx configuration
COPY ahp-frontend/nginx.conf /etc/nginx/nginx.conf

# Copy built Angular app
COPY --from=builder /build/dist/analytical-hierarchy-process-frontend /usr/share/nginx/html

# Copy entrypoint script
COPY ahp-frontend/docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

EXPOSE 80

ENTRYPOINT ["/docker-entrypoint.sh"]
