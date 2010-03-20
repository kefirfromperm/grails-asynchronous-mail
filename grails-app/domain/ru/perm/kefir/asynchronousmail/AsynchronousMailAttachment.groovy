package ru.perm.kefir.asynchronousmail

class AsynchronousMailAttachment {
    public static final DEFAULT_MIME_TYPE = 'application/octet-stream';

    String attachmentName;
    String mimeType = DEFAULT_MIME_TYPE;
    byte[] content;

    static belongsTo = [message:AsynchronousMailMessage];

    static constraints = {
        attachmentName(nullable:false, blank:false);
        mimeType(nullable:false);
        content(nullable:false);
    }
}
