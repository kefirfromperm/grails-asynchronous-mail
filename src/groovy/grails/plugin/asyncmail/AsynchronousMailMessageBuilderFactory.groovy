package grails.plugin.asyncmail

import org.codehaus.groovy.grails.commons.GrailsApplication
import javax.activation.FileTypeMap
import javax.activation.MimetypesFileTypeMap
import org.springframework.mail.javamail.JavaMailSender

/**
 * @author Vitalii Samolovskikh aka Kefir
 */
class AsynchronousMailMessageBuilderFactory {
    def mailMessageContentRenderer;
    def mailSender;
    GrailsApplication grailsApplication;
    private final FileTypeMap fileTypeMap;

    AsynchronousMailMessageBuilderFactory() {
        fileTypeMap = new MimetypesFileTypeMap();
    }

    public AsynchronousMailMessageBuilder createBuilder(){
        AsynchronousMailMessageBuilder builder = new AsynchronousMailMessageBuilder();
        builder.mailMessageContentRenderer = mailMessageContentRenderer;
        builder.fileTypeMap = fileTypeMap;
        builder.setMimeCapable(mailSender instanceof JavaMailSender);
        builder.init(grailsApplication.config);
        return builder;
    }
}
