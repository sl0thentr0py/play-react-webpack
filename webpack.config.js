'use strict';

var webpack = require('webpack');
var path = require('path');
var HardSourceWebpackPlugin = require('hard-source-webpack-plugin');


var config = {
    entry: './app/assets/js/index.jsx',
    output: {
        filename: 'bundle.js',
        path: path.resolve('target', 'web', 'webpack', 'js'),
        publicPath: '/assets/js/',
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
            }
        ]
    },
    resolve: {
        extensions: ['.js', '.jsx']
    },
    plugins: [
        new HardSourceWebpackPlugin(),
        new webpack.ContextReplacementPlugin(/moment[\/\\]locale$/, /en/)
    ],
    devServer: {
        //hot: true,
        inline: true,
        stats: { colors: true },
        proxy: {
          '*': 'http://localhost:9000'
        }
    }
};


if (process.env.NODE_ENV === 'production') {
  config.plugins.push(new webpack.DefinePlugin({
    'process.env': {
      'NODE_ENV': JSON.stringify('production')
    }
  }))
  config.plugins.push(new webpack.optimize.UglifyJsPlugin())
} else {
  config.devtool = 'source-map'
}

module.exports = config