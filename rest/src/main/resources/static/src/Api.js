import {restUrl} from "./scripts";
import axios from 'axios';

export {Api}

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

    static retrieveCourses(credentials, nickname, onSuccess, onFailure) {
        if (credentials.username !== undefined && credentials.password !== undefined) {
            axios
                .get('allCourses', {
                    baseURL: restUrl(),
                    auth: credentials,
                    params: {
                        nickname: nickname
                    }
                })
                .then(response => onSuccess(response.data.payload))
                .catch(response => onFailure(response));
        }
    }

    static createCourse(credentials, courseData, onSuccess, onFailure) {
        if (credentials.username !== undefined && credentials.password !== undefined) {
            axios
                .post('createCourse', {}, {
                    baseURL: restUrl(),
                    auth: credentials,
                    params: courseData
                })
                .then(response => onSuccess(response.data.payload))
                .catch(response => onFailure(response));
        }
    }
}