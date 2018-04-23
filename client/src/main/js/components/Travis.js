import React from 'react';
import {
    DropdownItem,
    Popover,
    PopoverBody,
    PopoverHeader
} from 'reactstrap';

export class Travis extends React.Component {

    constructor(props) {
        super(props);
        this.toggle = this.toggle.bind(this);

        this.state = {
            show: false
        }
    }

    toggle() {
        this.setState({show: !this.state.show});
    }

    render() {
        return (
            <DropdownItem id="travis_authorization_item"
                          className={this.props.isAuthorized ? 'text-success' : ''}
                          onClick={this.toggle}
                          toggle={false}>
                Travis
                <Popover placement="left"
                         isOpen={this.state.show}
                         target="travis_authorization_item"
                         toggle={this.toggle}
                >
                    <PopoverHeader>Travis authorization</PopoverHeader>
                    <PopoverBody>
                        <p>
                            Travis authorization is done in background. The only requirement:
                            you should be authorized in <a href="https://travis-ci.org/">travis-ci</a>{' '}
                            with your github account.
                        </p>
                    </PopoverBody>
                </Popover>
            </DropdownItem>
        );
    }
}