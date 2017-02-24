package grails.plugin.asyncmail

import grails.plugin.asyncmail.enums.MessageStatus
import groovy.transform.CompileStatic

class AsynchronousMailPersistenceService {

    AsynchronousMailConfigService asynchronousMailConfigService

    @CompileStatic
    AsynchronousMailMessage save(
            AsynchronousMailMessage message, boolean flush, boolean validate
    ) {
        return message.save(flush: flush, failOnError:true, validate: validate)
    }

    @CompileStatic
    void delete(AsynchronousMailMessage message) {
        message.delete(flush: true)
    }

    @CompileStatic
    void deleteAttachments(AsynchronousMailMessage message) {
        message.attachments.clear()
        message.save(flush: true)
    }

    @CompileStatic
    AsynchronousMailMessage getMessage(long id) {
        return AsynchronousMailMessage.get(id)
    }

    List<Long> selectMessagesIdsForSend() {
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
            maxResults(asynchronousMailConfigService.messagesAtOnce)
            projections {
                if (asynchronousMailConfigService.mongo) {
                    id()
                } else {
                    property('id')
                }
            }
        } as List<Long>
    }

    void updateExpiredMessages() {
        int count = 0
        if (asynchronousMailConfigService.mongo) {
            AsynchronousMailMessage.withCriteria {
                lt "endDate", new Date()
                or {
                    eq "status", MessageStatus.CREATED
                    eq "status", MessageStatus.ATTEMPTED
                }
            }.each {
                it.status = MessageStatus.EXPIRED
                it.save(flush: true)
                count++
            }
        } else {
            // This could be done also with the above code.
            AsynchronousMailMessage.withTransaction {
                count = AsynchronousMailMessage.executeUpdate(
                        "update AsynchronousMailMessage amm set amm.status=:es where amm.endDate<:date and (amm.status=:cs or amm.status=:as)",
                        ["es": MessageStatus.EXPIRED, "date": new Date(), "cs": MessageStatus.CREATED, "as": MessageStatus.ATTEMPTED]
                )
            }
        }
        log.trace("${count} expired messages were updated.")
    }
}