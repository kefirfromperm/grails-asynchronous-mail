package grails.plugin.asyncmail

import org.codehaus.groovy.grails.commons.GrailsApplication
import javax.activation.FileTypeMap
import javax.activation.MimetypesFileTypeMap

/**
 * @author Vitaliy Samolovskih aka Kefir
 */
class AsynchronousMailMessageBuilderFactory {
    def mailMessageContentRenderer;
    GrailsApplication grailsApplication;
    private final FileTypeMap fileTypeMap;

    AsynchronousMailMessageBuilderFactory() {
        fileTypeMap = new MimetypesFileTypeMap();
    }

    public AsynchronousMailMessageBuilder createBuilder(){
        AsynchronousMailMessageBuilder builder = new AsynchronousMailMessageBuilder();
        builder.mailMessageContentRenderer = mailMessageContentRenderer;
        builder.fileTypeMap = fileTypeMap;
        ConfigObject config = grailsApplication.config;
        builder.init(config);
        return builder;
    }
}
