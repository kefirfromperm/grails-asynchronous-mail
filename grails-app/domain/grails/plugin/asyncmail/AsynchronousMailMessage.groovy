package grails.plugin.asyncmail

class AsynchronousMailMessage implements Serializable {
    private static final MAX_DATE;
    static {
        Calendar c = Calendar.getInstance();
        c.set(3000, 0, 1, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        MAX_DATE = c.getTime();
    }

    // !!! Message fields !!!
    // Sender attributes
    String from;
    String replyTo;

    // Receivers attributes
    List<String> to;
    List<String> cc;
    List<String> bcc;

    // Additional headers
    Map<String, String> headers;

    // Subject and text
    String subject;
    String text;
    boolean html = false;

    // Attachments
    List<AsynchronousMailAttachment> attachments;
    static hasMany = [to: String, cc: String, bcc: String, attachments: AsynchronousMailAttachment];

    // !!! Additional status fields !!!
    // Message status
    MessageStatus status = MessageStatus.CREATED;

    //Date when message was created
    Date createDate = new Date();

    // Date when message was sent
    Date sentDate;

    //Send interval
    Date beginDate = new Date();
    Date endDate = MAX_DATE;

    // Priority. The greater is first.
    int priority = 0;
    
    // Attempts
    int attemptsCount = 0;
    int maxAttemptsCount = 1;
    Date lastAttemptDate;

    // Minimal interval between attempts in milliseconds
    long attemptInterval = 300000l;

    // Mark this message for delete after sent
    boolean markDelete = false;

    static mapping = {
        table 'async_mail_mess';

        from column: 'from_column';

        to(
                indexColumn: 'to_idx',
                joinTable: [
                        name: 'async_mail_mess_to',
                        length: 320,
                        key: 'message_id',
                        column: 'to_string'
                ]
        );

        cc(
                indexColumn: 'cc_idx',
                joinTable: [
                        name: 'async_mail_mess_cc',
                        length: 320,
                        key: 'message_id',
                        column: 'cc_string'
                ]
        );

        bcc(
                indexColumn: 'bcc_idx',
                joinTable: [
                        name: 'async_mail_mess_bcc',
                        length: 320,
                        key: 'message_id',
                        column: 'bcc_string'
                ]
        );

        headers(
                indexColumn: [name: 'header_name', length: 255],
                joinTable: [
                        name: 'async_mail_mess_header',
                        key: 'message_id',
                        column: 'header_value'
                ]
        );

        text type: 'text';
    }

    static constraints = {
        // message fields
        from(nullable: true, maxSize: 320);
        replyTo(nullable: true, maxSize: 320);

        to(nullable: false, validator: {List<String> val -> !val.isEmpty();})
        cc(nullable: true);
        bcc(nullable: true);

        headers(nullable: true);

        subject(nullable: false, blank: false, maxSize: 988);
        text(nullable: false, blank: false);

        // Status fields
        status(nullable: false);
        createDate(nullable: false);
        sentDate(nullable: true);
        beginDate(nullable: false);
        endDate(
                nullable: false,
                validator: {Date val, AsynchronousMailMessage mess ->
                    val && mess.beginDate && val.after(mess.beginDate);
                }
        );
        
        // Attempt fields
        attemptsCount(min: 0);
        maxAttemptsCount(min: 1);
        lastAttemptDate(nullable: true);
        attemptInterval(min: 0l);
    }

    @Override
    def String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Asynchronous mail message: ");
        builder.append("subject: ").append(subject);
        builder.append("; to: ");
        to.each {String addr ->
            builder.append(addr);
            builder.append(',');
        }
        builder.append("status: ").append(status);
        return builder.toString();
    }
}

enum MessageStatus {
    CREATED, ATTEMPTED, SENT, ERROR, EXPIRED, ABORT;
}