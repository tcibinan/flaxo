import '../styles/style.css';
import React from 'react';
import {Alert} from 'react-bootstrap';

export function Notification(props) {
    return (
        <Alert bsStyle={props.succeed ? "success" : "danger"}>
            {props.message}
        </Alert>
    );
}