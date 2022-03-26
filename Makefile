compile:
	sbt fastLinkJS

prepare-gh-pages: compile
	npm install
	npx snowpack build
	ln -s ./index.html target/gh-pages/404.html

dev:
	npx snowpack dev

build-docker: prepare-gh-pages
	docker build -t ghcr.io/torrentdamdev/web .