package ru.perm.kefir.asynchronousmail

class AsynchronousMailMessage implements Serializable {
    private static final MAX_DATE;
    static {
        Calendar c = Calendar.getInstance();
        c.set(3000, 0, 1, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        MAX_DATE = c.getTime();
    }

    // !!! Message fields !!!
    List<String> to;
    String subject;
    Map<String, String> headers;
    String text;
    boolean html = false;
    List<String> bcc;
    List<String> cc;
    String replyTo;
    String from;

    // Attachments
    List<AsynchronousMailAttachment> attachments;

    // !!! Additional status fields !!!
    /** Message status */
    MessageStatus status = MessageStatus.CREATED;

    /** Date when message is created */
    Date createDate = new Date();

    /** Date when message sent */
    Date sentDate;

    /** Send interval */
    Date beginDate = new Date();

    /** Send interval */
    Date endDate = MAX_DATE;

    // Attempts
    int attemptsCount = 0;
    int maxAttemptsCount = 1;
    Date lastAttemptDate;

    // Minimal interval between attempts in milliseconds
    long attemptInterval = 300000l;

    // Mark this message for delete after sent
    boolean markDelete = false;

    static hasMany = [to: String, bcc: String, cc: String, attachments: AsynchronousMailAttachment];

    static mapping = {
        text type: 'text'
        from column: 'from_column'
    }

    static constraints = {
        // message fields
        to(nullable: false, validator: {List<String> val -> !val.isEmpty();})
        subject(nullable: false, blank: false);
        headers(nullable: true);
        text(nullable: false, blank: false);
        bcc(nullable: true);
        cc(nullable: true);
        replyTo(nullable: true);
        from(nullable: true);

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
        attemptsCount(min: 0);
        maxAttemptsCount(min: 0);
        lastAttemptDate(nullable: true);
        attemptInterval(min: 0l);
    }

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