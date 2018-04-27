import React from 'react';
import {
    ButtonDropdown,
    DropdownItem,
    DropdownMenu,
    DropdownToggle
} from 'reactstrap';
import ReactDOM from 'react-dom';
import {Set} from 'immutable';
import {credentials} from '../scripts';
import {Notification} from './Notification';
import {Api} from '../Api';

export class ServiceActivationMenu extends React.Component {

    constructor(props) {
        super(props);

        this.toggle = this.toggle.bind(this);
        this.activateCodacy = this.activateCodacy.bind(this);
        this.activateTravis = this.activateTravis.bind(this);
        this.state = {
            show: false,
            services: Set(['CODACY', 'TRAVIS'])
        };
    }

    toggle() {
        this.setState({show: !this.state.show});
    }

    activateTravis() {
        Api.activateTravis(credentials(),
            this.props.course.name,
            course => ReactDOM.render(
                <Notification succeed message={`Travis activation finished successfully
                for ${course.name} course`}/>,
                document.getElementById('notifications')
            ),
            response => ReactDOM.render(
                <Notification failed message={`Travis activation failed due to: ${response}`}/>,
                document.getElementById('notifications')
            )
        )
    }

    activateCodacy() {
        Api.activateCodacy(credentials(),
            this.props.course.name,
            course => ReactDOM.render(
                <Notification succeed message={`Codacy activation finished successfully
                for ${course.name} course`}/>,
                document.getElementById('notifications')
            ),
            response => ReactDOM.render(
                <Notification failed message={`Codacy activation failed due to: ${response}`}/>,
                document.getElementById('notifications')
            )
        )
    }

    render() {
        return (
            <div className="course-control">
                <ButtonDropdown isOpen={this.state.show} toggle={this.toggle}>
                    <DropdownToggle outline caret color="primary"
                                    disabled={
                                        this.props.course.state.lifecycle !== 'RUNNING'
                                        || this.state.services
                                            .every(service =>
                                                this.props.course.state.activatedServices.includes(service)
                                            )
                                            ? 'disabled'
                                            : ''
                                    }
                    >
                        Activate service
                    </DropdownToggle>
                    <DropdownMenu>
                        <DropdownItem
                            disabled={
                                this.props
                                    .course
                                    .state
                                    .activatedServices
                                    .includes('CODACY')
                                    ? 'disabled'
                                    : ''
                            }
                            onClick={this.activateCodacy}
                        >
                            codacy
                        </DropdownItem>
                        <DropdownItem
                            disabled={
                                this.props
                                    .course
                                    .state
                                    .activatedServices
                                    .includes('TRAVIS')
                                    ? 'disabled'
                                    : ''
                            }
                            onClick={this.activateTravis}
                        >
                            travis
                        </DropdownItem>
                    </DropdownMenu>
                </ButtonDropdown>
            </div>
        );
    }
}