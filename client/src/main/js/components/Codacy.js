import '../../styles/style.css';
import React from 'react';
import {Label} from 'react-bootstrap';

export class Codacy extends React.Component {

    constructor(props) {
        super(props);

        this.state = {isAuthorized: props.isAuthorized};
    }

    render() {
        return this.state.isAuthorized
            ? <Label bsStyle="primary">Codacy</Label>
            : <Label>Codacy</Label>;
    }
}