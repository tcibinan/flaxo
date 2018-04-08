const path = require('path');

module.exports = env => {
    return {
        module: {
            rules: [
                {
                    test: /\.js$/,
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
                        'style-loader',
                        'css-loader'
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