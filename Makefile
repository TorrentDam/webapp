compile:
	sbt fastLinkJS

prepare-gh-pages: compile
	npm install
	npx snowpack build

dev:
	npx snowpack dev