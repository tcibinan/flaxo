import axios from "axios/index";
import Cookies from "js-cookie";

export {Api, restUrl, credentials}

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

class Api {
    static retrieveAccount(credentials, onSuccess, onFailure) {
        if (credentials.username !== undefined && credentials.password !== undefined) {
            axios
                .get('account', {
                    baseURL: restUrl(),
                    auth: credentials
                })
                .then(response => onSuccess(response.data.payload))
                .catch(response => onFailure(response));
        }
    }
}