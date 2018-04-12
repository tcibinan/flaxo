import React from 'react';
import ReactDOM from 'react-dom';
import Immutable from 'immutable';
import axios from 'axios';
import {credentials, restUrl} from '../scripts.js';
import {Notification} from './Notification';
import {Badge, Button} from 'reactstrap';

export class Github extends React.Component {

    constructor(props) {
        super(props);

        this.authWithGithub = this.authWithGithub.bind(this);
        this.redirectToGithubAuth = this.redirectToGithubAuth.bind(this);

        this.state = {isAuthorized: props.isAuthorized};
    }

    render() {
        if (this.state.isAuthorized) {
            return <Badge color="primary">Github</Badge>;
        } else {
            return (
                <Button color="primary" onClick={this.authWithGithub}>
                    Authorize with github
                </Button>
            );
        }
    }

    authWithGithub() {
        axios
            .get('github/auth', {
                baseURL: restUrl(),
                auth: credentials()
            })
            .then(this.redirectToGithubAuth)
            .catch(response => ReactDOM.render(
                <Notification message={`Github auth failed due to: ${response}`}/>,
                document.getElementById('notifications')
            ));
    }

    redirectToGithubAuth(response) {
        const payload = response.data.payload;
        const params = new URLSearchParams();
        Immutable.Map(payload.params)
            .forEach((value, key) => params.append(key, value));

        window.location = payload.redirect + '?' + params.toString();
    }
}