const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyPlugin = require("copy-webpack-plugin");
const { CleanWebpackPlugin } = require('clean-webpack-plugin');

module.exports = {
  optimization: {
    usedExports: true
  },
  entry: {
    landingPage: path.resolve(__dirname, 'src', 'pages', 'landingPage.js'),
    loginPage: path.resolve(__dirname, 'src', 'pages', 'loginPage.js'),
    myPlaylists: path.resolve(__dirname, 'src', 'pages', 'myPlaylists.js'),
    createPlaylist: path.resolve(__dirname, 'src', 'pages', 'createPlaylist.js'),
     myPlaylistView: path.resolve(__dirname, 'src', 'pages', 'myPlaylistView.js')
  },
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: '[name].js',
  },
   devServer: {
      https: false,
      port: 8080,
      open: true,
      proxy: [
        {
          context: [
            '/user',
            '/playlists',
            '/playlists/user',
            '/playlists/add-song'
            // Add other endpoints as they are developed
          ],
          target: 'http://localhost:5001'
        }
      ]
    },
  plugins: [
  new HtmlWebpackPlugin({
              template: './src/html/login.html',
              filename: 'index.html', // Changed to 'index.html'
              inject: false
            }),
    new HtmlWebpackPlugin({
      template: './src/html/landingPage.html',
      filename: 'landingPage.html',
      inject: false
    }),
    new HtmlWebpackPlugin({
      template: './src/html/login.html',
      filename: 'login.html',
      inject: false
    }),
    new HtmlWebpackPlugin({
      template: './src/html/myPlaylistView.html',
      filename: 'myPlaylistView.html',
      inject: false
    }),
     new HtmlWebpackPlugin({
          template: './src/html/createPlaylist.html',
          filename: 'createPlaylist.html',
          inject: false
        }),
        new HtmlWebpackPlugin({
          template: './src/html/myPlaylists.html',
          filename: 'myPlaylists.html',
          inject: false
        }),

    new CopyPlugin({
      patterns: [
        {
          from: path.resolve('src/css'),
          to: path.resolve("dist/css")
        },
        {
          from: path.resolve('src/api'),
          to: path.resolve("dist/api")
        }
      ]
    }),

//    new CleanWebpackPlugin()
  ]
}
