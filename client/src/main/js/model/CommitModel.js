export class CommitModel {
    constructor(commitJson) {
        this.sha = commitJson.sha;
        this.date = commitJson.date ? new Date(commitJson.date) : null;
    }
}