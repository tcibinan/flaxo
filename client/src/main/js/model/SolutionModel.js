import {Seq} from 'immutable';
import {BuildReportModel} from './BuildReportModel';
import {CodeStyleReportModel} from './CodeStyleReportModel';

export class SolutionModel {
    constructor(solutionJson) {
        this.task = solutionJson.task;
        this.student = solutionJson.student;
        this.score = solutionJson.score;
        this.date = solutionJson.date ? new Date(solutionJson.date) : null;
        this.buildReports =
            Seq(solutionJson.buildReports)
                .map(buildReportsJson => new BuildReportModel(buildReportsJson));
        this.codeStyleReports =
            Seq(solutionJson.codeStyleReports)
                .map(codeStyleReportJson => new CodeStyleReportModel(codeStyleReportJson));
    }
}