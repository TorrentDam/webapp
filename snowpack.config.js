module.exports = {
  buildOptions: {
    out: "target/gh-pages",
  },
  mount: {
    static: "/",
    "app/target/scala-3.1.0/app-fastopt": "/",
    "sw/target/scala-3.1.0/sw-fastopt": "/",
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
