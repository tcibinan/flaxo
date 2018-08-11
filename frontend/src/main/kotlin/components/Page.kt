package components

import react.*
import react.dom.div
import EmptyProps
import Cookies

class PageState(var account: Account? = null): RState

fun RBuilder.rootPage() = child(Page::class) {}

class Page: RComponent<EmptyProps, PageState>(EmptyProps()) {

    companion object {
        private const val USERNAME_COOKIE: String = "username"
        private const val PASSWORD_COOKIE: String = "password"
    }

    init {
//        Api.retrieveAccount(
//                credentials(),
//                account => {
//            this.setState({account: account});
//        },
//        response => ReactDOM.render(
//        <Notification failed message={'Account retrieving failed due to: ' + response}/>,
//        document.getElementById('notifications')
//        )
//        );
    }

    override fun RBuilder.render() {
        val account = state.account
        if (account != null) {
            div {
                +"U'r logged."
//                navigationBar(account, ::onLogout)
//                courses(account)
            }
//            return (
//            <article className="page">
//            <NavigationBar account={this.state.account} onLogout={this.onLogout}/>
//            <Courses account={this.state.account}/>
//            </article>
//            );
        } else {
            div("page") {
                welcomeDesk(::onLogin)
            }
        }
    }

    private fun onLogin(username: String, password: String, retrievedAccount: Account) {
        Cookies.set(USERNAME_COOKIE, username)
        Cookies.set(PASSWORD_COOKIE, password)

        setState {
            account = retrievedAccount
        }
    }

    private fun onLogout() {
        Cookies.remove(USERNAME_COOKIE)
        Cookies.remove(PASSWORD_COOKIE)

        setState {
            account = null
        }
    }
}