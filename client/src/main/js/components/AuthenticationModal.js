import React from 'react';
import {Api} from '../Api';
import ReactDOM from 'react-dom';
import {
    Button,
    Form,
    FormGroup,
    FormText,
    Input,
    Label, Modal, ModalBody, ModalFooter, ModalHeader
} from 'reactstrap';
import {Notification} from './Notification';

export class AuthenticationModal extends React.Component {

    constructor(props) {
        super(props);

        this.toggle = this.toggle.bind(this);
        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
        this.authorizeUser = this.authorizeUser.bind(this);

        this.state = {
            show: false,
            username: null,
            password: null
        };
    }

    toggle() {
        this.setState({show: !this.state.show});
    }

    render() {
        return (
            <span>
                <Button color="primary" outline
                        onClick={this.toggle}>Authorize</Button>
                <Modal isOpen={this.state.show} toggle={this.toggle}>
                    <ModalHeader toggle={this.toggle}>Authorize</ModalHeader>
                    <ModalBody>
                        <Form>
                            <FormGroup>
                                <Label for="username-authentication">Username</Label>
                                <Input id="username-authentication" onChange={this.handleUsernameChange}/>
                                <FormText color="muted">Flaxo username</FormText>
                            </FormGroup>
                            <FormGroup>
                                <Label for="password-authentication">Password</Label>
                                <Input id="password-authentication" onChange={this.handlePasswordChange}/>
                                <FormText color="muted">Flaxo password</FormText>
                            </FormGroup>
                        </Form>
                    </ModalBody>
                    <ModalFooter>
                        <Button color="primary" onClick={this.authorizeUser}>Authorize</Button>{' '}
                        <Button color="secondary" onClick={this.toggle}>Cancel</Button>
                    </ModalFooter>
                </Modal>
            </span>
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
                this.props.onLogin(this.state.username, this.state.password, account);
                this.toggle();
            },
            () => ReactDOM.render(
                <Notification message="User with the given nickname and password was not found."/>,
                document.getElementById('notifications')
            )
        );
    }
}