import '../styles/style.css';
import React from "react";
import axios from 'axios'
import {credentials, restUrl} from "../scripts.js";

export {Github}

class Github extends React.Component {

    constructor(props) {
        super(props);
        this.state = {isGithubAuthorized: props.isGithubAuthorized};
    }

    render() {
        if (this.state.isGithubAuthorized) {
            return (
                <span className="badge badge-success">Github authorized</span>
            )
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
            .then(this.redirectToGithubAuth())
            .catch(response => {
                console.log("github auth failed");
                console.log(response);
            });
    }

    redirectToGithubAuth() {
        return response => {
            const payload = response.data.payload;
            const params = new URLSearchParams();
            const redirectParams = Immutable.Map(payload.params)
                .forEach((value, key) => params.append(key, value));

            window.location = payload.redirect + "?" + params.toString();
        };
    }
}