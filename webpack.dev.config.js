var HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
  mode: "production",
  entry: [
    'react-hot-loader',
    __dirname + '/src/index.js'
  ],
  resolve: {
    modules: [
      __dirname + '/public',
      __dirname + '/node_modules',
      __dirname + '/target/scala-2.13/webapp-fastopt'
    ],
    alias: {
      'react-dom': '@hot-loader/react-dom',
    }
  },
  module: {
    rules: [
      { test: /\.js$/, exclude: /node_modules/, use: ['react-hot-loader/webpack'] },
      { test: /\.svg$/, use: ['@svgr/webpack'] }
    ]
  },
  output: {
    path: __dirname + '/target/webpack/dev-dest',
    publicPath: '/',
    filename: 'bundle.js'
  },
  devServer: {
    hot: true,
    watchOptions: {
      poll: 200
    }
  },
  plugins: [
    new HtmlWebpackPlugin({
      templateParameters: {
        config: {
          serverUrl: 'bittorrent-server.herokuapp.com',
          useEncryption: true
//          serverUrl: 'localhost:9999',
//          useEncryption: false
        }
      }
    })
  ]
};
