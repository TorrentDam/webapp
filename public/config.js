(function () {
  var script = document.currentScript;
  window.__config = {
    server: "server.torrents.lavrov.nl",
    secure: true,
    basePath: script ? new URL(".", script.src).pathname : "/"
//  server: "localhost:9999",
//  secure: false
  };
})();
