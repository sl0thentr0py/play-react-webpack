'use strict';

var webpack = require('webpack');
var HardSourceWebpackPlugin = require('hard-source-webpack-plugin');


module.exports = {
    entry: './app/assets/js/index.jsx',
    output: {
        filename: './target/web/webpack/main/js/bundle.js'
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
    ]
};