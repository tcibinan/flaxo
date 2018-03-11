import '../styles/style.css';
import React from 'react';
import ReactDOM from 'react-dom';
import {Alert, Button, ControlLabel, FormControl, FormGroup, HelpBlock} from 'react-bootstrap';
import Cookies from 'js-cookie';
import {Github} from './Github';
import {Registration} from "./Registration";
import {Api, credentials} from "../scripts";

export {Authentication}

class Authentication extends React.Component {

    constructor(props) {
        super(props);

        this.login = this.login.bind(this);
        this.logout = this.logout.bind(this);
        this.formStateFromAccount = this.formStateFromAccount.bind(this);
        this.setAccount = this.setAccount.bind(this);

        Api.retrieveAccount(
            credentials(),
            account => {
                console.log(account);
                this.setState(this.formStateFromAccount(account));
            },
            response => {
                console.log('account retrieving failed');
                console.log(response);
            }
        );

        this.state = {
            isLoggedIn: false,
            isGithubAuthorized: false,
            isTravisAuthorized: false,
            isCodacyAuthorized: false
        }
    }

    render() {
        if (this.state.isLoggedIn) {
            return (
                <section>
                    <LogoutForm onSuccess={this.logout}/>
                    <Github isGithubAuthorized={this.state.isGithubAuthorized}/>
                </section>
            )
        } else {
            return (
                <section>
                    <AuthenticationForm onSuccess={this.setAccount}/>
                    <Registration onSuccess={this.login}/>
                </section>
            )
        }
    }

    login() {
        this.setState({isLoggedIn: true});
    }

    setAccount(account) {
        this.setState(this.formStateFromAccount(account))
    }

    logout() {
        Cookies.remove('username');
        Cookies.remove('password');

        this.setState({isLoggedIn: false});
    }

    formStateFromAccount(account) {
        return {
            isLoggedIn: account != null,
            isGithubAuthorized: account != null ? account.githubAuthorized : false,
            isTravisAuthorized: account != null ? account.travisAuthorized : false,
            isCodacyAuthorized: account != null ? account.codacyAuthorized : false
        }
    }
}

class AuthenticationForm extends React.Component {

    constructor(props) {
        super(props);

        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
        this.authorizeUser = this.authorizeUser.bind(this);

        this.state = {
            username: null,
            password: null,
            onSuccess: props.onSuccess
        };
    }

    render() {
        return (
            <form>
                <FormGroup>
                    <ControlLabel>Username</ControlLabel>
                    <FormControl
                        type="text"
                        placeholder="Username"
                        onChange={this.handleUsernameChange}/>
                    <HelpBlock>Flaxo account username</HelpBlock>
                </FormGroup>
                <FormGroup>
                    <ControlLabel>Password</ControlLabel>
                    <FormControl
                        type="password"
                        placeholder="Password"
                        onChange={this.handlePasswordChange}/>
                    <HelpBlock>Flaxo account password</HelpBlock>
                </FormGroup>
                <Button type="submit" bsStyle="primary" onClick={this.authorizeUser}>Authorize</Button>
            </form>
        )
    }

    handleUsernameChange(event) {
        this.setState({username: event.target.value});
    }

    handlePasswordChange(event) {
        this.setState({password: event.target.value});
    }

    authorizeUser(event) {
        event.preventDefault();

        Api.retrieveAccount(
            {
                username: this.state.username,
                password: this.state.password
            },
            account => {
                Cookies.set('username', this.state.username);
                Cookies.set('password', this.state.password);

                this.state.onSuccess(account)
            },
            response => {
                console.log('account retrieving failed');
                console.log(response);

                ReactDOM.render(<AuthorizationFailed/>, document.getElementById('notifications'));
            }
        );
    }
}

class LogoutForm extends React.Component {

    constructor(props) {
        super(props);

        this.state = {onSuccess: props.onSuccess}
    }

    render() {
        return (
            <Button type="button" onClick={this.state.onSuccess}>Logout</Button>
        )
    }
}

class AuthorizationFailed extends React.Component {
    render() {
        return (
            <Alert bsStyle="danger">
                User with the given nickname and password was not found
            </Alert>
        );
    }
}