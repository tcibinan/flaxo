import React from 'react';
import {Badge} from 'reactstrap';

export class Travis extends React.Component {

    constructor(props) {
        super(props);

        this.state = {isAuthorized: props.isAuthorized};
    }

    render() {
        return this.state.isAuthorized
            ? <Badge color="primary">Travis</Badge>
            : <Badge color="secondary">Travis</Badge>
    }
}