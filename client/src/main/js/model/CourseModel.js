import {StateModel} from './StateModel';
import {UserModel} from './UserModel';
import {Seq} from 'immutable';

export class CourseModel {
    constructor(courseJson) {
        this.name = courseJson.name;
        this.language = courseJson.language;
        this.testingLanguage = courseJson.testingLanguage;
        this.testingFramework = courseJson.testingFramework;
        this.url = courseJson.url;
        this.state = new StateModel(courseJson.state);
        this.user = new UserModel(courseJson.user);
        this.students = Seq(courseJson.students);
        this.tasks = Seq(courseJson.tasks);
    }
}