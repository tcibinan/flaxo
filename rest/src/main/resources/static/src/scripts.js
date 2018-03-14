import Cookies from 'js-cookie';

export function restUrl() {
    // placeholder will be replaced during webpacking
    return 'REST_URL';
}

export function credentials() {
    return {
        username: Cookies.get('username'),
        password: Cookies.get('password')
    }
}