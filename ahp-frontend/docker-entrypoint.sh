#!/bin/sh
# Entrypoint script to replace environment variables in JavaScript bundle

# Replace API_URL in the main JS file
if [ -n "$API_URL" ]; then
    find /usr/share/nginx/html -type f -name "*.js" -exec sed -i "s|http://localhost:9000|${API_URL}|g" {} \;
fi

# Start nginx
exec nginx -g "daemon off;"
