compile:
	sbt fullLinkJS

prepare-gh-pages: compile
	mkdir -p target/gh-pages
	cp -r static/* target/gh-pages/
	cp -r target/gh-pages/index.html target/gh-pages/404.html
	cp target/scala-2.13/webapp-opt/main.js target/gh-pages/main.js
