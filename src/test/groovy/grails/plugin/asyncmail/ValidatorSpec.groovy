package grails.plugin.asyncmail
import spock.lang.Specification

import static grails.plugin.asyncmail.Validator.isMailbox
/**
 * @author Puneet Behl
 */
class ValidatorSpec extends Specification {

    void "test@example.com is valid mailbox"() {
        expect:
        isMailbox "test@example.com"
    }

    void "John Smith <test@example.com> is valid mailbox"() {
        expect:
        isMailbox "John Smith <test@example.com>"
    }

    void "abc is not valid mailbox"() {
        expect:
        !isMailbox("abc")
    }

    void "empty string is not valid mailbox"() {
        expect:
        !isMailbox("")
    }

    void "blank string is not a valid mailbox"() {
        expect:
        !isMailbox(" ")
    }
}