package ru.perm.kefir.asynchronousmail

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.grails.mail.MailMessageBuilder
import org.springframework.web.context.request.RequestContextHolder
import javax.servlet.http.HttpServletRequest
import org.codehaus.groovy.grails.web.servlet.DefaultGrailsApplicationAttributes
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.springframework.web.context.support.WebApplicationContextUtils
import grails.util.GrailsWebUtil
import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import org.codehaus.groovy.grails.commons.GrailsResourceUtils
import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU

/**
 * Build new synchronous message
 */
class AsynchronousMailMessageBuilder {
    AsynchronousMailMessage message;
    boolean immediately = false;
    boolean immediatelySetted = false;

    def groovyPagesTemplateEngine;

    def AsynchronousMailMessageBuilder(groovyPagesTemplateEngine) {
        this.groovyPagesTemplateEngine = groovyPagesTemplateEngine;
    }

    def init() {
        message = new AsynchronousMailMessage(); 
        message.attemptInterval = ConfigurationHolder.config?.asynchronous?.mail?.default?.attempt?.interval?:300000l;
        message.maxAttemptsCount = ConfigurationHolder.config?.asynchronous?.mail?.default?.max?.attempts?.count?:1;
    }

    // Specified fields for asynchronous message
    void beginDate(Date begin){
        message.beginDate = begin;
    }

    void endDate(Date end){
        message.endDate = end;
    }

    void maxAttemptsCount(Integer max){
        message.maxAttemptsCount = max;
    }

    void attemptInterval(Long interval){
        message.attemptInterval = interval;
    }

    // Multipart field do nothing
    void multipart(boolean multipart) {
        // nothing
        // Added analogous to mail plugin
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

    // Field "subject"
    void title(String subject) {
        subject(subject);
    }

    void subject(String subject) {
        message.subject = subject;
    }

    // Headers
    void headers(Map headers) {
        message.headers = headers;
    }

    // Body
    void body(CharSequence seq) {
        text(seq);
    }

    void body(Map params) {
        if (params.view) {
            // Here need to render it first, establish content type of virtual response / contentType model param
            renderMailView(params.view, params.model, params.plugin)
        }
    }

    void text(CharSequence seq) {
        message.html = false;
        message.text = seq.toString();
    }

    void html(CharSequence seq) {
        message.html = true;
        message.text = seq.toString();
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
    void replyTo(String val) {
        message.replyTo = val;
    }

    // Field "from"
    void from(String sender) {
        message.from = sender;
    }

    // Attachments
    void attachBytes(String name, String mimeType, byte[] content){
        message.addToAttachments(
                new AsynchronousMailAttachment(
                        attachmentName:name, mimeType:mimeType, content:content
                )
        );
    }

    protected renderMailView(templateName, model, pluginName = null) {
        if(!groovyPagesTemplateEngine) throw new IllegalStateException("Property [groovyPagesTemplateEngine] must be set!")
        assert templateName

        def engine = groovyPagesTemplateEngine
        def requestAttributes = RequestContextHolder.getRequestAttributes()
        boolean unbindRequest = false

        // outside of an executing request, establish a mock version
        if(!requestAttributes) {
            def servletContext  = ServletContextHolder.getServletContext()
            def applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext)
            requestAttributes = GrailsWebUtil.bindMockWebRequest(applicationContext)
            unbindRequest = true
        }
        def request = requestAttributes.request

        // See if the application has the view for it
        def uri = getMailViewUri(templateName, request)

        def r = engine.getResourceForUri(uri)
        // Try plugin view if not found in application
        if (!r || !r.exists()) {
            if (log.debugEnabled) {
                log.debug "Could not locate email view ${templateName} at ${uri}, trying plugin"
            }
            if (pluginName) {
                // Caution, this uses views/ always, whereas our app view resolution uses the PATH_TO_MAILVIEWS which may in future be orthogonal!
                def plugin = PluginManagerHolder.pluginManager.getGrailsPlugin(pluginName)
                String pathToView = null
                if (plugin) {
                    pathToView = '/plugins/'+GCU.getScriptName(plugin.name)+'-'+plugin.version+'/'+GrailsResourceUtils.GRAILS_APP_DIR+'/views'
                }

                if (pathToView != null) {
                    uri = GrailsResourceUtils.WEB_INF +pathToView +templateName+".gsp";
                    r = engine.getResourceForUri(uri)
                } else {
                    if (log.errorEnabled) {
                        log.error "Could not locate email view ${templateName} in plugin [$pluginName]"
                    }
                    throw new IllegalArgumentException("Could not locate email view ${templateName} in plugin [$pluginName]")
                }
            } else {
                if (log.errorEnabled) {
                    log.error "Could not locate email view ${templateName} at ${uri}, no pluginName specified so couldn't look there"
                }
                throw new IllegalArgumentException("Could not locate mail body ${templateName}. Is it in a plugin? If so you must pass the plugin name in the [plugin] variable")
            }
        }
        def t = engine.createTemplate(r)

        def out = new StringWriter();
        def originalOut = requestAttributes.getOut()
        requestAttributes.setOut(out)
        try {
            if(model instanceof Map) {
                t.make( model ).writeTo(out)
            } else {
                t.make().writeTo(out)
            }
        } finally {
            requestAttributes.setOut(originalOut)
            if(unbindRequest) {
                RequestContextHolder.setRequestAttributes(null)
            }
        }

        if (MailMessageBuilder.HTML_CONTENTTYPES.contains(t.metaInfo.contentType)) {
            html(out.toString())
        } else {
            text(out.toString())
        }
    }

    protected String getMailViewUri(String viewName, HttpServletRequest request) {
        def buf = new StringBuilder(MailMessageBuilder.PATH_TO_MAILVIEWS)

        if(viewName.startsWith("/")) {
           def tmp = viewName[1..-1]
           if(tmp.indexOf('/') > -1) {
               def i = tmp.lastIndexOf('/')
               buf << "/${tmp[0..i]}/${tmp[(i+1)..-1]}"
           }
           else {
               buf << "/${viewName[1..-1]}"
           }
        } else {
           if (!request) throw new IllegalArgumentException(
               "Mail views cannot be loaded from relative view paths where there is no current HTTP request")
           def grailsAttributes = new DefaultGrailsApplicationAttributes(request.servletContext)
           buf << "${grailsAttributes.getControllerUri(request)}/${viewName}"
        }
        return buf.append(".gsp").toString()
    }

    void immediate(boolean value){
        immediately = value;
        immediatelySetted = true;
    }
}
