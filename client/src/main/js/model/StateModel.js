export class StateModel {
    constructor(stateJson) {
        this.lifecycle = stateJson.lifecycle;
        this.activatedServices = stateJson.activatedServices;
    }
}