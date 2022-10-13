package ricardo.ch.login.ui.login

import android.util.Patterns
import java.util.regex.Pattern

/**
 * As it is not possible to mock static fields, and [Patterns] belongs to the
 * android framework, it would be complicated to use [Patterns.EMAIL_ADDRESS] in tests.
 * So we use an intermediary class that is mock-able instead.
 */
class MailValidator {
    fun emailPattern(): Pattern {
        return Patterns.EMAIL_ADDRESS
    }
}
