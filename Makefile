prepare-gh-pages:
	./mill app.fullLinkJS
	npm install
	npx vite build

dev:
	npm install
	./mill --watch app.fastLinkJS &
	npx vite

build-docker: prepare-gh-pages
	docker build -t ghcr.io/torrentdamdev/web .
