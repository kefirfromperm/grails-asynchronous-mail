package grails.plugin.asyncmail

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import org.springframework.mail.javamail.JavaMailSender

import javax.activation.FileTypeMap
import javax.activation.MimetypesFileTypeMap
/**
 * Create a message builder.
 *
 * @author Vitalii Samolovskikh aka Kefir
 * @coauthor Puneet Behl
 */
class AsynchronousMailMessageBuilderFactory implements GrailsConfigurationAware {
    def mailMessageContentRenderer
    def mailSender
    Config configuration
    private final FileTypeMap fileTypeMap = new MimetypesFileTypeMap()

    AsynchronousMailMessageBuilder createBuilder() {
        AsynchronousMailMessageBuilder builder = new AsynchronousMailMessageBuilder(
                (mailSender instanceof JavaMailSender),
                configuration,
                fileTypeMap,
                mailMessageContentRenderer
        )
        builder.init(configuration)
        return builder
    }
}
