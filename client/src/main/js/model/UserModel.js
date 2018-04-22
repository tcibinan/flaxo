export class UserModel {
    constructor(userJson) {
        this.githubId = userJson.githubId;
        this.nickname = userJson.nickname;
        this.githubAuthorized = userJson.githubAuthorized;
        this.travisAuthorized = userJson.travisAuthorized;
        this.codacyAuthorized = userJson.codacyAuthorized;
    }
}