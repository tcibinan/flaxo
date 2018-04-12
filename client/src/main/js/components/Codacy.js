import React from 'react';
import {Badge} from 'reactstrap';

export class Codacy extends React.Component {

    constructor(props) {
        super(props);

        this.state = {isAuthorized: props.isAuthorized};
    }

    render() {
        return this.state.isAuthorized
            ? <Badge color="primary">Codacy</Badge>
            : <Badge color="secondary">Codacy</Badge>
    }
}