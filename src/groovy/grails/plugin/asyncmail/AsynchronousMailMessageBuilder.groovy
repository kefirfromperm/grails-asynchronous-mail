package grails.plugin.asyncmail

import grails.plugin.mail.GrailsMailException
import grails.plugin.mail.MailMessageContentRender
import grails.plugin.mail.MailMessageContentRenderer
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.InputStreamSource
import org.springframework.mail.MailMessage
import org.springframework.mail.MailSender
import org.springframework.util.Assert

import javax.activation.FileTypeMap

/**
 * Build new synchronous message
 */
class AsynchronousMailMessageBuilder {
    private AsynchronousMailMessage message
    private boolean immediately = false
    private boolean immediatelySetted = false

    private Locale locale

    final boolean mimeCapable
    final MailMessageContentRenderer mailMessageContentRenderer

    private FileTypeMap fileTypeMap

    final String defaultFrom
    final String defaultTo
    final String overrideAddress

    AsynchronousMailMessageBuilder(
            boolean mimeCapable,
            ConfigObject config,
            FileTypeMap fileTypeMap,
            MailMessageContentRenderer mailMessageContentRenderer = null
    ) {
        this.mimeCapable = mimeCapable;
        this.overrideAddress = config.overrideAddress ?: null
        this.defaultFrom = overrideAddress ?: (config.default.from ?: null)
        this.defaultTo = overrideAddress ?: (config.default.to ?: null)
        this.fileTypeMap = fileTypeMap;
        this.mailMessageContentRenderer = mailMessageContentRenderer;
    }

    void init(config) {
        message = new AsynchronousMailMessage()
        message.attemptInterval = config?.asynchronous?.mail?.default?.attempt?.interval ?: 300000l
        message.maxAttemptsCount = config?.asynchronous?.mail?.default?.max?.attempts?.count ?: 1
        message.markDelete = config?.asynchronous?.mail?.clear?.after?.sent ?: false
    }

    // Specified fields for asynchronous message
    void beginDate(Date begin) {
        Assert.notNull(begin, "Begin date can't be null.")

        message.beginDate = begin
    }

    void endDate(Date end) {
        Assert.notNull(end, "End date can't be null.")

        message.endDate = end
    }

    // Priority
    void priority(int priority) {
        message.priority = priority
    }

    // Attempts
    void maxAttemptsCount(int max) {
        message.maxAttemptsCount = max
    }

    void attemptInterval(long interval) {
        message.attemptInterval = interval
    }

    // Mark that the message must be sent immediately
    void immediate(boolean value) {
        immediately = value
        immediatelySetted = true
    }

    // Mark message must be deleted after sent
    void delete(boolean value) {
        message.markDelete = value
    }

    // Multipart field do nothing
    void multipart(boolean multipart) {
        // nothing
        // Added analogous to mail plugin
    }

    void multipart(int multipartMode) {
        // nothing
        // Added analogous to mail plugin
    }

    // Mail message headers
    void headers(Map headers) {
        Assert.notEmpty(headers, "Headers can't be null.")

        if(!mimeCapable){
            throw new GrailsMailException("You must use a JavaMailSender to customise the headers.")
        }

        Map map = new HashMap()

        headers.each{key, value->
            String keyString = key?.toString()
            String valueString = value?.toString()

            Assert.hasText(keyString, "Header name can't be null or empty.")
            Assert.hasText(valueString, "Value of header ${keyString} can't be null or empty.")

            map.put(keyString, valueString)
        }

        message.headers = map
    }

    // Field "to"
    void to(CharSequence recipient) {
        Assert.notNull(recipient, "Field to can't be null.")
        to([recipient])
    }

    void to(Object[] recipients) {
        Assert.notNull(recipients, "Field to can't be null.")
        to(recipients*.toString())
    }

    void to(List<? extends CharSequence> recipients) {
        message.to = validateAndConvertAddrList('to', recipients)
    }

    private List<String> validateAndConvertAddrList(String fieldName, List<? extends CharSequence> recipients) {
        Assert.notNull(recipients, "Field $fieldName can't be null.")
        Assert.notEmpty(recipients, "Field $fieldName can't be empty.")

        List<String> list = new ArrayList<String>(recipients.size())
        recipients.each {CharSequence seq ->
            String addr = seq.toString()
            assertEmail(addr, fieldName)
            list.add(addr)
        }
        return list
    }

    private assertEmail(String addr, String fieldName) {
        Assert.notNull(addr, "Value of $fieldName can't be null.")
        Assert.hasText(addr, "Value of $fieldName can't be blank.")
        if (!Validator.isMailbox(addr)) {
            throw new GrailsMailException("Value of $fieldName must be email address.")
        }
    }

    // Field "bcc"
    void bcc(CharSequence val) {
        Assert.notNull(val, "Field bcc can't be null.")
        bcc([val])
    }

    void bcc(Object[] recipients) {
        Assert.notNull(recipients, "Field bcc can't be null.")
        bcc(recipients*.toString())
    }

    void bcc(List<? extends CharSequence> recipients) {
        message.bcc = validateAndConvertAddrList('bcc', recipients)
    }

    // Field "cc"
    void cc(CharSequence val) {
        Assert.notNull(val, "Field cc can't be null.")
        cc([val])
    }

