export class BuildReportModel {
    constructor(buildReportsJson) {
        this.succeed = buildReportsJson.succeed;
        this.date = new Date(buildReportsJson.date);
    }
}