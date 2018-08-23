module.exports["module"]["rules"].push({
    test: /\.css$/,
    use: ['style-loader', 'css-loader']
});

module.exports["module"]["rules"].push({
    test: /\.(png|woff|woff2|eot|ttf|svg)$/,
    loader: 'url-loader?limit=100000'
});