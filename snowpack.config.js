module.exports = {
  buildOptions: {
    out: "target/gh-pages",
  },
  mount: {
    static: "/",
    "target/scala-3.0.0-RC2/webapp-fastopt": "/",
    "sw/target/scala-3.0.0-RC2/sw-fastopt": "/",
  },
  "routes": [
    {"match": "routes", "src": ".*", "dest": "/index.html"},
  ],
  optimize: {
    bundle: true,
    minify: true,
    target: 'es2018',
  },
};