package grails.plugin.asyncmail

import grails.plugin.mail.GrailsMailException
import grails.plugin.mail.MailMessageContentRender
import grails.plugin.mail.MailMessageContentRenderer
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.InputStreamSource
import org.springframework.util.Assert
import javax.activation.FileTypeMap
import org.apache.commons.validator.EmailValidator

/**
 * Build new synchronous message
 */
class AsynchronousMailMessageBuilder {
    AsynchronousMailMessage message;
    boolean immediately = false;
    boolean immediatelySetted = false;

    private Locale locale;

    MailMessageContentRenderer mailMessageContentRenderer;
    FileTypeMap fileTypeMap;

    private boolean mimeCapable = false;

    def AsynchronousMailMessageBuilder() {
    }

    def init(def config) {
        message = new AsynchronousMailMessage();
        message.attemptInterval = config?.asynchronous?.mail?.default?.attempt?.interval ?: 300000l;
        message.maxAttemptsCount = config?.asynchronous?.mail?.default?.max?.attempts?.count ?: 1;
        message.markDelete = config?.asynchronous?.mail?.clear?.after?.sent ?: false;
    }

    boolean isMimeCapable() {
        return mimeCapable;
    }

    void setMimeCapable(boolean mimeCapable) {
        this.mimeCapable = mimeCapable
    }

    // Specified fields for asynchronous message
    void beginDate(Date begin) {
        Assert.notNull(begin, "Begin date can't be null.");
        
        message.beginDate = begin;
    }

    void endDate(Date end) {
        Assert.notNull(end, "End date can't be null.");
        
        message.endDate = end;
    }

    // Priority
    void priority(int priority) {
        message.priority = priority;
    }

    // Attempts
    void maxAttemptsCount(int max) {
        message.maxAttemptsCount = max;
    }

    void attemptInterval(long interval) {
        message.attemptInterval = interval;
    }

    // Mark that the message must be sent immediately
    void immediate(boolean value) {
        immediately = value;
        immediatelySetted = true;
    }

    // Mark message must be deleted after sent
    void delete(boolean value) {
        message.markDelete = value;
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
        Assert.notEmpty(headers, "Headers can't be null.");

        if(!mimeCapable){
            throw new GrailsMailException("You must use a JavaMailSender to customise the headers.")
        }
        
        Map map = new HashMap();
        
        headers.each{key, value->
            String keyString = key?.toString();
            String valueString = value?.toString();
            
            Assert.hasText(keyString, "Header name can't be null or empty.")
            Assert.hasText(valueString, "Value of header ${keyString} can't be null or empty.")
            
            map.put(keyString, valueString);
        }
        
        message.headers = map;
    }

    // Field "to"
    void to(CharSequence recipient) {
        Assert.notNull(recipient, "Field to can't be null.");
        to([recipient]);
    }

    void to(CharSequence[] recipients) {
        Assert.notNull(recipients, "Field to can't be null.");
        to(Arrays.asList(recipients));
    }

    void to(List<CharSequence> recipients) {
        message.to = validateAndConvertAddrList('to', recipients);
    }

    private List<String> validateAndConvertAddrList(String fieldName, List<CharSequence> recipients) {
        Assert.notNull(recipients, "Field $fieldName can't be null.");
        Assert.notEmpty(recipients, "Field $fieldName can't be empty.");

        List<String> list = new ArrayList<String>(recipients.size());
        recipients.each {CharSequence seq ->
            String addr = seq.toString();
            assertEmail(addr, fieldName);
            list.add(addr);
        }
        return list;
    }

    private assertEmail(String addr, String fieldName) {
        Assert.notNull(addr, "Value of $fieldName can't be null.");
        Assert.hasText(addr, "Value of $fieldName can't be blank.");
        if (!EmailValidator.getInstance().isValid(addr)) {
            throw new GrailsMailException("Value of $fieldName must be email address.");
        }
    }

    // Field "bcc"
    void bcc(CharSequence val) {
        Assert.notNull(val, "Field bcc can't be null.");
        bcc([val]);
    }