    void cc(Object[] recipients) {
        Assert.notNull(recipients, "Field cc can't be null.")
        cc(recipients*.toString())
    }

    void cc(List<? extends CharSequence> recipients) {
        message.cc = validateAndConvertAddrList('cc', recipients)
    }

    // Field "replyTo"
    void replyTo(CharSequence val) {
        def addr = val?.toString()
        assertEmail(addr, 'replyTo')
        message.replyTo = addr
    }

    // Field "from"
    void from(CharSequence sender) {
        def addr = sender?.toString()
        assertEmail(addr, 'from')
        message.from = addr
    }

    // Field "subject"
    void title(CharSequence subject1) {
        subject(subject1)
    }

    void subject(CharSequence subject) {
        String string = subject?.toString()
        Assert.hasText(string, "Field subject can't be null or blank.")
        message.subject = string
    }

    // Body
    void body(CharSequence seq) {
        text(seq)
    }

    void body(Map params) {
        Assert.notEmpty(params, "body cannot be null or empty")

        def render = doRender(params)

        if (render.html) {
            html(render.out.toString())
        } else {
            text(render.out.toString())
        }
    }

    void text(CharSequence seq) {
        message.html = false
        def string = seq?.toString()
        Assert.hasText(string, "Body text can't be null or blank.")
        message.text = string
    }

    void text(Map params) {
        text(doRender(params).out.toString())
    }

    void html(CharSequence seq) {
        message.html = true
        def string = seq?.toString()
        Assert.hasText(string, "Body can't be null or blank.")
        message.text = string
    }

    void html(Map params) {
        html(doRender(params).out.toString())
    }

    protected MailMessageContentRender doRender(Map params) {
        if (mailMessageContentRenderer == null) {
            throw new GrailsMailException(
                    "mail message builder was constructed without a message content render so cannot render views"
            )
        }

        if (!params.view) {
            throw new GrailsMailException("no view specified")
        }

        return mailMessageContentRenderer.render(new StringWriter(), params.view, params.model, locale, params.plugin)
    }

    void locale(String localeStr) {
        Assert.hasText(localeStr, "locale cannot be null or empty")

        locale(new Locale(localeStr.split('_', 3).toArrayString()))
    }

    void locale(Locale locale) {
        Assert.notNull(locale, "locale cannot be null")

        this.locale = locale
    }

    // Attachments
    void attachBytes(String name, String mimeType, byte[] content) {
        Assert.hasText(name, "Attachment name can't be blank.")
        Assert.notNull(content, "Attachment content can't be null.")

        if(!mimeCapable){
            throw new GrailsMailException("You must use a JavaMailSender to add attachment.")
        }

        message.addToAttachments(
                new AsynchronousMailAttachment(
                        attachmentName: name, mimeType: mimeType, content: content
                )
        )
    }

    void attach(String fileName, String contentType, byte[] bytes) {
        attachBytes(fileName, contentType, bytes)
    }

    void attach(File file) {
        attach(file.name, file)
    }

    void attach(String fileName, File file) {
        attach(fileName, fileTypeMap.getContentType(file), file)
    }

    void attach(String fileName, String contentType, File file) {
        if (!file.exists()) {
            throw new FileNotFoundException("cannot use $file as an attachment as it does not exist")
        }

        attach(fileName, contentType, new FileSystemResource(file))
    }

    void attach(String fileName, String contentType, InputStreamSource source) {
        InputStream stream = source.inputStream
        try {
            attachBytes(fileName, contentType, stream.bytes)
        } finally {
            stream.close()
        }
    }

    void inline(String name, String mimeType, byte[] content) {
        Assert.hasText(name, "Inline id can't be blank.")
        Assert.notNull(content, "Inline content can't be null.")

        if(!mimeCapable){
            throw new GrailsMailException("You must use a JavaMailSender to add inlines.")
        }

        message.addToAttachments(
                new AsynchronousMailAttachment(
                        attachmentName: name, mimeType: mimeType, content: content, inline: true
                )
        )
    }

    void inline(File file) {
        inline(file.name, file)
    }

    void inline(String fileName, File file) {
        inline(fileName, fileTypeMap.getContentType(file), file)
    }

    void inline(String contentId, String contentType, File file) {
        if (!file.exists()) {
            throw new FileNotFoundException("cannot use $file as an attachment as it does not exist")
        }

        inline(contentId, contentType, new FileSystemResource(file))
    }

    void inline(String contentId, String contentType, InputStreamSource source) {
        InputStream stream = source.inputStream
        try {
            inline(contentId, contentType, stream.bytes)
        } finally {
            stream.close()
        }
    }

    MailMessage finishMessage() {
        throw new UnsupportedOperationException(
                "You use Grails Asynchronous Mail plug-in which doesn't support some methods."
        );
    }

    MailMessage sendMessage() {
        throw new UnsupportedOperationException(
                "You use Grails Asynchronous Mail plug-in which doesn't support some methods."
        );
    }

    MailSender getMailSender(){
        throw new UnsupportedOperationException(
                "You use Grails Asynchronous Mail plug-in which doesn't support some methods."
        );
    }

    AsynchronousMailMessage getMessage() {
        return message
    }

    boolean getImmediately() {
        return immediately
    }

    boolean getImmediatelySetted() {
        return immediatelySetted
    }
}
