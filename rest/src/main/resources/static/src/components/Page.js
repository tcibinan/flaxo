import '../styles/style.css';
import React from 'react';
import {Authentication} from "./Authentication";
import {CoursesList} from "./CoursesList";
import {credentials} from "../scripts";
import {Api} from "../Api";
import Cookies from "js-cookie";

export {Page}

class Page extends React.Component {

    constructor(props) {
        super(props);

        this.onLogin = this.onLogin.bind(this);
        this.onLogout = this.onLogout.bind(this);

        this.state = {
            account: null
        };

        Api.retrieveAccount(
            credentials(),
            account => {
                this.setState({account: account});
            },
            response => {
                console.log('account retrieving failed');
                console.log(response);
            }
        );
    }

    render() {
        if (this.state.account != null) {
            return (
                <article className="page">
                    <Authentication account={this.state.account} onLogin={this.onLogin} onLogout={this.onLogout}/>
                    <CoursesList account={this.state.account}/>
                </article>
            )
        } else {
            return (
                <article className="page">
                    <h1>Flaxo educational system</h1>
                    <Authentication account={this.state.account} onLogin={this.onLogin} onLogout={this.onLogout}/>
                </article>
            )
        }
    }

    onLogin(username, password, account) {
        Cookies.set('username', username);
        Cookies.set('password', password);

        this.setState({account: account});
    }

    onLogout() {
        Cookies.remove('username');
        Cookies.remove('password');

        this.setState({account: null});
    }
}