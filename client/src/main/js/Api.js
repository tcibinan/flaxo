import {restUrl} from './scripts';
import axios from 'axios';
import {List, Seq} from 'immutable';
import {CourseModel} from './model/CourseModel';
import {TaskModel} from './model/TaskModel';

export class Api {
    static retrieveAccount(credentials, onSuccess, onFailure) {
        if (credentials.username && credentials.password) {
            axios
                .get('user', {
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
                .get('course/all', {
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
                .post('course/create', {}, {
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
            .get('settings/languages', {
                baseURL: restUrl()
            })
            .then(response => onSuccess(response.data.payload))
            .catch(response => onFailure(response));
    }

    static retrieveCourseStatistics(credentials, username, courseName, onSuccess, onFailure) {
        if (credentials.username && credentials.password) {
            axios
                .get('statistics', {
                    baseURL: restUrl(),
                    auth: credentials,
                    params: {
                        owner: username,
                        course: courseName
                    }
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
                .post('course/activate', {}, {
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
                .delete('course/delete', {
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
                .post('course/analyse/plagiarism', {}, {
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

    static syncCourse(credentials, courseName, onSuccess, onFailure) {
        if (credentials.username && credentials.password) {
            axios
                .post('course/sync', {}, {
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
                .post('task/update/rules', {}, {
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
                .post('course/activate/travis', {}, {
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
                .post('course/activate/codacy', {}, {
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

    static downloadStatistics(credentials, courseName, format, onSuccess, onFailure) {
        if (credentials.username && credentials.password) {
            axios
                .get('statistics/download', {
                    baseURL: restUrl(),
                    auth: credentials,
                    responseType: 'blob',
                    params: {
                        courseName,
                        format
                    }
                })
                .then(response => onSuccess(response.data))
                .catch(response => onFailure(response));
        }
    }
}