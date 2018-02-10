function authWithGithub() {
    axios
        .get('github/auth', {
            baseURL: restUrl(),
            auth: credentials()
        })
        .then(response => {
            const payload = response.data.payload;
            const params = new URLSearchParams();
            const redirectParams = Immutable.Map(payload.params)
                .forEach((value, key) => params.append(key, value));

            window.location = payload.redirect + "?" + params.toString()
        })
        .catch(logResponse);
}


function echo() {
    axios
        .get('echo', {
            baseURL: restUrl(),
            auth: credentials(),
            params: {
                message: "helloworldsfsdfsdm"
            }
        })
        .then(logResponse)
        .catch(logResponse);
}

function logResponse(response) {
    console.log(response);
}

function credentials() {
    return {
        username: username(),
        password: password()
    };
}

function username() {
    return document.querySelector("#nickname").value;
}

function password() {
    return document.querySelector("#password").value;
}

function restUrl() {
    // TODO 10.02.18: Remove hardcoded url (set it with spring template manager f.e. thymeleaf)
    return 'http://173.0.157.203:8080/rest';
}
