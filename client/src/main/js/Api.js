import {restUrl} from './scripts';
import axios from 'axios';
import {List, Seq} from 'immutable';
import {CourseModel} from './model/CourseModel';
import {TaskModel} from './model/TaskModel';

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
                .then(response => {
                    const courses =
                        Seq(response.data.payload)
                            .map(courseJson => new CourseModel(courseJson));
                    onSuccess(courses);
                })
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
                .then(response => {
                    const course = new CourseModel(response.data.payload);
                    onSuccess(course);
                })
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
                .then(response => {
                    const tasks =
                        Seq(response.data.payload)
                            .map(taskJson => new TaskModel(taskJson));
                    onSuccess(tasks);
                })
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

    static updateRules(credentials, courseName, taskBranch, deadline, onSuccess, onFailure) {
        if (credentials.username && credentials.password) {
            axios
                .put('updateRules', {}, {
                    baseURL: restUrl(),
                    auth: credentials,
                    params: {
                        courseName,
                        taskBranch,
                        deadline: deadline ? deadline : null
                    }
                })
                .then(response => onSuccess(response.data.payload))
                .catch(response => onFailure(response));
        }
    }

    static addCodacyToken(credentials, codacyToken, onSuccess, onFailure) {
        if (credentials.username && credentials.password) {
            axios
                .put('codacy/token', {}, {
                    baseURL: restUrl(),
                    auth: credentials,
                    params: {
                        token: codacyToken
                    }
                })
                .then(response => onSuccess(response.data.payload))
                .catch(response => onFailure(response));
        }
    }

    static activateTravis(credentials, courseName, onSuccess, onFailure) {
        if (credentials.username && credentials.password) {
            axios
                .post('activateTravis', {}, {
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

    static activateCodacy(credentials, courseName, onSuccess, onFailure) {
        if (credentials.username && credentials.password) {
            axios
                .post('activateCodacy', {}, {
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