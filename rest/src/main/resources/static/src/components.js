import 'bootstrap/dist/css/bootstrap.min.css';
import React from 'react';

export {RegistrationForm, AuthorizationPanel}

class RegistrationForm extends React.Component {
    render() {
        return <form id="register-user-form"
                     onSubmit="event.preventDefault(); registerUser(document.querySelector('#register-user-form'));">
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
        </form>;
    }
}

class AuthorizationPanel extends React.Component {
    render() {
        return <GithubAuthButton/>
    }
}

class GithubAuthButton extends React.Component {
    render() {
        return <button type="button"
                       className="btn btn-primary"
                       onClick="authWithGithub()">Authorize github</button>
    }
}