import React from 'react';
import ReactDOM from 'react-dom';
import Cookies from 'js-cookie';
import {credentials} from '../scripts';
import {Api} from '../Api';
import {Notification} from './Notification';
import {NavigationBar} from './NavigationBar';
import {Courses} from './Courses';
import {Container, Jumbotron} from 'reactstrap';
import {RegistrationModal} from './RegistrationModal';
import {AuthenticationModal} from './AuthenticationModal';

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
                <Notification succeed message={'Account retrieving failed due to: ' + response}/>,
                document.getElementById('notifications')
            )
        );
    }

    render() {
        if (this.state.account != null) {
            return (
                <article className="page">
                    <NavigationBar account={this.state.account} onLogout={this.onLogout}/>
                    <Courses account={this.state.account}/>
                </article>
            );
        } else {
            return (
                <article className="page">
                    <Jumbotron>
                        <h1 className="display-3">Flaxo</h1>
                        <p className="lead">
                            An open git-based educational platform for everyone
                        </p>
                        <hr className="my-2"/>
                        <p>
                            Flaxo tests and assess students solutions for you. It can even search for plagiarism.
                            And it is completely free.
                        </p>
                        <p className="lead">
                            <RegistrationModal onLogin={this.onLogin}/>{' '}
                            <AuthenticationModal onLogin={this.onLogin}/>
                        </p>
                    </Jumbotron>
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