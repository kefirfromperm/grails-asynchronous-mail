package ru.perm.kefir.asynchronousmail

import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * Build new synchronous message
 */
class AsynchronousMailMessageBuilder {
    AsynchronousMailMessage message;

    def init() {
        message = new AsynchronousMailMessage(); 
        message.attemptInterval = ConfigurationHolder.config.asynchronous.mail.default.attempt.interval;
        message.maxAttemptsCount = ConfigurationHolder.config.asynchronous.mail.default.max.attempts.count;
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
}
