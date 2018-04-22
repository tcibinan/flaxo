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

export function suggestScore(buildReport, codeStyleReport, date, deadline) {
    if (date == null || buildReport == null) {
        return 0;
    }

    const buildSucceed = buildReport ? buildReport.succeed : false;
    const codeStyleGrade = codeStyleReport ? codeStyleReport.grade : null;
    const deadlinePassed = deadline ? date < deadline : true;

    return 60 * buildSucceed + 5 * gradeToNum(codeStyleGrade) + 10 * deadlinePassed;
}