package grails.plugin.asyncmail

import grails.plugin.mail.GrailsMailException
import grails.plugin.mail.MailMessageContentRender
import grails.plugin.mail.MailMessageContentRenderer
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.InputStreamSource
import org.springframework.util.Assert
import javax.activation.FileTypeMap

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

    def AsynchronousMailMessageBuilder() {
    }

    def init(def config) {
        message = new AsynchronousMailMessage();
        message.attemptInterval = config?.asynchronous?.mail?.default?.attempt?.interval ?: 300000l;
        message.maxAttemptsCount = config?.asynchronous?.mail?.default?.max?.attempts?.count ?: 1;
        message.markDelete = config?.asynchronous?.mail?.clear?.after?.sent ?: false;
    }

    // Specified fields for asynchronous message
    void beginDate(Date begin) {
        message.beginDate = begin;
    }

    void endDate(Date end) {
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
        message.headers = headers;
    }

    // Field "to"
    void to(String recipient) {
        message.to = [recipient];
    }

    void to(String[] recipients) {
        message.to = Arrays.asList(recipients);
    }

    void to(List<String> recipients) {
        message.to = recipients;
    }

    // Field "bcc"
    void bcc(String val) {
        message.bcc = [val];
    }

    void bcc(String[] recipients) {
        message.bcc = Arrays.asList(recipients);
    }

    void bcc(List<String> recipients) {
        message.bcc = recipients;
    }

    // Field "cc"
    void cc(String val) {
        message.cc = [val];
    }

    void cc(String[] recipients) {
        message.cc = Arrays.asList(recipients);
    }

    void cc(List<String> recipients) {
        message.cc = recipients;
    }

    // Field "replyTo"
    void replyTo(CharSequence val) {
        message.replyTo = val.toString();
    }

    // Field "from"
    void from(CharSequence sender) {
        message.from = sender.toString();
    }

    // Field "subject"
    void title(CharSequence subject1) {
        subject(subject1);
    }

    void subject(CharSequence subject) {
        message.subject = subject.toString();
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

    /**
     * It's added to compatibility with Mail plugin
     */
    boolean isMimeCapable() {
        // TODO: I need more time for think about it
        return true;
    }
}
