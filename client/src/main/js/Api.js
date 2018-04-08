import {credentials, restUrl} from './scripts';
import axios from 'axios';

export class Api {
    static retrieveAccount(credentials, onSuccess, onFailure) {
        if (credentials.username && credentials.password) {
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
        if (credentials.username && credentials.password) {
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
        if (credentials.username && credentials.password) {
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

    static retrieveLanguages(onSuccess, onFailure) {
        axios
            .get('supportedLanguages', {
                baseURL: restUrl()
            })
            .then(response => onSuccess(response.data.payload))
            .catch(response => onFailure(response));
    }

    static retrieveCourseStatistics(credentials, username, courseName, onSuccess, onFailure) {
        if (credentials.username && credentials.password) {
            axios
                .get(`/${username}/${courseName}/statistics`, {
                    baseURL: restUrl(),
                    auth: credentials
                })
                .then(response => onSuccess(response.data.payload))
                .catch(response => onFailure(response));
        }
    }

    static startCourse(credentials, courseName, onSuccess, onFailure) {
        if (credentials.username && credentials.password) {
            axios
                .post('composeCourse', {}, {
                    baseURL: restUrl(),
                    auth: credentials,
                    params: {
                        courseName
                    }
                })
                .then(response => onSuccess(response.data.payload))
                .catch(response => onFailure(response));
        }
    }

    static deleteCourse(credentials, courseName, onSuccess, onFailure) {
        if (credentials.username && credentials.password) {
            axios
                .post('deleteCourse', {}, {
                    baseURL: restUrl(),
                    auth: credentials,
                    params: {
                        courseName
                    }
                })
                .then(response => onSuccess(response.data.payload))
                .catch(response => onFailure(response));
        }
    }

    static analysePlagiarism(credentials, courseName, onSuccess, onFailure) {
        if (credentials.username && credentials.password) {
            axios
                .post('analysePlagiarism', {}, {
                    baseURL: restUrl(),
                    auth: credentials,
                    params: {
                        courseName
                    }
                })
                .then(response => onSuccess(response.data.payload))
                .catch(response => onFailure(response));
        }
    }
}