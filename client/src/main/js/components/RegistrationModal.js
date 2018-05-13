import React from 'react';
import ReactDOM from 'react-dom';
import axios from 'axios';
import {restUrl} from '../scripts';
import {Api} from '../Api';
import {Notification} from './Notification';
import {
    Button,
    Form,
    FormGroup,
    FormText,
    Input,
    Label,
    Modal,
    ModalBody,
    ModalFooter,
    ModalHeader
} from 'reactstrap';

export class RegistrationModal extends React.Component {

    constructor(props) {
        super(props);

        this.toggle = this.toggle.bind(this);
        this.registerUser = this.registerUser.bind(this);
        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);

        this.state = {
            show: false,
            username: null,
            password: null
        };
    }

    toggle() {
        this.setState({show: !this.state.show});
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
            .post('/user/register', {}, {
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
                        this.props.onLogin(this.state.username, this.state.password, account);

                        ReactDOM.render(
                            <Notification succeed message="Registration has finished successful."/>,
                            document.getElementById('notifications')
                        );
                    },
                    response => ReactDOM.render(
                        <Notification message={`Retrieving user after registration failed due to: ${response}`}/>,
                        document.getElementById('notifications')
                    )
                );
            })
            .catch(
                response => ReactDOM.render(
                    <Notification message={`Registration failed due to: ${response}`}/>,
                    document.getElementById('notifications')
                )
            );

        this.toggle();
    }

    render() {
        return (
            <span>
                <Button color="primary" onClick={this.toggle}>Register</Button>
                <Modal isOpen={this.state.show} toggle={this.toggle}>
                    <ModalHeader toggle={this.toggle}>Register</ModalHeader>
                    <ModalBody>
                        <Form>
                            <FormGroup>
                                <Label for="username-registration">Username</Label>
                                <Input type="text" id="username-registration" onChange={this.handleUsernameChange}/>
                                <FormText color="muted">Flaxo username</FormText>
                            </FormGroup>
                            <FormGroup>
                                <Label for="password-registration">Username</Label>
                                <Input type="password" id="password-registration" onChange={this.handlePasswordChange}/>
                                <FormText color="muted">Flaxo password</FormText>
                            </FormGroup>
                        </Form>
                    </ModalBody>
                    <ModalFooter>
                        <Button color="primary" onClick={this.registerUser}>Register</Button>{' '}
                        <Button color="secondary" onClick={this.toggle}>Cancel</Button>
                    </ModalFooter>
                </Modal>
            </span>
        );
    }

}