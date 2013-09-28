package grails.plugin.asyncmail


class AsynchronousMailPersistenceService {
    def grailsApplication

    private AsynchronousMailMessage save(AsynchronousMailMessage message, boolean flush = false) {
        return message.save(flush: flush)
    }

    void delete(AsynchronousMailMessage message) {
        message.delete()
    }

    AsynchronousMailMessage getMessage(long id){
        return AsynchronousMailMessage.get(id)
    }

    List<Long> selectMessagesIdsForSend(){
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
            maxResults((int) grailsApplication.config.asynchronous.mail.messages.at.once)
            projections {
                property('id')
            }
        }
    }

    void updateExpiredMessages(){
        int count = 0
        AsynchronousMailMessage.withTransaction {
            count = AsynchronousMailMessage.executeUpdate(
                    "update AsynchronousMailMessage amm set amm.status=:es where amm.endDate<:date and (amm.status=:cs or amm.status=:as)",
                    ["es": MessageStatus.EXPIRED, "date": new Date(), "cs": MessageStatus.CREATED, "as": MessageStatus.ATTEMPTED]
            )
        }
        log.trace("${count} expired messages was updated.")
    }
}
