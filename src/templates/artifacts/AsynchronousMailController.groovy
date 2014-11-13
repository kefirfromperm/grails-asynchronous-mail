@artifact.package@
import grails.plugin.asyncmail.AsynchronousMailMessage
import grails.plugin.asyncmail.MessageStatus

class @artifact.name@ {
    static defaultAction = 'list'

    static allowedMethods = [update: 'POST']

    /**
     * List messages.
     */
    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        params.sort = params.sort ?: 'createDate'
        params.order = params.order ?: 'desc'
        [resultList: AsynchronousMailMessage.list(params)]
    }

    private withMessage(Closure cl) {
        AsynchronousMailMessage message = AsynchronousMailMessage.get(params.id)
        if (message) {
            return cl(message)
        }

        flash.message = "The message ${params.id} was not found."
        flash.error = true
        redirect(action: 'list')
    }

    /**
     * Show message data.
     */
    def show() {
        withMessage {AsynchronousMailMessage message ->
            return [message: message]
        }
    }

    /**
     * Edit message data.
     */
    def edit() {
        withMessage {AsynchronousMailMessage message ->
            return [message: message]
        }
    }

    /**
     * Update message.
     */
    def update() {
        withMessage {AsynchronousMailMessage message ->
            bindData(
                    message, params,
                    [include:
                            [
                                    'status',
                                    'beginDate',
                                    'endDate',
                                    'maxAttemptsCount',
                                    'attemptInterval',
                                    'priority',
                                    'markDelete'
                            ]
                    ]
            )
            message.attemptsCount = 0
            if (!message.hasErrors() && message.save()) {
                flash.message = "The message ${params.id} was updated."
                redirect(action: 'show', id: message.id)
            } else {
                render(view: 'edit', model: [message: message])
            }
        }
    }

    /**
     * Abort message sending.
     */
    def abort() {
        withMessage {AsynchronousMailMessage message ->
            if (message.abortable) {
                message.status = MessageStatus.ABORT
                if (message.save()) {
                    flash.message = "The message ${message.id} was aborted."
                } else {
                    flash.message = "Can't abort the message ${message.id}."
                    flash.error = true
                }
            } else {
                flash.message = "Can't abort the message ${message.id} with the status ${message.status}."
                flash.error = true
            }
            redirect(action: 'list')
        }
    }

    /**
     * Delete message.
     */
    def delete() {
        withMessage {AsynchronousMailMessage message ->
            try {
                message.delete()
                flash.message = "The message ${message.id} was deleted."
                redirect(action: 'list')
            } catch (Exception e) {
                def errorMessage = "Can't delete the message with the id ${message.id}.";
                log.error(errorMessage, e)
                flash.message = errorMessage
                flash.error = true
                redirect(action: 'show', id: message.id)
            }
        }
    }
}
