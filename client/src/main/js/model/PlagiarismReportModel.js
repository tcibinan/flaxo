import {Seq} from 'immutable';
import {PlagiarismMatchModel} from './PlagiarismMatchModel';

export class PlagiarismReportModel {
    constructor(plagiarismReportJson) {
        this.url = plagiarismReportJson.url;
        this.date = new Date(plagiarismReportJson.date);
        this.matches =
            Seq(plagiarismReportJson.matches)
                .map(plagiarismMatchJson => new PlagiarismMatchModel(plagiarismMatchJson));
    }
}