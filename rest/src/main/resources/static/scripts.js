function authWithGithub() {
    axios
        .get('github/auth', {
            baseURL: restUrl(),
            auth: credentials()
        })
        .then(redirectToGithubAuth())
        .catch(logResponse());
}

function registerUser(form) {
    const elements = Immutable.Seq(form.elements)
        .filter(element => element.tagName === 'INPUT')
        .toMap()
        .mapEntries(([i, element]) => [element.name, element.value]);

    axios
        .post('register', {}, {
            params: elements.toObject(),
            baseURL: restUrl()
        })
        .then(finalizeRegistration(elements))
        .catch(logResponse())
}

function finalizeRegistration(elements) {
    return () => elements.forEach((value, name) => Cookies.set(name, value));
}

function redirectToGithubAuth() {
    return response => {
        const payload = response.data.payload;
        const params = new URLSearchParams();
        const redirectParams = Immutable.Map(payload.params)
            .forEach((value, key) => params.append(key, value));

        window.location = payload.redirect + "?" + params.toString()
    };
}

function echo(message) {
    axios
        .get('echo', {
            baseURL: restUrl(),
            auth: credentials(),
            params: {
                message: message
            }
        })
        .then(logResponse())
        .catch(logResponse());
}

function logResponse() {
    return response => console.log(response);
}

function credentials() {
    return {
        username: username(),
        password: password()
    };
}

function username() {
    return Cookies.get('nickname');
}

function password() {
    return Cookies.get('password');
}

function restUrl() {
    // TODO 10.02.18: Remove hardcoded url (set it with spring template manager f.e. thymeleaf)
    return 'http://localhost:8080/rest';
}
