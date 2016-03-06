<%@ page import="grails.plugin.asyncmail.MessageStatus; grails.plugin.asyncmail.AsynchronousMailMessage" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <asset:stylesheet src="asyncmail.css"/>
    <title>Edit Asynchronous Mail Message</title>
</head>

<body>

<%-- Header --%>
<h1>Edit Asynchronous Mail Message</h1>

<%-- Navigation --%>
<div class="nav">
    <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="index">List</g:link></span>
</div>

<%-- Flash message --%>
<g:render template="flashMessage"/>

<div class="body">
    <g:hasErrors bean="${message}">
        <div class="error">
            <g:renderErrors bean="${message}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <input type="hidden" name="id" value="${message.id}"/>

        <table>
            <tbody>
            <tr class="odd">
                <td class="fieldName">Subject:</td>
                <td>${fieldValue(bean: message, field: 'subject')}</td>
            </tr>

            <tr class="even ${hasErrors(bean: message, field: 'status', 'error')}">
                <td class="fieldName">Status:</td>
                <td>
                    <g:select
                            from="${[grails.plugin.asyncmail.enums.MessageStatus.CREATED, grails.plugin.asyncmail.enums.MessageStatus.ABORT]}"
                            value="${message.status}" name="status"/>
                </td>
            </tr>

            <tr class="odd ${hasErrors(bean: message, field: 'beginDate', 'error')}">
                <td class="fieldName">Begin date:</td>
                <td><g:datePicker name="beginDate" value="${message.beginDate}" precision="minute"/></td>
            </tr>

            <tr class="even ${hasErrors(bean: message, field: 'endDate', 'error')}">
                <td class="fieldName">End date:</td>
                <td><g:datePicker name="endDate" value="${message.endDate}" precision="minute"/></td>
            </tr>

            <tr class="odd ${hasErrors(bean: message, field: 'maxAttemptsCount', 'error')}">
                <td class="fieldName">Max attempts count:</td>
                <td valign="top" class="value">
                    <input type="text" id="maxAttemptsCount" name="maxAttemptsCount"
                           value="${fieldValue(bean: message, field: 'maxAttemptsCount')}"/>
                </td>
            </tr>

            <tr class="even ${hasErrors(bean: message, field: 'attemptInterval', 'error')}">
                <td class="fieldName">Attempt interval (ms):</td>
                <td valign="top" class="value">
                    <input type="text" id="attemptInterval" name="attemptInterval"
                           value="${fieldValue(bean: message, field: 'attemptInterval')}"/>
                </td>
            </tr>

            <tr class="odd ${hasErrors(bean: message, field: 'priority', 'error')}">
                <td class="fieldName">Priority:</td>
                <td valign="top" class="value">
                    <input type="text" id="priority" name="priority"
                           value="${fieldValue(bean: message, field: 'priority')}"/>
                </td>
            </tr>

            <tr class="even ${hasErrors(bean: message, field: 'markDelete', 'error')}">
                <td class="fieldName">Delete after sent:</td>
                <td valign="top" class="value">
                    <g:checkBox name="markDelete" checked="${message.markDelete}"/>
                </td>
            </tr>
            </tbody>
        </table>

        <div class="buttons">
            <span class="button"><g:actionSubmit class="save" value="Update"/></span>
            <g:if test="${message.abortable}">
                <span class="button"><g:actionSubmit class="delete" action="abort"
                                                     onclick="return confirm('Are you sure?');" value="Abort"/></span>
            </g:if>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');"
                                                 action="delete" value="Delete"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
