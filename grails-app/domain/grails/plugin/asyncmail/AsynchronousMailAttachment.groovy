package grails.plugin.asyncmail

class AsynchronousMailAttachment implements Serializable{
    public static final DEFAULT_MIME_TYPE = 'application/octet-stream';
    private static final SIZE_30_MB = 30*1024*1024;

    String attachmentName;
    String mimeType = DEFAULT_MIME_TYPE;
    byte[] content;
    boolean inline = false;

    static belongsTo = [message:AsynchronousMailMessage];

    static mapping = {
        table 'async_mail_attachment';
        version false;
    }

    static constraints = {
        attachmentName(nullable:false, blank:false);
        mimeType(nullable:false);
        content(nullable:false, maxSize:SIZE_30_MB);
    }
}
