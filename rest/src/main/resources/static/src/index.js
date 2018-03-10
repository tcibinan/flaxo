import './styles/style.css';
import React from 'react';
import ReactDOM from 'react-dom';
import {Authentication} from './components/Authentication';

ReactDOM.render(
    <Authentication
        isLoggedIn={false}
        isGithubAuthorized={false}/>,
    document.getElementById('root')
);