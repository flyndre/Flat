FROM node:20-slim AS base
RUN corepack enable
WORKDIR /

COPY . .
FROM base AS build
RUN --mount=type=cache,id=pnpm,target=/pnpm/store pnpm install --frozen-lockfile
RUN pnpm run build

FROM nginx:1.16.1

COPY --from=build /dist /var/www/
COPY ./nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
