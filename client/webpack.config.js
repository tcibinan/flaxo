const path = require('path');

module.exports = env => {
    return {
        module: {
            rules: [
                {
                    test: /\.js$/,
                    exclude: /node_modules/,
                    use: {
                        loader: 'babel-loader',
                        options: {
                            presets: ['react']
                        }
                    }
                },
                {
                    test: /\.css$/,
                    use: [
                        {
                            loader: 'style-loader'
                        },
                        {
                            loader: 'css-loader',
                            options: {url: false}
                        }
                    ]
                },
                {
                    test: /scripts\.js$/,
                    loader: 'string-replace-loader',
                    options: {
                        search: 'REST_URL',
                        replace: env.REST_URL,
                    }
                }
            ]
        }
    };
};