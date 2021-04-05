compile:
	sbt fullLinkJS

prepare-gh-pages: compile
	mkdir -p target/gh-pages
	cp -r static/* target/gh-pages/
	cp -r target/gh-pages/index.html target/gh-pages/404.html
	cp target/scala-2.13/webapp-opt/*.js target/gh-pages/
	cp sw/target/scala-2.13/sw-opt/*.js target/gh-pages/

dev:
	npx snowpack dev