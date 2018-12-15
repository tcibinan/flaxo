module.exports["module"]["rules"].push({
    test: /\.css$/,
    use: ['style-loader', 'css-loader']
});

module.exports["module"]["rules"].push({
    test: /\.(png|woff|woff2|eot|ttf|svg)$/,
    loader: 'url-loader?limit=100000'
});

module.exports["module"]["rules"].push({
    test: /\.js$/,
    loader: 'string-replace-loader',
    options: {
        multiple: [
            { search: '{{REST_URL}}', replace: process.env.REST_URL },
            { search: '{{FLAXO_VERSION}}', replace: process.env.FLAXO_VERSION }
        ]
    }
});
