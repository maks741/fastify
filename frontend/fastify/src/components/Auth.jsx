import React, { useState } from 'react';
import classes from './Auth.module.css';
import api, {setCurrentUser} from "../service/HttpClient.js";
import {API_ENDPOINTS} from "../constants/api.constants.js";
import {useNavigate} from "react-router-dom";

function Auth() {
    const [isLoginMode, setIsLoginMode] = useState(true);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: ''
    });
    const navigate = useNavigate();

    const handleInputChange = (e) => {
        setFormData(prev => ({
            ...prev,
            [e.target.name]: e.target.value
        }));
    };

    const isFormValid = () => {
        const { email, password, username } = formData;
        const emailValid = /\S+@\S+\.\S+/.test(email);
        const passwordValid = password.length >= 6;
        const usernameValid = isLoginMode || username.trim() !== '';
        return emailValid && passwordValid && usernameValid;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!isFormValid()) return;

        setIsLoading(true);
        setError(null);

        try {
            if (isLoginMode) {
                api.post(API_ENDPOINTS.LOGIN, {
                    email: formData.email,
                    password: formData.password
                }).then(function (response) {
                    const user = response.data;
                    setCurrentUser(user);
                    setIsLoading(false);
                    navigate('/');
                })
            } else {
                api.post(API_ENDPOINTS.LOGIN, {
                    username: formData.username,
                    email: formData.email,
                    password: formData.password
                }).then(function (response) {
                    const user = response.data;
                    setCurrentUser(user);
                    setIsLoading(false);
                    navigate('/');
                })
            }
        } catch (err) {
            setError('Authentication failed. Please try again.');
        } finally {
            setIsLoading(false);
        }
    };

    const switchModeHandler = () => {
        setIsLoginMode(prev => !prev);
        setError(null);
    };

    return (
        <div className={classes.auth_container}>
            <div className={classes.auth_card}>
                <div className={classes.logo}>
                    <img src="/assets/images/spotify-logo.png" width="50" height="50" alt="spotify-logo" />
                </div>
                <form onSubmit={handleSubmit}>
                    {!isLoginMode && (
                        <div className={classes.form_group}>
                            <label htmlFor="username">Username</label>
                            <input
                                type="text"
                                id="username"
                                name="username"
                                className={classes.form_control}
                                placeholder="Enter username"
                                value={formData.username}
                                onChange={handleInputChange}
                                required
                            />
                        </div>
                    )}
                    <div className={classes.form_group}>
                        <label htmlFor="email">Email</label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            className={classes.form_control}
                            placeholder="Enter email"
                            value={formData.email}
                            onChange={handleInputChange}
                            required
                        />
                    </div>
                    <div className={classes.form_group}>
                        <label htmlFor="password">Password</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            className={classes.form_control}
                            placeholder="Enter password"
                            value={formData.password}
                            onChange={handleInputChange}
                            required
                            minLength={6}
                        />
                    </div>
                    {error && <div className={classes.error_message}>{error}</div>}
                    <div className={classes.button_group}>
                        <button
                            type="submit"
                            className={classes.btn}
                            disabled={!isFormValid() || isLoading}
                        >
                            {isLoading ? <span className={classes.loading_spinner}></span> :
                                <span>{isLoginMode ? 'Login' : 'Sign Up'}</span>}
                        </button>
                    </div>
                    <div className={classes.switch_mode}>
                        <p>
                            {isLoginMode ? 'Need an account?' : 'Already have an account?'}
                            <a href="#!" onClick={switchModeHandler}>
                                {isLoginMode ? ' Sign Up' : ' Login'}
                            </a>
                        </p>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default Auth;