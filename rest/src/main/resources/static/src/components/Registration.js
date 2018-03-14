import '../styles/style.css';
import React from 'react';
import ReactDOM from 'react-dom';
import axios from 'axios';
import {
    Alert,
    Button,
    ControlLabel,
    FormControl,
    FormGroup,
    HelpBlock,
    Modal
} from 'react-bootstrap';
import {credentials, restUrl} from '../scripts';
import {Api} from '../Api';
import {Notification} from './Notification';

export class Registration extends React.Component {

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
            password: null
        };
    }

    render() {
        return (
            <div>
                <Button onClick={this.handleShow}>
                    Register
                </Button>
                <Modal show={this.state.show} onHide={this.handleClose}>
                    <form>
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
                Api.retrieveAccount({
                        username: this.state.username,
                        password: this.state.password
                    },
                    account => {
                        this.props.onSuccess(this.state.username, this.state.password, account);

                        ReactDOM.render(
                            <Notification succeed message="Registration has finished successful."/>,
                            document.getElementById('notifications')
                        );
                    },
                    response => ReactDOM.render(
                        <Notification message={"Retrieving user after registration failed.<br/>" + response}/>,
                        document.getElementById('notifications')
                    )
                );
            })
            .catch(
                response => ReactDOM.render(
                    <Notification message={"Registration failed.<br/>" + response}/>,
                    document.getElementById('notifications')
                )
            );

        this.handleClose();
    }
}