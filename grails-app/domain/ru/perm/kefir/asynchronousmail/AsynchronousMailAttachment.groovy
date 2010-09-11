package ru.perm.kefir.asynchronousmail

class AsynchronousMailAttachment implements Serializable{
    public static final DEFAULT_MIME_TYPE = 'application/octet-stream';
    private static final SIZE_30_MB = 30*1024*1024;

    String attachmentName;
    String mimeType = DEFAULT_MIME_TYPE;
    byte[] content;

    static belongsTo = [message:AsynchronousMailMessage];

    static constraints = {
        attachmentName(nullable:false, blank:false);
        mimeType(nullable:false);
        content(nullable:false, maxSize:SIZE_30_MB);
    }
}
