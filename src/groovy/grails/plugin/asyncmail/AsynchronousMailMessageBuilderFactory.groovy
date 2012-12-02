package grails.plugin.asyncmail

import javax.activation.FileTypeMap
import javax.activation.MimetypesFileTypeMap

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.mail.javamail.JavaMailSender

/**
 * Create an message builder.
 *
 * @author Vitalii Samolovskikh aka Kefir
 */
class AsynchronousMailMessageBuilderFactory {
    def mailMessageContentRenderer
    def mailSender
    GrailsApplication grailsApplication
    private final FileTypeMap fileTypeMap = new MimetypesFileTypeMap()

    AsynchronousMailMessageBuilder createBuilder() {
        AsynchronousMailMessageBuilder builder = new AsynchronousMailMessageBuilder(
                (mailSender instanceof JavaMailSender),
                grailsApplication.config,
                fileTypeMap,
                mailMessageContentRenderer
        )
        builder.init(grailsApplication.config)
        return builder
    }
}
