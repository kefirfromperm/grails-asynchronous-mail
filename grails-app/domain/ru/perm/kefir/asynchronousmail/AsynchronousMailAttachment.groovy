package ru.perm.kefir.asynchronousmail

class AsynchronousMailAttachment {
    String attachmentName;
    String mimeType = 'application/octet-stream';
    byte[] content;

    static belongsTo = [message:AsynchronousMailMessage];

    static constraints = {
        attachmentName(nullable:false, blank:false);
        mimeType(nullable:false);
        content(nullable:false);
    }
}
