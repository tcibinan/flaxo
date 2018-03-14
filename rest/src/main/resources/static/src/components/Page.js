import '../styles/style.css';
import React from 'react';
import {Authentication} from './Authentication';
import {CoursesList} from './CoursesList';
import {credentials} from '../scripts';
import {Api} from '../Api';
import Cookies from 'js-cookie';
import ReactDOM from 'react-dom';
import {Notification} from './Notification';

export class Page extends React.Component {

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
            response => ReactDOM.render(
                <Notification succeed message={"Account retrieving failed.<br/>" + response}/>,
                document.getElementById('notifications')
            )
        );
    }

    render() {
        if (this.state.account != null) {
            return (
                <article className="page">
                    <Authentication account={this.state.account} onLogin={this.onLogin} onLogout={this.onLogout}/>
                    <CoursesList account={this.state.account}/>
                </article>
            );
        } else {
            return (
                <article className="page">
                    <h1>Flaxo educational system</h1>
                    <Authentication account={this.state.account} onLogin={this.onLogin} onLogout={this.onLogout}/>
                </article>
            );
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