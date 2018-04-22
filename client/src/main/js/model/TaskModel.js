import {Seq} from 'immutable';
import {SolutionModel} from './SolutionModel';
import {PlagiarismReportModel} from './PlagiarismReportModel';

export class TaskModel {
    constructor(taskJson) {
        this.branch = taskJson.branch;
        this.deadline = taskJson.deadline ? new Date(taskJson.deadline) : null;
        this.url = taskJson.url;
        this.plagiarismReports =
            Seq(taskJson.plagiarismReports)
                .map(plagiarismReportJson => new PlagiarismReportModel(plagiarismReportJson));
        this.solutions =
            Seq(taskJson.solutions)
                .map(solutionReportJson => new SolutionModel(solutionReportJson));
    }
}