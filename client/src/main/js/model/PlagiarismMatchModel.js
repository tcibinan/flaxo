export class PlagiarismMatchModel {
    constructor(plagiarismMatchJson) {
        this.url = plagiarismMatchJson.url;
        this.student1 = plagiarismMatchJson.student1;
        this.student2 = plagiarismMatchJson.student2;
        this.lines = plagiarismMatchJson.lines;
        this.percentage = plagiarismMatchJson.percentage;
    }
}