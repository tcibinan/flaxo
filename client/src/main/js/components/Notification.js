import React from 'react';
import {Alert} from 'reactstrap';

export class Notification extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        let status = "danger";
        if (this.props.succeed) status = "success";
        if (this.props.info) status = "info";

        return (
            <Alert color={status}>{this.props.message}</Alert>
        );
    }
}