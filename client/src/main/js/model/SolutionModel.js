import {Seq} from 'immutable';
import {BuildReportModel} from './BuildReportModel';
import {CodeStyleReportModel} from './CodeStyleReportModel';
import {CommitModel} from './CommitModel';

export class SolutionModel {
    constructor(solutionJson) {
        this.task = solutionJson.task;
        this.student = solutionJson.student;
        this.score = solutionJson.score;
        this.commits =
            Seq(solutionJson.commits)
                .map(commitJson => new CommitModel(commitJson));
        this.buildReports =
            Seq(solutionJson.buildReports)
                .map(buildReportsJson => new BuildReportModel(buildReportsJson));
        this.codeStyleReports =
            Seq(solutionJson.codeStyleReports)
                .map(codeStyleReportJson => new CodeStyleReportModel(codeStyleReportJson));
    }
}