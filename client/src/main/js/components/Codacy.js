import React from 'react';
import ReactDOM from 'react-dom';
import {
    Button,
    DropdownItem,
    Form,
    FormGroup,
    Input,
    Popover,
    PopoverBody,
    PopoverHeader
} from 'reactstrap';
import {credentials} from '../scripts';
import {Api} from '../Api';
import {Notification} from './Notification';

export class Codacy extends React.Component {

    constructor(props) {
        super(props);
        this.toggle = this.toggle.bind(this);
        this.handleCodacyTokenChange = this.handleCodacyTokenChange.bind(this);
        this.updateCodacyToken = this.updateCodacyToken.bind(this);

        this.state = {
            show: false,
            codacyToken: null
        }
    }

    toggle() {
        this.setState({show: !this.state.show});
    }

    render() {
        return (
            <DropdownItem id="codacy_authorization_item"
                          className={this.props.isAuthorized ? 'text-success' : ''}
                          onClick={this.toggle}
                          toggle={false}
            >
                Codacy
                <Popover placement="left"
                         isOpen={this.state.show}
                         target="codacy_authorization_item"
                >
                    <PopoverHeader>Codacy authorization</PopoverHeader>
                    <PopoverBody>
                        <p>
                            Codacy authorization is only possible by manually generating api token in{' '}
                            <a href="https://app.codacy.com/account/apiTokens">codacy account settings</a>.
                        </p>
                        <Form>
                            <FormGroup>
                                <Input type="text"
                                       name="codacyTokenInput"
                                       id="codacyTokenInput"
                                       onChange={this.handleCodacyTokenChange}
                                />
                            </FormGroup>
                            <Button color="primary" onClick={this.updateCodacyToken}>Add codacy token</Button>
                        </Form>
                    </PopoverBody>
                </Popover>
            </DropdownItem>
        );
    }

    handleCodacyTokenChange(event) {
        const codacyToken = event.target.value;
        this.setState({codacyToken});
    }

    updateCodacyToken() {
        this.toggle();
        Api.addCodacyToken(credentials(),
            this.state.codacyToken,
            () => ReactDOM.render(
                <Notification succeed message="Codacy token was successfully added to account"/>,
                document.getElementById('notifications')
            ),
            response => ReactDOM.render(
                <Notification failed message={`Codacy token wasn't added to account due to: ${response}`}/>,
                document.getElementById('notifications')
            )
        )
    }
}