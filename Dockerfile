FROM nginx

COPY target/gh-pages /usr/share/nginx/html
COPY nginx/default.conf /etc/nginx/conf.d/default.conf