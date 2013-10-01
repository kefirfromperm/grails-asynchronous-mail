package grails.plugin.asyncmail

import groovy.transform.ToString
import org.apache.commons.lang.StringUtils
import static grails.plugin.asyncmail.MessageStatus.*

@ToString(includeNames = true, includeFields = true,  includes = 'id,subject,to,status')
class AsynchronousMailMessage implements Serializable {
    /**
     * This date is accepted as max date because different DBMSs store date in
     * different formats. So we can't use date which is maximum in Java.
     * I want to believe that my plugin will work 1000 years. If asynchronous mail plugin
     * works 1000 years then I or somebody else will change this value.
     */
    private static final MAX_DATE
    static {
        Calendar c = Calendar.getInstance()
        c.set(3000, 0, 1, 0, 0, 0)
        c.set(Calendar.MILLISECOND, 0)
        MAX_DATE = c.getTime()
    }

    /** Max length of email address. See the RFC 5321. */
    private static final int MAX_EMAIL_ADDR_SIZE = 256

    /** Id. Need to be declared explicitly for properly @ToString output */
    Long id

    // !!! Message fields !!!
    // Sender attributes
    String from
    String replyTo

    // Receivers attributes
    List<String> to
    List<String> cc
    List<String> bcc

    /** Additional headers */
    Map<String, String> headers

    // Subject and text
    String subject
    String text
    boolean html = false

    /** Attachments */
    List<AsynchronousMailAttachment> attachments

    // !!! Additional status fields !!!
    /** Message status */
    MessageStatus status = CREATED

    /** Date when message was created */
    Date createDate = new Date()

    /** Date when message was sent */
    Date sentDate

    // Send interval
    Date beginDate = new Date()
    Date endDate = MAX_DATE

    /** Priority. The greater is first. */
    int priority = 0

    // Attempts
    int attemptsCount = 0
    int maxAttemptsCount = 1
    Date lastAttemptDate

    /** Minimal interval between attempts in milliseconds */
    long attemptInterval = 300000l

    /** Mark this message for delete after sent */
    boolean markDelete = false

    /** Check can message be aborted */
    boolean isAbortable() {
        return status in [CREATED, ATTEMPTED]
    }

    static transients = ['abortable']

    static hasMany = [to: String, cc: String, bcc: String, attachments: AsynchronousMailAttachment]
    static mapping = {
        table 'async_mail_mess'

        from column: 'from_column'

        attachments fetch: 'join'

        /**
         * Don't touch!
         * "AsynchronousMailMessage.MAX_EMAIL_ADDR_SIZE" is needed for backward compatibility with Grails 2.0.0
         */
        to(
                indexColumn: 'to_idx',
                fetch: 'join',
                joinTable: [
                        name: 'async_mail_to',
                        length: AsynchronousMailMessage.MAX_EMAIL_ADDR_SIZE,
                        key: 'message_id',
                        column: 'to_string'
                ]
        )

        cc(
                indexColumn: 'cc_idx',
                fetch: 'join',
                joinTable: [
                        name: 'async_mail_cc',
                        length: AsynchronousMailMessage.MAX_EMAIL_ADDR_SIZE,
                        key: 'message_id',
                        column: 'cc_string'
                ]
        )

        bcc(
                indexColumn: 'bcc_idx',
                fetch: 'join',
                joinTable: [
                        name: 'async_mail_bcc',
                        length: AsynchronousMailMessage.MAX_EMAIL_ADDR_SIZE,
                        key: 'message_id',
                        column: 'bcc_string'
                ]
        )

        headers(
                indexColumn: [name: 'header_name', length: 255],
                fetch: 'join',
                joinTable: [
                        name: 'async_mail_header',
                        key: 'message_id',
                        column: 'header_value'
                ]
        )

        text type: 'text'
    }

    static constraints = {
        def mailboxValidator = { String value ->
            return value == null || Validator.isMailbox(value)
        }

        // message fields
        from(nullable: true, maxSize: AsynchronousMailMessage.MAX_EMAIL_ADDR_SIZE, validator: mailboxValidator)
        replyTo(nullable: true, maxSize: AsynchronousMailMessage.MAX_EMAIL_ADDR_SIZE, validator: mailboxValidator)

        // The validator for email addresses list
        def emailList = { List<String> list, reference, errors ->
            boolean flag = true
            if (list != null) {
                list.each { String addr ->
                    if (!Validator.isMailbox(addr)) {
                        errors.rejectValue(propertyName, 'asynchronous.mail.mailbox.invalid')
                        flag = false
                    }
                }
            }
            return flag
        }

        def atLeastOneRecipientValidator = { List<String> value, reference, errors ->
            // It's needed to access to propertyName
            emailList.delegate = delegate

            // Validate address list
            if (!emailList(value, reference, errors)) {
                return false
            }

            boolean hasRecipients = reference.to || reference.cc || reference.bcc
            if (!hasRecipients) {
                errors.reject('asynchronous.mail.one.recipient.required')
            }
            return hasRecipients
        }

        // The nullable constraint isn't applied for collections by default.
        to(nullable: true, validator: atLeastOneRecipientValidator)
        cc(nullable: true, validator: emailList)
        bcc(nullable: true, validator: emailList)

        headers(nullable: true, validator: { Map<String, String> map ->
            boolean flag = true
            map?.each { String key, String value ->
                if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
                    flag = false
                }
            }
            return flag
        })

        subject(blank: false, maxSize: 988)
        text(blank: false)

        // Status fields
        status()
        createDate()
        sentDate(nullable: true)
        beginDate()
        endDate(validator: { Date val, AsynchronousMailMessage mess ->
            val && mess.beginDate && val.after(mess.beginDate)
        })

        // Attempt fields
        attemptsCount(min: 0)
        maxAttemptsCount(min: 1)
        lastAttemptDate(nullable: true)
        attemptInterval(min: 0l)
    }

}

enum MessageStatus {
    CREATED, ATTEMPTED, SENT, ERROR, EXPIRED, ABORT
}
