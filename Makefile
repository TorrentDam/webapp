compile:
	sbt ui/fastLinkJS

prepare-gh-pages: compile
	mkdir -p target/gh-pages
	cp ui/static/index.html target/gh-pages/index.html
	cp ui/target/scala-2.13/laminar-bulma-fastopt/main.js target/gh-pages/main.js
