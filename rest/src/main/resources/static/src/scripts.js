import Cookies from "js-cookie";

export {restUrl, credentials}

function restUrl() {
    // placeholder will be replaced during webpacking
    return 'REST_URL';
}

function credentials() {
    return {
        username: Cookies.get('username'),
        password: Cookies.get('password')
    }
}