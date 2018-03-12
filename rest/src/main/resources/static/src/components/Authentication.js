import '../styles/style.css';
import React from 'react';
import ReactDOM from 'react-dom';
import {Alert, Button, ControlLabel, FormControl, FormGroup, HelpBlock, Panel} from 'react-bootstrap';
import {Api} from "../Api";
import {Registration} from "./Registration";
import {Github} from './Github';
import {Travis} from "./Travis";
import {Codacy} from "./Codacy";
import {Notification} from "./Notification";

export class Authentication extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        if (this.props.account != null) {
            return (
                <Panel>
                    <Panel.Body>
                        <Logout onSuccess={this.props.onLogout}/>
                        <Github isAuthorized={this.props.account.githubAuthorized}/>
                        <Travis isAuthorized={this.props.account.travisAuthorized}/>
                        <Codacy isAuthorized={this.props.account.codacyAuthorized}/>
                    </Panel.Body>
                </Panel>
            );
        } else {
            return (
                <Panel>
                    <Panel.Body>
                        <AuthenticationForm onSuccess={this.props.onLogin}/>
                    </Panel.Body>
                    <Panel.Body>
                        <Registration onSuccess={this.props.onLogin}/>
                    </Panel.Body>
                </Panel>
            );
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
            password: null
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
                this.props.onSuccess(this.state.username, this.state.password, account);
            },
            response => ReactDOM.render(
                <Notification message="User with the given nickname and password was not found."/>,
                document.getElementById('notifications')
            )
        );
    }
}

class Logout extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <Button type="button" onClick={this.props.onSuccess}>Logout</Button>
        );
    }
}