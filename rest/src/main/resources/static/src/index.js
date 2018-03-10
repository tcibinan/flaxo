import './styles/style.css';
import React from 'react';
import ReactDOM from 'react-dom';
import {AuthorizationPanel} from './components.js';

ReactDOM.render(
    <AuthorizationPanel
        isLoggedIn={false}
        isGithubAuthorized={false}/>,
    document.getElementById('root')
);