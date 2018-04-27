import React from 'react';
import {
    ButtonDropdown,
    DropdownItem,
    DropdownMenu,
    DropdownToggle
} from 'reactstrap';

export class CourseStatisticsDownloadMenu extends React.Component {

    constructor(props) {
        super(props);

        this.toggle = this.toggle.bind(this);
        this.state = {show: false};
    }

    toggle() {
        this.setState({show: !this.state.show});
    }

    render() {
        return (
            <div className="course-control">
                <ButtonDropdown isOpen={this.state.show} toggle={this.toggle}>
                    <DropdownToggle outline caret>
                        Download as
                    </DropdownToggle>
                    <DropdownMenu>
                        <DropdownItem disabled>json</DropdownItem>
                        <DropdownItem disabled>excel</DropdownItem>
                        <DropdownItem disabled>csv</DropdownItem>
                    </DropdownMenu>
                </ButtonDropdown>
            </div>
        );
    }
}