'use strict';

var webpack = require('webpack');
var path = require('path');
var HardSourceWebpackPlugin = require('hard-source-webpack-plugin');
var	ExtractTextPlugin = require("extract-text-webpack-plugin");

var config = {
    entry: './app/assets/js/index.jsx',
    output: {
        filename: 'js/bundle.js',
        path: path.resolve('target', 'web', 'webpack'),
        publicPath: '/assets/',
    },
    module: {
        rules: [
            {
                test: /\.jsx?$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['es2015', 'react'] // ?? env ??
                    }
                }
            },
            {
                test: /\.(less|css)$/,
                use: ExtractTextPlugin.extract({
                    fallback: 'style-loader',
                    use: ['css-loader', 'less-loader']
                })
            }
        ]
    },
    resolve: {
        extensions: ['.js', '.jsx', '.less', '.css']
    },
    plugins: [
        new ExtractTextPlugin('stylesheets/main.css'),
        new HardSourceWebpackPlugin(),
        new webpack.ContextReplacementPlugin(/moment[\/\\]locale$/, /en/)
    ],
    devServer: {
        //hot: true,
        port: 8000,
        inline: true,
        stats: { colors: true },
        proxy: {
          '*': 'http://localhost:9000'
        },
        publicPath: '/versioned/'
    }
};


if (process.env.NODE_ENV === 'production') {
  config.plugins.push(new webpack.DefinePlugin({
    'process.env': {
      'NODE_ENV': JSON.stringify('production')
    }
  }));
  config.plugins.push(new webpack.optimize.UglifyJsPlugin());
} else {
  config.devtool = 'source-map';
}

module.exports = config