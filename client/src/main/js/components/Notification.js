import React from 'react';
import {Alert} from 'reactstrap';

export class Notification extends React.Component {

    constructor(props) {
        super(props);

        let status = 'danger';
        if (this.props.failed) status = 'danger';
        if (this.props.succeed) status = 'success';
        if (this.props.info) status = 'info';

        this.state = {
            status: status,
            show: true
        }
    }

    render() {
        return (
            <Alert color={this.state.status}
                   isOpen={this.state.show}
                   onClick={() => this.setState({show: !this.state.show})}
            >
                {this.props.message}
            </Alert>
        );
    }
}