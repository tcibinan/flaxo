import {Seq} from 'immutable';

export class StateModel {
    constructor(stateJson) {
        this.lifecycle = stateJson.lifecycle;
        this.activatedServices = Seq(stateJson.activatedServices);
    }
}