import './styles/style.css';
import React from 'react';
import ReactDOM from 'react-dom';
import {AuthorizationPanel, RegistrationForm} from './components.js';

ReactDOM.render(<RegistrationForm/>, document.getElementById('root'));
ReactDOM.render(<AuthorizationPanel/>, document.getElementById('root'));