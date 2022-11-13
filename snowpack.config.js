module.exports = {
  buildOptions: {
    out: "target/gh-pages",
  },
  mount: {
    static: "/",
    "app/target/scala-3.2.1/app-fastopt": "/",
    "sw/target/scala-3.2.1/sw-fastopt": "/",
  },
  routes: [
    {"match": "routes", "src": ".*", "dest": "/index.html"},
  ],
  optimize: {
    // https://github.com/withastro/snowpack/issues/3109
    // When fixed config.js -> config.json
    bundle: false,
    minify: true,
    target: 'es2018',
  },
};
