import '../styles/style.css';
import React from "react";
import {Button, ControlLabel, FormControl, FormGroup, HelpBlock} from 'react-bootstrap';
import {Github} from "./Github";
import {Registration} from "./Registration";

export {Authentication}

class Authentication extends React.Component {

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
                    <AuthenticationForm/>
                    <Registration/>
                </section>
            )
        }
    }
}

class AuthenticationForm extends React.Component {
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
                        placeholder="Password"/>
                    <HelpBlock>Flaxo account password</HelpBlock>
                </FormGroup>
                <Button type="submit" bsStyle="primary">Authorize</Button>
            </form>
        )
    }

    authorizeUser(event) {
        event.preventDefault();

        let form = document.querySelector('#login-form');
        // todo: authorize user
    }
}