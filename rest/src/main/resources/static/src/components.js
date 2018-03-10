import 'bootstrap/dist/css/bootstrap.min.css';
import React from 'react';
import ReactDOM from "react-dom";
import Cookies from "js-cookie";
import 'axios'
import {credentials, logResponse, restUrl} from "./scripts.js";

export {RegistrationForm, AuthorizationPanel}

class RegistrationForm extends React.Component {
    render() {
        return (
            <form id="register-form"
                  onSubmit={this.registerUser}>
                <label htmlFor="nickname">
                    <input id="nickname"
                           type="text"
                           placeholder="Username"/>
                </label>
                <label htmlFor="password">
                    <input id="password"
                           type="password"
                           placeholder="Password"/>
                </label>
                <button type="submit">Register</button>
            </form>
        );
    }

    registerUser(event) {
        event.preventDefault();

        let form = document.querySelector('#register-user-form');

        const elements = Immutable.Seq(form.elements)
            .filter(element => element.tagName === 'INPUT')
            .toMap()
            .mapEntries(([i, element]) => [element.name, element.value]);

        axios
            .post('register', {}, {
                params: elements.toObject(),
                baseURL: restUrl()
            })
            .then(this.finalizeRegistration(elements))
            .catch(logResponse())
    }

    finalizeRegistration(elements) {
        return () => elements.forEach((value, name) => Cookies.set(name, value));
    }
}

class AuthorizationForm extends React.Component {
    render() {
        return (
            <form id="login-form"
                  onSubmit={this.authorizeUser}>
                <label htmlFor="nickname">
                    <input id="nickname"
                           type="text"
                           placeholder="Username"/>
                </label>
                <label htmlFor="password">
                    <input id="password"
                           type="password"
                           placeholder="Password"/>
                </label>
                <button type="submit">Authorize</button>
            </form>
        )
    }

    authorizeUser(event) {
        event.preventDefault();

        let form = document.querySelector('#login-form');
        // todo: authorize user
    }
}

class RegistrationButton extends React.Component {
    render() {
        return (
            <button type="button"
                    className="btn btn-light"
                    onClick={this.openRegistrationPopup}>
                Register
            </button>
        )
    }

    openRegistrationPopup() {
        ReactDOM.render(<RegistrationForm/>, document.getElementById('root'))
    }
}

class AuthorizationPanel extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            isLoggedIn: props.isLoggedIn,
            isGithubAuthorized: props.isGithubAuthorized
        };
    }

    render() {
        if (this.state.isLoggedIn) {
            return (
                <section>
                    <Github isGithubAuthorized={this.state.isGithubAuthorized}/>
                </section>
            )
        } else {
            return (
                <section>
                    <AuthorizationForm/>
                    <RegistrationButton/>
                </section>
            )
        }
    }
}

class Github extends React.Component {

    constructor(props) {
        super(props);
        this.state = {isGithubAuthorized: props.isGithubAuthorized}
    }

    render() {
        if (this.state.isGithubAuthorized) {
            return (
                <span className="badge badge-success">Github authorized</span>
            )
        } else {
            return (
                <button type="button"
                        className="btn btn-primary"
                        onClick={this.authWithGithub}>
                    Authorize github
                </button>
            )
        }
    }

    authWithGithub() {
        axios
            .get('github/auth', {
                baseURL: restUrl(),
                auth: credentials()
            })
            .then(this.redirectToGithubAuth())
            .catch(logResponse());
    }

    redirectToGithubAuth() {
        return response => {
            const payload = response.data.payload;
            const params = new URLSearchParams();
            const redirectParams = Immutable.Map(payload.params)
                .forEach((value, key) => params.append(key, value));

            window.location = payload.redirect + "?" + params.toString()
        };
    }
}