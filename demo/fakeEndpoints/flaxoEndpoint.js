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
                        id: 3312312,
                        githubId: "admin",
                        nickname: "admin",
                        isGithubAuthorized: true,
                        isTravisAuthorized: true,
                        isCodacyAuthorized: true
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
                            name: "java",
                            compatibleTestingLanguages: ["java", "kotlin"],
                            compatibleTestingFrameworks: ["junit"]
                        },
                        {
                            name: "kotlin",
                            compatibleTestingLanguages: ["kotlin"],
                            compatibleTestingFrameworks: ["junit", "spek"]
                        }
                    ]
                }
            }]
        },
        {
            params: "/allCourses",
            requests: [{
                method: 'GET',
                response: {
                    payload: [
                        {
                            id: "1",
                            name: "JavaCourse",
                            language: "java",
                            testingLanguage: "java",
                            testingFramework: "junit",
                            status: "init",
                            user: "admin",
                            userGithubId: "userGithubId",
                            students: ["student1", "student2", "student3", "student4", "student5"],
                            tasks: ["task1", "task2", "task3"]
                        },
                        {
                            id: "2",
                            name: "KotlinCourse",
                            language: "kotlin",
                            testingLanguage: "kotlin",
                            testingFramework: "spek",
                            status: "running",
                            user: "admin",
                            userGithubId: "userGithubId",
                            students: ["student1", "student2", "student3", "student4", "student5"],
                            tasks: ["task1", "task2", "task3"]
                        }
                    ]
                }
            }]
        },
        {
            params: "/{nickname}/{courseName}/statistics",
            requests: [{
                method: 'GET',
                response: {
                    payload: {
                        perStudentStats: {
                            student1: [
                                {
                                    id: 11,
                                    task: "task1",
                                    student: "student1",
                                    built: true,
                                    succeed: true
                                },
                                {
                                    id: 12,
                                    task: "task2",
                                    student: "student1",
                                    built: true,
                                    succeed: false
                                },
                                {
                                    id: 13,
                                    task: "task3",
                                    student: "student1",
                                    built: false,
                                    succeed: false
                                }
                            ],
                            student2: [
                                {
                                    id: 21,
                                    task: "task1",
                                    student: "student2",
                                    built: true,
                                    succeed: true
                                },
                                {
                                    id: 22,
                                    task: "task2",
                                    student: "student2",
                                    built: true,
                                    succeed: false
                                },
                                {
                                    id: 23,
                                    task: "task3",
                                    student: "student2",
                                    built: false,
                                    succeed: false
                                }
                            ],
                            student3: [
                                {
                                    id: 31,
                                    task: "task1",
                                    student: "student3",
                                    built: true,
                                    succeed: true
                                },
                                {
                                    id: 32,
                                    task: "task2",
                                    student: "student3",
                                    built: true,
                                    succeed: false
                                },
                                {
                                    id: 33,
                                    task: "task3",
                                    student: "student3",
                                    built: false,
                                    succeed: false
                                }
                            ],
                            student4: [
                                {
                                    id: 41,
                                    task: "task1",
                                    student: "student4",
                                    built: true,
                                    succeed: true
                                },
                                {
                                    id: 42,
                                    task: "task2",
                                    student: "student4",
                                    built: true,
                                    succeed: false
                                },
                                {
                                    id: 43,
                                    task: "task3",
                                    student: "student4",
                                    built: false,
                                    succeed: false
                                }
                            ],
                            student5: [
                                {
                                    id: 51,
                                    task: "task1",
                                    student: "student5",
                                    built: true,
                                    succeed: true
                                },
                                {
                                    id: 52,
                                    task: "task2",
                                    student: "student5",
                                    built: true,
                                    succeed: false
                                },
                                {
                                    id: 53,
                                    task: "task3",
                                    student: "student5",
                                    built: false,
                                    succeed: false
                                }
                            ],
                        },
                        perTaskStats: {
                            task1: {
                                mossResultUrl: "http://mossResultUrl",
                                mossPlagiarismMatches: [
                                    {
                                        students: {
                                            first: "student1",
                                            second: "student2"
                                        },
                                        lines: 50,
                                        link: "http://plagiarismLink",
                                        percentage: 85
                                    },
                                    {
                                        students: {
                                            first: "student3",
                                            second: "student5"
                                        },
                                        lines: 21,
                                        link: "http://plagiarismLink",
                                        percentage: 43
                                    }
                                ]
                            }
                        }
                    }
                }
            }]
        }
    ]
});
