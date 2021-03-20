compile:
	sbt ui/fullLinkJS

prepare-gh-pages: compile
	mkdir -p target/gh-pages
	cp -r ui/static/* target/gh-pages/
	cp ui/target/scala-2.13/laminar-bulma-opt/main.js target/gh-pages/main.js
