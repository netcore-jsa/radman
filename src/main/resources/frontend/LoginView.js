import {html, PolymerElement} from '@polymer/polymer/polymer-element.js';
import '@polymer/iron-form/iron-form.js';
import '@vaadin/vaadin-button/vaadin-button.js';
import '@vaadin/vaadin-text-field/vaadin-text-field.js';
import '@vaadin/vaadin-text-field/vaadin-password-field.js';
import '@vaadin/vaadin-ordered-layout/vaadin-vertical-layout.js';

class LoginView extends PolymerElement {

    static get template(){
        return html`
            <style>
                .container {
                    width: 100%;
                    height: 100%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }
                iron-form {
                    max-width: 400px;
                }
                vaadin-password-field,
                vaadin-text-field {
                    width: 100%;
                }
                #authenticationMessage {
                    padding: 0 20px;
                    height: 100%;
                    max-height: 0;
                    color: #ffffff;
                    font-weight: bold;
                    border-radius: 2px;
                    background-color: #ff5353;
                    -moz-transition: all 0.25s ease-in;
                    -webkit-transition: all 0.25s ease-in;
                    -o-transition: all 0.25s ease-in;
                    transition: all 0.25s ease-in;
                    overflow: hidden;
                }
                #authenticationMessage.visible {
                    max-height: 100px;
                    padding: 10px 20px;
                    margin-bottom: 10px;
                }
            </style>
            <div class="container">
                <iron-form id="form" allow-redirect>
                    <form method="post" action="login">
                        <vaadin-vertical-layout>
                            <h1>RadMan</h1>
                            <vaadin-label id="authenticationMessage">Incorrect username or password</vaadin-label>
                            <vaadin-text-field id="username" name="username" on-value-changed="onValueChange"
                                   on-keypress="onKeyPressed" autofocus></vaadin-text-field>
                            <vaadin-password-field id="password" name="password" on-value-changed="onValueChange"
                                       on-keypress="onKeyPressed"></vaadin-password-field>
                            <vaadin-button on-click="login" theme="primary">Login</vaadin-button>
                        </vaadin-vertical-layout>
                    </form>
                </iron-form>
            </div>`;
    }

    static get is() {
        return 'login-view';
    }

    onKeyPressed(event) {
        if (event.keyCode === 13) {
            event.preventDefault();
            this.login();
        }
    }

    login() {
        if (!this.$.username.invalid && !this.$.password.invalid) {
            this.$.form.submit();
        }
    }

    onValueChange(event) {
        this.$.authenticationMessage.classList.remove("visible");
    }
}

customElements.define(LoginView.is, LoginView);