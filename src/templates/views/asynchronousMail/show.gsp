<%@ page import="ru.perm.kefir.asynchronousmail.MessageStatus; ru.perm.kefir.asynchronousmail.AsynchronousMailMessage" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show Asynchronous Mail Message</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">List</g:link></span>
</div>
<div class="body">
    <h1>Show Asynchronous Mail Message</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div class="dialog">
        <table>
            <tbody>

            <tr class="prop">
                <td valign="top" class="name">Subject:</td>
                <td valign="top" class="value">${fieldValue(bean: message, field: 'subject')}</td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">from:</td>
                <td valign="top" class="value">${fieldValue(bean: message, field: 'from')}</td>
            </tr>

            <g:if test="${message.replyTo}">
            <tr class="prop">
                <td valign="top" class="name">reply to:</td>
                <td valign="top" class="value">${fieldValue(bean: message, field: 'replyTo')}</td>
            </tr>
            </g:if>

            <tr class="prop">
                <td valign="top" class="name">to:</td>
                <td valign="top" class="value">${fieldValue(bean: message, field: 'to')}</td>
            </tr>

            <g:if test="${message.cc}">
            <tr class="prop">
                <td valign="top" class="name">cc:</td>
                <td valign="top" class="value">${fieldValue(bean: message, field: 'cc')}</td>
            </tr>
            </g:if>

            <g:if test="${message.bcc}">
            <tr class="prop">
                <td valign="top" class="name">bcc:</td>
                <td valign="top" class="value">${fieldValue(bean: message, field: 'bcc')}</td>
            </tr>
            </g:if>

            <g:if test="${message.headers}">
                <tr class="prop">
                    <td valign="top" class="name">Headers:</td>
                    <td valign="top" class="value">
                        <g:each var="entry" in="${message.headers}" status="status">
                            <g:if test="${status!=0}">,</g:if>
                            ${entry.key?.encodeAsHTML()}:${entry.value?.encodeAsHTML()}
                        </g:each>
                    </td>
                </tr>
            </g:if>

            <tr class="prop">
                <td valign="top" class="name">Status:</td>
                <td valign="top" class="value">${message.status.encodeAsHTML()}</td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">Create date:</td>
                <td valign="top" class="value"><g:formatDate date="${message.createDate}"/></td>
            </tr>

            <g:if test="${message.status==MessageStatus.ATTEMPTED && message.lastAttemptDate}">
                <tr class="prop">
                    <td valign="top" class="name">Last attempt date:</td>
                    <td valign="top" class="value"><g:formatDate date="${message.lastAttemptDate}"/></td>
                </tr>
            </g:if>

            <g:if test="${(message.status==MessageStatus.SENT || message.status==MessageStatus.ERROR) && message.sentDate}">
                <tr class="prop">
                    <td valign="top" class="name">Sent date:</td>
                    <td valign="top" class="value"><g:formatDate date="${message.sentDate}"/></td>
                </tr>
            </g:if>

            <tr class="prop">
                <td valign="top" class="name">Begin date:</td>
                <td valign="top" class="value"><g:formatDate date="${message.beginDate}"/></td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">End date:</td>
                <td valign="top" class="value"><g:formatDate date="${message.endDate}"/></td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">Attempts count:</td>
                <td valign="top" class="value">${fieldValue(bean: message, field: 'attemptsCount')}</td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">Max attempts count:</td>
                <td valign="top" class="value">
                    <g:if test="${message.maxAttemptsCount==0}">INFINITE</g:if>
                    <g:else>${fieldValue(bean: message, field: 'maxAttemptsCount')}</g:else>
                </td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">Attempt interval (ms):</td>
                <td valign="top" class="value">${fieldValue(bean: message, field: 'attemptInterval')}</td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">Html:</td>
                <td valign="top" class="value">${fieldValue(bean: message, field: 'html')}</td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">Text:</td>
                <td valign="top" class="value">${fieldValue(bean: message, field: 'text')}</td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">Attachments:</td>
                <td valign="top" class="value">
                    <g:each var="attachment" in="${message.attachments}" status="status">
                        <g:if test="${status!=0}">, </g:if>
                        ${attachment.attachmentName?.encodeAsHTML()}
                    </g:each>
                </td>
            </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${message?.id}"/>
            <span class="button"><g:actionSubmit class="edit" action="edit" value="Edit"/></span>
            <g:if test="${message.status == MessageStatus.CREATED || message.status == MessageStatus.ATTEMPTED}">
                <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Abort"/></span>
            </g:if>
        </g:form>
    </div>
</div>
</body>
</html>
