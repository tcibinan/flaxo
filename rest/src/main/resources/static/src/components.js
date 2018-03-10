import React from 'react';

export {AuthorizationPanel}

class AuthorizationPanel extends React.Component {
    render() {
        return <form id="register-user-form"
                     onSubmit="event.preventDefault(); registerUser(document.querySelector('#register-user-form'));">
            <label htmlFor="nickname">
                <input id="nickname" type="text" placeholder="Username"/>
            </label>
            <label htmlFor="password">
                <input id="password" type="password" placeholder="Password"/>
            </label>
        </form>;
    }
}

class GithubAuthButton extends React.Component {
    render() {
        return <button onClick="authWithGithub()">Authorize github</button>
    }
}