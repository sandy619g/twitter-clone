FROM node:18
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Use NGINX to serve the static files
FROM nginx:alpine
COPY --from=0 /app/dist /usr/share/nginx/html
# or if you're using Vite
# COPY --from=0 /app/build /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
