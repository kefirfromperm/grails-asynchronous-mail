import ru.perm.kefir.asynchronousmail.*
import grails.plugin.asyncmail.AsynchronousMailMessage
import grails.plugin.asyncmail.MessageStatus

class AsynchronousMailController {
    static defaultAction = 'list';

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        params.max = Math.min(params.max ? params.max.toInteger() : 100, 100)
        if (!params.sort) {
            params.sort = 'createDate';
        }
        if (!params.order) {
            params.order = 'desc';
        }
        [list: AsynchronousMailMessage.list(params), total: AsynchronousMailMessage.count()]
    }

    private Closure withMessage(Closure cl) {
        return {
            AsynchronousMailMessage message = AsynchronousMailMessage.get(params.id);
            if (message) {
                return cl(message);
            } else {
                flash.message = "Message not found with id ${params.id}";
                redirect(action: 'list');
            }
        };
    }

    def show = withMessage {AsynchronousMailMessage message ->
        return [message: message];
    }

    def edit = withMessage {AsynchronousMailMessage message ->
        return [message: message];
    }

    def update = withMessage {AsynchronousMailMessage message ->
        bindData(message, params, [include:['status', 'beginDate', 'endDate', 'maxAttemptsCount', 'attemptInterval']]);
        message.attemptsCount = 0;
        if (!message.hasErrors() && message.save()) {
            flash.message = "Message ${params.id} updated"
            redirect(action: show, id: message.id)
        } else {
            render(view: 'edit', model: [message: message])
        }
    }

    /** Abort message sent   */
    def abort = withMessage {AsynchronousMailMessage message ->
        if (message.status == MessageStatus.CREATED || message.status == MessageStatus.ATTEMPTED) {
            message.status = MessageStatus.ABORT;
            if (!message.save()) {
                flash.message = "Can't abort message with id ${message.id}";
            }
        } else {
            flash.message = "Can't abort message with id ${message.id} and status ${message.status}";
        }
        redirect(action: 'list');
    }
}
