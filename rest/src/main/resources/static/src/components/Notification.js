import '../styles/style.css';
import React from 'react';
import {Alert} from 'react-bootstrap';

export function Notification(props) {

    let status = "danger";
    if (props.succeed) {
        status = "success";
    }
    if (props.info) {
        status = "info";
    }

    return (
        <Alert bsStyle={status}>
            {props.message}
        </Alert>
    );
}