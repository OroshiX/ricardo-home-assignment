package ricardo.ch.login

import android.app.Activity
import android.view.KeyEvent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.core.IsNot.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ricardo.ch.login.ui.login.LoginActivity
import ricardo.ch.login.util.hasNoErrorText


/**
 * Tests the UI of the Login
 *
 * Don't forget to disable all autofill methods in your system phone before
 *
 * Google Autofill, any password manager, etc.
 * @see [https://stackoverflow.com/a/48313811/2589983]
 *
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginUITest {

    // GIVEN - our login screen
    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun loginWrongEmail_DisplaysError() {
        // WHEN - we type a wrong email
        onView(withId(R.id.username)).perform(typeText("myemail@"), closeSoftKeyboard())

        // THEN - the login button should not be enabled, and an error should be displayed for username
        onView(withId(R.id.login)).check(matches(not(isEnabled())))
        onView(withId(R.id.username)).check(matches(hasErrorText("Not a valid username")))
    }

    @Test
    fun loginWrongEmailPlusPassword_DisplaysError() {
        // WHEN - we type a wrong email and a correct password
        onView(withId(R.id.username)).perform(typeText("myemail@"))
        onView(withId(R.id.password)).perform(typeText("123456"))

        // THEN - the login button should not be enabled, and an error should be displayed for username
        onView(withId(R.id.login)).check(matches(not(isEnabled())))
        onView(withId(R.id.username)).check(matches(hasErrorText("Not a valid username")))
    }

    // This test should pass (display error when having incorrect password), but there is a bug in
    // the [LoginActivity] code that makes it so that the state of the form is updated only when the
    // state of the form (error or not) changes, but the edit text error is reset each time the text
    // in the password field changes, so the error disappears (when it shouldn't)
    @Test
    fun loginPasswordLength_DisplaysError() {
        // WHEN - we type username and a short password
        onView(withId(R.id.username)).perform(typeText("some username"))
        onView(withId(R.id.password)).perform(typeText("1234"), closeSoftKeyboard())

        // THEN - the login button should not be enabled, and an error should be displayed for password
        onView(withId(R.id.login)).check(matches(not(isEnabled())))
        onView(withId(R.id.password)).check(matches(hasErrorText("Password must be >5 characters")))
    }

    // Same test as above, but with `replaceText` instead of `typeText` (to really show the bug)
    @Test
    fun loginPasswordLengthSelectAll_DisplaysError() {
        // WHEN - we type username and a short password
        onView(withId(R.id.username)).perform(typeText("some username"))
        onView(withId(R.id.password)).perform(replaceText("1234"), closeSoftKeyboard())

        // THEN - the login button should not be enabled, and an error should be displayed for password
        onView(withId(R.id.login)).check(matches(not(isEnabled())))
        onView(withId(R.id.password)).check(matches(hasErrorText("Password must be >5 characters")))
    }

    @Test
    fun loginCorrect_LoginButtonEnabled() {
        // WHEN - we type correct credentials
        onView(withId(R.id.username)).perform(typeText("myemail@toto.fr"))
        onView(withId(R.id.password)).perform(typeText("123456"))

        // THEN - the login button should be enabled, and no errors should be displayed
        onView(withId(R.id.login)).check(matches(isEnabled()))
        onView(withId(R.id.username)).check(matches(hasNoErrorText()))
        onView(withId(R.id.password)).check(matches(hasNoErrorText()))
    }

    @Test
    fun loginWrongUsernameThenCorrect_NoErrorOnUsername() {
        // WHEN - we type correct credentials after having an error
        onView(withId(R.id.username)).perform(typeText("m@t."))
            .perform(
                pressKey(KeyEvent.KEYCODE_DEL),
                pressKey(KeyEvent.KEYCODE_DEL),
                pressKey(KeyEvent.KEYCODE_DEL),
            )
            .perform(closeSoftKeyboard())

        // THEN - the username field should not have any error
        onView(withId(R.id.username)).check(matches(hasNoErrorText()))
    }

    @Test
    fun login_FinishesActivity() {
        // WHEN - we have correct credentials and we click on login
        onView(withId(R.id.username)).perform(replaceText("ma"))
        onView(withId(R.id.password)).perform(replaceText("123456"))

        onView(withId(R.id.login)).perform(click())

        // THEN - Our login activity finished correctly with RESULT_OK
        MatcherAssert.assertThat(
            activityRule.scenario.result.resultCode,
            CoreMatchers.`is`(Activity.RESULT_OK)
        )
    }

    // Bug: We should not be able to bypass the login button disabled when
    // clicking on the "DONE" ime action button on the keyboard => test doesn't pass
    @Test
    fun loginWrongCredentialsWithImeAction_DoesNotSendLogin() {
        // WHEN - we fill bad credentials and click on "Done" ime Action
        onView(withId(R.id.username)).perform(replaceText("ma"))
        onView(withId(R.id.password)).perform(replaceText("12345"))
            .perform(pressImeActionButton())

        // THEN - Our login activity should not be finished
        MatcherAssert.assertThat(
            activityRule.scenario.result.resultCode,
            Matchers.`is`(Activity.RESULT_CANCELED)
        )
    }
}