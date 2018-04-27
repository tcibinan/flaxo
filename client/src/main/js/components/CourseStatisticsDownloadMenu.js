import React from 'react';
import {
    ButtonDropdown,
    DropdownItem,
    DropdownMenu,
    DropdownToggle
} from 'reactstrap';
import {credentials} from '../scripts';
import {Api} from '../Api';
import ReactDOM from 'react-dom';
import {Notification} from './Notification';

export class CourseStatisticsDownloadMenu extends React.Component {

    constructor(props) {
        super(props);

        this.toggle = this.toggle.bind(this);
        this.state = {show: false};
    }

    toggle() {
        this.setState({show: !this.state.show});
    }

    downloadAs(format) {
        Api.downloadStatistics(credentials(),
            this.props.course.name,
            format,
            data => {
                const url = window.URL.createObjectURL(new Blob([data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', `${this.props.course.name}-statistics.${format}`);
                document.body.appendChild(link);
                link.click();
            },
            response => ReactDOM.render(
                <Notification failed message={`File downloading failed due to: ${response}`}/>,
                document.getElementById('notifications')
            )
        )
    }

    render() {
        return (
            <div className="course-control">
                <ButtonDropdown isOpen={this.state.show} toggle={this.toggle}>
                    <DropdownToggle outline caret>
                        Download as
                    </DropdownToggle>
                    <DropdownMenu>
                        <DropdownItem onClick={() => this.downloadAs('json')}>json</DropdownItem>
                        <DropdownItem disabled onClick={() => this.downloadAs('xls')}>excel</DropdownItem>
                        <DropdownItem disabled onClick={() => this.downloadAs('csv')}>csv</DropdownItem>
                    </DropdownMenu>
                </ButtonDropdown>
            </div>
        );
    }
}