package grails.plugin.asyncmail

import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * @author Vitaliy Samolovskih aka Kefir
 */
class AsynchronousMailMessageBuilderFactory {
    def mailMessageContentRenderer;
    GrailsApplication grailsApplication;

    public AsynchronousMailMessageBuilder createBuilder(){
        AsynchronousMailMessageBuilder builder = new AsynchronousMailMessageBuilder(mailMessageContentRenderer);
        ConfigObject config = grailsApplication.config;
        builder.init(config);
        return builder;
    }
}
