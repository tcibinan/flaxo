const path = require('path');

module.exports = env => {
    return {
        entry: './src/index.js',
        output: {
            path: path.resolve(__dirname, 'dist'),
            filename: 'flaxo.bundle.js'
        },
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