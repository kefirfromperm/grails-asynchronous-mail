package grails.plugin.asyncmail

import static grails.util.Holders.config as HC

class AsynchronousMailAttachment implements Serializable {
    static final CONF_DS = 'asynchronous.mapping.datasource'
    static final CONF_MAPPING = 'asynchronous.mapping.attachment'
    static final CONF_MAPPING_TABLE = 'asynchronous.mapping.attachment.table'
    static final CONF_CONSTRAINTS = 'asynchronous.constraints.attachment'
    static final CONF_SIZE = 'asynchronous.constraints.attachment.size'

    
    static final DEFAULT_MIME_TYPE = 'application/octet-stream'

    private static final SIZE_30_MB = 30*1024*1024

    String attachmentName
    String mimeType = DEFAULT_MIME_TYPE
    byte[] content
    boolean inline = false

    static belongsTo = [message:AsynchronousMailMessage]

    static mapping = {
        if(HC.getProperty(CONF_DS)){
            datasource HC.getProperty(CONF_DS)
        }

        table HC.getProperty(CONF_MAPPING_TABLE,'async_mail_attachment')
        
        version false

        def customMapping = HC.getProperty(CONF_MAPPING,Closure)
        if(customMapping){
            def mappingCode = customMapping.rehydrate(delegate,this,this)
            mappingCode.resolveStrategy = Closure.DELEGATE_FIRST
            mappingCode()
        }
    }

    static constraints = {
        attachmentName(blank:false)
        //mimeType()
        content(maxSize:HC.getProperty(CONF_SIZE,Integer.class,SIZE_30_MB))

        def customConstraints = HC.getProperty(CONF_CONSTRAINTS,Closure)
        if(customConstraints){
            def constraintCode = customConstraints.rehydrate(delegate,this,this)
            constraintCode.resolveStrategy = Closure.DELEGATE_FIRST
            constraintCode()
        }
    }
}
