import '../styles/style.css';
import React from 'react';
import {Label} from "react-bootstrap";
import Immutable from 'immutable';
import axios from 'axios'
import {credentials, restUrl} from "../scripts.js";

export {Github}

class Github extends React.Component {

    constructor(props) {
        super(props);

        this.authWithGithub = this.authWithGithub.bind(this);
        this.redirectToGithubAuth = this.redirectToGithubAuth.bind(this);

        this.state = {isGithubAuthorized: props.isGithubAuthorized};
    }

    render() {
        if (this.state.isGithubAuthorized) {
            return <Label bsStyle="primary">Github authorized</Label>;
        } else {
            return (
                <button type="button" className="btn btn-primary" onClick={this.authWithGithub}>
                    Authorize github
                </button>
            )
        }
    }

    authWithGithub() {
        axios
            .get('github/auth', {
                baseURL: restUrl(),
                auth: credentials()
            })
            .then(this.redirectToGithubAuth)
            .catch(response => {
                console.log('github auth failed');
                console.log(response);
            });
    }

    redirectToGithubAuth(response) {
        const payload = response.data.payload;
        const params = new URLSearchParams();
        Immutable.Map(payload.params)
            .forEach((value, key) => params.append(key, value));

        window.location = payload.redirect + '?' + params.toString();
    }
}