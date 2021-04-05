module.exports = {
  buildOptions: {
    out: "target/gh-pages",
  },
  mount: {
    static: "/",
    "target/scala-2.13/webapp-fastopt": "/",
    "sw/target/scala-2.13/sw-fastopt": "/",
  },
  "routes": [
    {"match": "routes", "src": ".*", "dest": "/index.html"}
  ]
};