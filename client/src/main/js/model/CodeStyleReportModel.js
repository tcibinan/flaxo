export class CodeStyleReportModel {
    constructor(codeStyleReportJson) {
        this.grade = codeStyleReportJson.grade;
        this.date = new Date(codeStyleReportJson.date);
    }
}