'use strict';

const SetupEndpoint = require('./setup/');


module.exports = SetupEndpoint({
    name: 'flaxo',
    urls: [
        {
            params: '/account',
            requests: [{
                method: 'GET',
                response: {
                    payload: {
                        githubId: 'admin',
                        nickname: 'admin',
                        githubAuthorized: true,
                        travisAuthorized: true,
                        codacyAuthorized: false
                    }
                }
            }]
        },
        {
            params: '/supportedLanguages',
            requests: [{
                method: 'GET',
                response: {
                    payload: [
                        {
                            name: 'java',
                            compatibleTestingLanguages: ['java', 'kotlin'],
                            compatibleTestingFrameworks: ['junit']
                        },
                        {
                            name: 'kotlin',
                            compatibleTestingLanguages: ['kotlin'],
                            compatibleTestingFrameworks: ['junit', 'spek']
                        }
                    ]
                }
            }]
        },
        {
            params: '/allCourses',
            requests: [{
                method: 'GET',
                response: {
                    payload: [
                        {
                            name: 'JavaCourse',
                            description:
                            'Ambiguous course description. Ambiguous course description. Ambiguous course description. ' +
                            'Ambiguous course description. Ambiguous course description. Ambiguous course description.',
                            createdDate: '2018-03-08T15:00:00.000',
                            url: 'https://github.com/flaxo-developer/JavaCourse',
                            language: 'java',
                            testingLanguage: 'java',
                            testingFramework: 'junit',
                            state: {
                                lifecycle: 'RUNNING',
                                activatedServices: [
                                    'TRAVIS',
                                    'CODACY'
                                ]
                            },
                            user: {
                                githubId: 'flaxo-developer',
                                nickname: 'admin',
                                githubAuthorized: true,
                                travisAuthorized: true,
                                codacyAuthorized: false
                            },
                            students: [
                                'student1',
                                'student2',
                                'student3',
                                'student4',
                                'student5'
                            ],
                            tasks: [
                                'task1',
                                'task2'
                            ]
                        },
                        {
                            name: 'KotlinCourse',
                            description: null,
                            createdDate: '2018-03-01T15:00:00.000',
                            url: 'https://github.com/flaxo-developer/KotlinCourse',
                            language: 'java',
                            testingLanguage: 'kotlin',
                            testingFramework: 'spek',
                            state: {
                                lifecycle: 'INIT',
                                activatedServices: []
                            },
                            user: {
                                githubId: 'flaxo-developer',
                                nickname: 'admin',
                                githubAuthorized: true,
                                travisAuthorized: true,
                                codacyAuthorized: false
                            },
                            students: [],
                            tasks: [
                                'task1',
                                'task2',
                                'task3',
                                'task4'
                            ]
                        }
                    ]
                }
            }]
        },
        {
            params: '/{nickname}/{courseName}/statistics',
            requests: [{
                method: 'GET',
                response: {
                    payload: [
                        {
                            branch: 'task1',
                            url: 'https://github.com/flaxo-developer/JavaCourse/tree/task1',
                            deadline: '2018-05-10T15:00:00.000',
                            plagiarismReports: [
                                {
                                    url: 'http://analysis.example.url1',
                                    date: '2018-04-10T10:00:00.000',
                                    matches: []
                                },
                                {
                                    url: 'http://analysis.example.url2',
                                    date: '2018-04-10T15:00:00.000',
                                    matches: [
                                        {
                                            url: 'http://analysis.example.url/match1',
                                            student1: 'student1',
                                            student2: 'student5',
                                            lines: 800,
                                            percentage: 85
                                        },
                                        {
                                            url: 'http://analysis.example.url/match1',
                                            student1: 'student2',
                                            student2: 'student5',
                                            lines: 200,
                                            percentage: 22
                                        }
                                    ]
                                }
                            ],
                            solutions: [
                                {
                                    task: 'task1',
                                    student: 'student1',
                                    score: 80,
                                    date: '2018-04-10T15:00:00.000',
                                    buildReports: [
                                        {
                                            succeed: true,
                                            date: '2018-04-10T10:00:00.000'
                                        },
                                        {
                                            succeed: true,
                                            date: '2018-04-10T15:00:00.000'
                                        }
                                    ],
                                    codeStyleReports: [
                                        {
                                            grade: 'E',
                                            date: '2018-04-10T10:00:00.000'
                                        },
                                        {
                                            grade: 'B',
                                            date: '2018-04-10T15:00:00.000'
                                        }
                                    ]
                                },
                                {
                                    task: 'task1',
                                    student: 'student2',
                                    score: null,
                                    date: '2018-04-10T15:00:00.000',
                                    buildReports: [
                                        {
                                            succeed: true,
                                            date: '2018-04-10T15:00:00.000'
                                        }
                                    ],
                                    codeStyleReports: [
                                        {
                                            grade: 'A',
                                            date: '2018-04-10T15:00:00.000'
                                        }
                                    ]
                                },
                                {
                                    task: 'task1',
                                    student: 'student3',
                                    score: 75,
                                    date: '2018-04-10T15:00:00.000',
                                    buildReports: [
                                        {
                                            succeed: false,
                                            date: '2018-04-10T15:00:00.000'
                                        }
                                    ],
                                    codeStyleReports: [
                                        {
                                            grade: 'B',
                                            date: '2018-04-10T15:00:00.000'
                                        }
                                    ]
                                },
                                {
                                    task: 'task1',
                                    student: 'student4',
                                    score: null,
                                    date: null,
                                    buildReports: [],
                                    codeStyleReports: []
                                },
                                {
                                    task: 'task1',
                                    student: 'student5',
                                    score: 70,
                                    date: '2018-05-30T15:00:00.000',
                                    buildReports: [
                                        {
                                            succeed: true,
                                            date: '2018-04-10T15:00:00.000'
                                        }
                                    ],
                                    codeStyleReports: [
                                        {
                                            grade: 'C',
                                            date: '2018-04-10T15:00:00.000'
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            branch: 'task2',
                            url: 'https://github.com/flaxo-developer/JavaCourse/tree/task2',
                            deadline: null,
                            plagiarismReport: [],
                            solutions: [
                                {
                                    task: 'task2',
                                    student: 'student1',
                                    score: null,
                                    date: null,
                                    buildReports: [],
                                    codeStyleReports: []
                                },
                                {
                                    task: 'task2',
                                    student: 'student2',
                                    score: null,
                                    date: null,
                                    buildReports: [],
                                    codeStyleReports: []
                                },
                                {
                                    task: 'task2',
                                    student: 'student3',
                                    score: null,
                                    date: null,
                                    buildReports: [],
                                    codeStyleReports: []
                                },
                                {
                                    task: 'task2',
                                    student: 'student4',
                                    score: null,
                                    date: null,
                                    buildReports: [],
                                    codeStyleReports: []
                                },
                                {
                                    task: 'task2',
                                    student: 'student5',
                                    score: null,
                                    date: null,
                                    buildReports: [],
                                    codeStyleReports: []
                                }
                            ]
                        }
                    ]
                }
            }]
        },
        {
            params: '/updateRules',
            requests: [{
                method: 'PUT',
                response: {
                    payload: {}
                }
            }]
        },
        {
            params: '/codacy/token',
            requests: [{
                method: 'PUT',
                response: {
                    payload: {}
                }
            }]
        }
    ]
});
