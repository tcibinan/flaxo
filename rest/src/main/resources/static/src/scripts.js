import Cookies from 'js-cookie';
import React from 'react';

export {logResponse, credentials, username, password, restUrl}

function logResponse() {
    return response => console.log(response);
}

function username() {
    return Cookies.get('nickname');
}

function password() {
    return Cookies.get('password');
}

function restUrl() {
    // placeholder will be replaced during webpacking
    return 'REST_URL';
}

function credentials() {
    return {
        username: username(),
        password: password()
    };
}