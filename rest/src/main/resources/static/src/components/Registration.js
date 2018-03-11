import '../styles/style.css';
import React from 'react';
import ReactDOM from 'react-dom';
import Cookies from 'js-cookie';
import axios from 'axios'
import {Alert, Button, ControlLabel, FormControl, FormGroup, HelpBlock, Modal} from 'react-bootstrap';
import {restUrl} from '../scripts.js';

export {Registration}

class Registration extends React.Component {

    constructor(props) {
        super(props);

        this.handleShow = this.handleShow.bind(this);
        this.handleClose = this.handleClose.bind(this);
        this.registerUser = this.registerUser.bind(this);
        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);

        this.state = {
            show: false,
            username: null,
            password: null,
            onSuccess: props.onSuccess
        };
    }

    render() {
        return (
            <div>
                <Button onClick={this.handleShow}>
                    Register
                </Button>
                <Modal show={this.state.show} onHide={this.handleClose}>
                    <form id="register-form">
                        <Modal.Header>
                            <Modal.Title>Register</Modal.Title>
                        </Modal.Header>
                        <Modal.Body>
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
                                    type="text"
                                    placeholder="Password"
                                    onChange={this.handlePasswordChange}/>
                                <HelpBlock>Flaxo account password</HelpBlock>
                            </FormGroup>
                        </Modal.Body>
                        <Modal.Footer>
                            <Button onClick={this.handleClose}>Close</Button>
                            <Button bsStyle="primary" onClick={this.registerUser}>Register</Button>
                        </Modal.Footer>
                    </form>
                </Modal>
            </div>
        );
    }

    handleClose() {
        this.setState({show: false});
    }

    handleShow() {
        this.setState({show: true});
    }

    handleUsernameChange(event) {
        this.setState({username: event.target.value});
    }

    handlePasswordChange(event) {
        this.setState({password: event.target.value});
    }

    registerUser(event) {
        event.preventDefault();

        axios
            .post('register', {}, {
                params: {
                    nickname: this.state.username,
                    password: this.state.password
                },
                baseURL: restUrl()
            })
            .then(() => {
                Cookies.set('username', this.state.username);
                Cookies.set('password', this.state.password);

                ReactDOM.render(<RegistrationSuccessful/>, document.getElementById('notifications'));

                this.state.onSuccess();
            })
            .catch((response) => {
                console.log("Registration failed");
                console.log(response);
            });

        this.handleClose();
    }
}

class RegistrationSuccessful extends React.Component {
    render() {
        return (
            <Alert bsStyle="success">
                Registration has been finished successful
            </Alert>
        );
    }
}