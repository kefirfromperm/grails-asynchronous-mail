package test

import grails.plugin.asyncmail.AsynchronousMailMessage
import grails.plugin.asyncmail.MessageStatus

class AsynchronousMailController {
    static defaultAction = 'list';

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [update: 'POST']

    /**
     * Show all message in table.
     */
    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        if (!params.sort) {
            params.sort = 'createDate';
        }
        if (!params.order) {
            params.order = 'desc';
        }
        [list: AsynchronousMailMessage.list(params), total: AsynchronousMailMessage.count()]
    }

    private withMessage(Closure cl) {
        AsynchronousMailMessage message = AsynchronousMailMessage.get(params.id);
        if (message) {
            return cl(message);
        } else {
            flash.message = "Message with id ${params.id} not found.";
            flash.error = true;
            redirect(action: 'list');
        }
    }

    /**
     * Show message data.
     */
    def show() {
        withMessage {AsynchronousMailMessage message ->
            return [message: message];
        }
    }

    /**
     * Show form for editing.
     */
    def edit() {
        withMessage {AsynchronousMailMessage message ->
            return [message: message];
        }
    }

    /**
     * Update message
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
            );
            message.attemptsCount = 0;
            if (!message.hasErrors() && message.save()) {
                flash.message = "Message ${params.id} was updated."
                redirect(action: 'show', id: message.id)
            } else {
                render(view: 'edit', model: [message: message])
            }
        }
    }

    /**
     * Abort message sent
     */
    def abort() {
        withMessage {AsynchronousMailMessage message ->
            if (message.abortable) {
                message.status = MessageStatus.ABORT;
                if (message.save()) {
                    flash.message = "Message ${message.id} was aborted."
                } else {
                    flash.message = "Can't abort message with id ${message.id}.";
                    flash.error = true;
                }
            } else {
                flash.message = "Can't abort message with id ${message.id} and status ${message.status}.";
                flash.error = true;
            }
            redirect(action: 'list');
        }
    }

    /**
     * Delete message
     */
    def delete() {
        withMessage {AsynchronousMailMessage message ->
            try {
                message.delete();
                flash.message = "Message with id ${message.id} was deleted.";
                redirect(action: 'list');
            } catch (Exception e) {
                flash.message = "Can't delete message with id ${message.id}.";
                flash.error = true;
                redirect(action: 'show', id: message.id);
            }
        }
    }
}
