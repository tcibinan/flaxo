function authWithGithub() {
    axios.get('rest/github/auth', {}, authHeaders())
        .then(function (response) {
            console.log(response);
            console.log(response.data);
        })
        .catch(function (reason) {
            console.log(reason);
            console.log(reason.data);
        });
}

function authHeaders() {
    return {
        auth: {
            username: username(),
            password: password()
        }
    };
}

function username() {
    return "uuuu";
    // return document.querySelector("#nickname").value;
}

function password() {
    return username();
    // return document.querySelector("#password").value;
}
