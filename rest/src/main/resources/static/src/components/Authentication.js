import '../styles/style.css';
import React from 'react';
import {Button, ControlLabel, FormControl, FormGroup, HelpBlock} from 'react-bootstrap';
import Cookies from 'js-cookie';
import {Github} from './Github';
import {Registration} from "./Registration";

export {Authentication}

class Authentication extends React.Component {

    constructor(props) {
        super(props);

        const nickname = Cookies.get('nickname');
        const password = Cookies.get('password');

        this.login = this.login.bind(this);
        this.logout = this.logout.bind(this);

        this.state = {
            isLoggedIn: nickname !== undefined & password !== undefined,
            isGithubAuthorized: false
        };
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
                    <AuthenticationForm onSuccess={this.login}/>
                    <Registration onSuccess={this.login}/>
                </section>
            )
        }
    }

    login() {
        this.setState({isLoggedIn: true});
    }

    logout() {
        Cookies.remove("nickname");
        Cookies.remove("password");

        this.setState({isLoggedIn: false});
    }
}

class AuthenticationForm extends React.Component {

    constructor(props) {
        super(props);

        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
        this.authorizeUser = this.authorizeUser.bind(this);

        this.state = {
            nickname: null,
            password: null,
            onSuccess: props.onSuccess
        };
    }

    render() {
        return (
            <form>
                <FormGroup
                    controlId="authorization-form-nickname">
                    <ControlLabel>Username</ControlLabel>
                    <FormControl
                        type="text"
                        placeholder="Username"/>
                    <HelpBlock>Flaxo account username</HelpBlock>
                </FormGroup>
                <FormGroup
                    controlId="authorization-form-password">
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
        this.setState({nickname: event.target.value});
    }

    handlePasswordChange(event) {
        this.setState({password: event.target.value});
    }

    authorizeUser(event) {
        event.preventDefault();

        Cookies.set('nickname', this.state.nickname);
        Cookies.set('password', this.state.password);

        this.state.onSuccess()
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