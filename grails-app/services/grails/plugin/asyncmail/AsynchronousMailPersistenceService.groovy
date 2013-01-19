package grails.plugin.asyncmail

class AsynchronousMailPersistenceService {
    AsynchronousMailMessage save(AsynchronousMailMessage message, boolean flush = false){
        return message.save(flush: flush)
    }

    def delete(AsynchronousMailMessage message){
        message.delete()
    }

    def selectMessagesForSend(){
        return AsynchronousMailMessage.withCriteria {
            Date now = new Date()
            lt('beginDate', now)
            gt('endDate', now)
            or {
                eq('status', MessageStatus.CREATED)
                eq('status', MessageStatus.ATTEMPTED)
            }
            order('priority', 'desc')
            order('endDate', 'asc')
            order('attemptsCount', 'asc')
            order('beginDate', 'asc')
            maxResults((int) config.asynchronous.mail.messages.at.once)
        }
    }
}
