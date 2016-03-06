<%@ page import="grails.plugin.asyncmail.MessageStatus; grails.plugin.asyncmail.AsynchronousMailMessage" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <asset:stylesheet src="asyncmail.css"/>
    <title>Show Asynchronous Mail Message</title>
</head>

<body>
<%-- Header --%>
<h1>Show Asynchronous Mail Message</h1>

<%-- Navigation --%>
<div class="nav">
    <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="index">List</g:link></span>
</div>

<%-- Flash message --%>
<g:render template="flashMessage"/>

<div class="body">
    <table>
        <colgroup>
            <col width="25%">
            <col width="25%">
            <col width="25%">
            <col width="25%">
        </colgroup>
        <tbody>
        <tr class="odd">
            <td class="fieldName">From:</td>
            <td>
                <g:if test="${message.from}">
                    <a href="mailto:${message.from.encodeAsURL()}">${message.from.encodeAsHTML()}</a>
                </g:if>
            </td>
            <td class="fieldName">Reply to:</td>
            <td>
                <g:if test="${message.replyTo}">
                    <a href="mailto:${message.replyTo.encodeAsURL()}">${message.replyTo.encodeAsHTML()}</a>
                </g:if>
            </td>
        </tr>
        <tr class="even">
            <td class="fieldName">To:</td>
            <td colspan="3"><g:render template="listAddr" bean="${message.to}"/></td>
        </tr>
        <tr class="odd">
            <td class="fieldName">Cc:</td>
            <td colspan="3"><g:render template="listAddr" bean="${message.cc}"/></td>
        </tr>
        <tr class="even">
            <td class="fieldName">Bcc:</td>
            <td colspan="3"><g:render template="listAddr" bean="${message.bcc}"/></td>
        </tr>
        <tr class="odd">
            <td class="fieldName">Headers:</td>
            <td colspan="3">
                <g:each var="entry" in="${message.headers}" status="status"><g:if test="${status!=0}">,</g:if>
                    ${entry.key?.encodeAsHTML()}:${entry.value?.encodeAsHTML()}</g:each>
            </td>
        </tr>
        <tr class="even">
            <td class="fieldName">Subject:</td>
            <td colspan="3">${message.subject?.encodeAsHTML()}</td>
        </tr>
        <tr class="odd"><td colspan="4">${message.text?.encodeAsHTML()}</td></tr>
        <tr class="even">
            <td class="fieldName">Attachments:</td>
            <td colspan="3">
                <g:each var="attachment" in="${message.attachments}" status="status"><g:if test="${status!=0}">,</g:if>
                    ${attachment.attachmentName?.encodeAsHTML()}</g:each>
            </td>
        </tr>
        <tr class="odd">
            <td class="fieldName">Create date:</td>
            <td><g:formatDate date="${message.createDate}" format="yyyy-MM-dd HH:mm:ss"/></td>
            <td class="fieldName">Status:</td>
            <td>${message.status.encodeAsHTML()}</td>
        </tr>
        <tr class="even">
            <td class="fieldName">Last attempt date:</td>
            <td><g:formatDate date="${message.lastAttemptDate}" format="yyyy-MM-dd HH:mm:ss"/></td>
            <td class="fieldName">Attempts count:</td>
            <td>${fieldValue(bean: message, field: 'attemptsCount')}</td>
        </tr>
        <tr class="odd">
            <td class="fieldName">Sent date:</td>
            <td><g:formatDate date="${message.sentDate}" format="yyyy-MM-dd HH:mm:ss"/></td>
            <td class="fieldName">Max attempts count:</td>
            <td>
                <g:if test="${message.maxAttemptsCount==0}">INFINITE</g:if>
                <g:else>${fieldValue(bean: message, field: 'maxAttemptsCount')}</g:else>
            </td>
        </tr>
        <tr class="even">
            <td class="fieldName">Begin date:</td>
            <td><g:formatDate date="${message.beginDate}" format="yyyy-MM-dd HH:mm:ss"/></td>
            <td class="fieldName">Attempt interval (ms):</td>
            <td><g:formatNumber number="${message.attemptInterval}" format="#,##0"/></td>
        </tr>
        <tr class="odd">
            <td class="fieldName">End date:</td>
            <td><g:formatDate date="${message.endDate}" format="yyyy-MM-dd HH:mm:ss"/></td>
            <td class="fieldName">Priority:</td>
            <td><g:formatNumber number="${message.priority}" format="#,##0"/></td>
        </tr>
        <tr class="even">
            <td class="fieldName">HTML:</td>
            <td>${fieldValue(bean: message, field: 'html')}</td>
            <td class="fieldName">Delete after sent:</td>
            <td>${fieldValue(bean: message, field: 'markDelete')}</td>
        </tr>
        </tbody>
    </table>

    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${message?.id}"/>
            <span class="button"><g:actionSubmit class="edit" action="edit" value="Edit"/></span>
            <g:if test="${message.abortable}">
                <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');"
                                                     action="abort" value="Abort"/></span>
            </g:if>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');"
                                                 action="delete" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
