import React from 'react';
import {RegistrationModal} from './RegistrationModal';
import {Github} from './Github';
import {Travis} from './Travis';
import {Codacy} from './Codacy';
import {
    Collapse,
    DropdownItem,
    DropdownMenu,
    DropdownToggle,
    Nav,
    Navbar,
    NavbarBrand,
    NavItem,
    NavLink,
    UncontrolledDropdown
} from 'reactstrap';

export class NavigationBar extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <Navbar color="light" light expand="md">
                <NavbarBrand href="/">Flaxo</NavbarBrand>
                <Collapse navbar>
                    <Nav className="ml-auto" navbar>
                        <NavItem>
                            <NavLink>Signed as {this.props.account.nickname}</NavLink>
                        </NavItem>
                        <UncontrolledDropdown nav inNavbar>
                            <DropdownToggle nav caret>
                                Options
                            </DropdownToggle>
                            <DropdownMenu right>
                                <DropdownItem>
                                    <Github isAuthorized={this.props.account.githubAuthorized}/>{' '}
                                </DropdownItem>
                                <DropdownItem>
                                    <Travis isAuthorized={this.props.account.travisAuthorized}/>{' '}
                                </DropdownItem>
                                <DropdownItem>
                                    <Codacy isAuthorized={this.props.account.codacyAuthorized}/>{' '}
                                </DropdownItem>
                                <DropdownItem divider/>
                                <DropdownItem onClick={this.props.onLogout}>
                                    Logout
                                </DropdownItem>
                            </DropdownMenu>
                        </UncontrolledDropdown>
                    </Nav>
                </Collapse>
            </Navbar>
        );
    }
}
