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

export function gradeToNum(grade) {
    switch (grade) {
        case 'A':
            return 6;
        case 'B':
            return 5;
        case 'C':
            return 4;
        case 'D':
            return 3;
        case 'E':
            return 2;
        case 'F':
            return 1;
        default:
            return 0;
    }
}