    void bcc(CharSequence[] recipients) {
        Assert.notNull(recipients, "Field bcc can't be null.");
        bcc(Arrays.asList(recipients));
    }

    void bcc(List<CharSequence> recipients) {
        message.bcc = validateAndConvertAddrList('bcc', recipients);
    }

    // Field "cc"
    void cc(CharSequence val) {
        Assert.notNull(val, "Field cc can't be null.");
        cc([val]);
    }

    void cc(CharSequence[] recipients) {
        Assert.notNull(recipients, "Field cc can't be null.");
        cc(Arrays.asList(recipients));
    }

    void cc(List<CharSequence> recipients) {
        message.cc = validateAndConvertAddrList('cc', recipients);
    }

    // Field "replyTo"
    void replyTo(CharSequence val) {
        def addr = val.toString();
        assertEmail(addr, 'replyTo');
        message.replyTo = addr;
    }

    // Field "from"
    void from(CharSequence sender) {
        def addr = sender.toString()
        assertEmail(addr, 'from');
        message.from = addr;
    }

    // Field "subject"
    void title(CharSequence subject1) {
        subject(subject1);
    }

    void subject(CharSequence subject) {
        String string = subject?.toString()
        Assert.hasText(string, "Field subject can't be null or blank.")
        message.subject = string;
    }

    // Body
    void body(CharSequence seq) {
        text(seq);
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
        message.html = false;
        message.text = seq.toString();
    }

    void text(Map params) {
        text(doRender(params).out.toString());
    }

    void html(CharSequence seq) {
        message.html = true;
        message.text = seq.toString();
    }

    void html(Map params) {
        html(doRender(params).out.toString());
    }

    protected MailMessageContentRender doRender(Map params) {
        if (mailMessageContentRenderer == null) {
            throw new GrailsMailException(
                    "mail message builder was constructed without a message content render so cannot render views"
            );
        }

        if (!params.view) {
            throw new GrailsMailException("no view specified");
        }

        return mailMessageContentRenderer.render(new StringWriter(), params.view, params.model, locale, params.plugin);
    }

    void locale(String localeStr) {
        Assert.hasText(localeStr, "locale cannot be null or empty")

        locale(new Locale(* localeStr.split('_', 3)));
    }

    void locale(Locale locale) {
        Assert.notNull(locale, "locale cannot be null")

        this.locale = locale;
    }

    // Attachments
    void attachBytes(String name, String mimeType, byte[] content) {
        message.addToAttachments(
                new AsynchronousMailAttachment(
                        attachmentName: name, mimeType: mimeType, content: content
                )
        );
    }

    void attach(String fileName, String contentType, byte[] bytes) {
        attachBytes(fileName, contentType, bytes);
    }

    void attach(File file) {
        attach(file.name, file);
    }

    void attach(String fileName, File file) {
        attach(fileName, fileTypeMap.getContentType(file), file);
    }

    void attach(String fileName, String contentType, File file) {
        if (!file.exists()) {
            throw new FileNotFoundException("cannot use $file as an attachment as it does not exist")
        }

        attach(fileName, contentType, new FileSystemResource(file));
    }

    void attach(String fileName, String contentType, InputStreamSource source) {
        InputStream stream = source.inputStream;
        try {
            attachBytes(fileName, contentType, stream.bytes);
        } finally {
            stream.close();
        }
    }
    
    void inline(String name, String mimeType, byte[] content) {
        message.addToAttachments(
                new AsynchronousMailAttachment(
                        attachmentName: name, mimeType: mimeType, content: content, inline: true
                )
        );
    }

    void inline(File file) {
        inline(file.name, file);
    }
    
    void inline(String fileName, File file) {
        inline(fileName, fileTypeMap.getContentType(file), file);
    }

    void inline(String contentId, String contentType, File file) {
        if (!file.exists()) {
            throw new FileNotFoundException("cannot use $file as an attachment as it does not exist")
        }
        
        inline(contentId, contentType, new FileSystemResource(file))
    }
    
    void inline(String contentId, String contentType, InputStreamSource source) {
        InputStream stream = source.inputStream;
        try {
            inline(contentId, contentType, stream.bytes);
        } finally {
            stream.close();
        }
    }
}
