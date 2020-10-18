var HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
  mode: "production",
  entry: [
    __dirname + '/src/index.js'
  ],
  resolve: {
    extensions: ['.js'],
    modules: [
      __dirname + '/public',
      __dirname + '/node_modules',
      __dirname + '/target/scala-2.13/webapp-opt'
    ]
  },
  module: {
    rules: [
      { test: /\.svg$/, use: ['@svgr/webpack'] }
    ]
  },
  output: {
    path: __dirname + '/target/webpack/dist',
    filename: 'bundle.js'
  },
  plugins: [
    new HtmlWebpackPlugin({
      templateParameters: {
        config: {
          serverUrl: 'bittorrent-server.herokuapp.com',
          useEncryption: true
        }
      }
    })
  ]
};
