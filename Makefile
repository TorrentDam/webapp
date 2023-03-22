prepare-gh-pages:
	npm install
	npx vite build

dev:
	npx vite

build-docker: prepare-gh-pages
	docker build -t ghcr.io/torrentdamdev/web .