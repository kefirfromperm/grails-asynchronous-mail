<%@ page import="grails.plugin.asyncmail.MessageStatus; grails.plugin.asyncmail.AsynchronousMailMessage" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <asset:stylesheet src="asyncmail.css"/>
    <title>Asynchronous Mail Message List</title>
</head>

<body>

<%-- Header --%>
<h1>Asynchronous Mail Message List</h1>

<%-- Navigation --%>
<div class="nav">
    <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
</div>

<%-- Flash message --%>
<g:render template="flashMessage"/>

<%-- Table --%>
<div class="body">
    <table>
        <thead>
        <tr>
            <g:sortableColumn property="id" title="Id"/>
            <g:sortableColumn property="subject" title="Subject"/>
            <th>To</th>
            <g:sortableColumn property="createDate" title="Create Date"/>
            <th>Status</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${resultList}" status="i" var="message">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td class="id">${message.id}</td>
                <td><g:link action="show" id="${message.id}">${fieldValue(bean: message, field: 'subject')}</g:link></td>
                <td><g:render template="listAddr" bean="${message.to}"/></td>
                <td><g:formatDate date="${message.createDate}" format="yyyy-MM-dd HH:mm:ss"/></td>
                <td>${fieldValue(bean: message, field: 'status')}</td>
                <td>
                    <g:link action="show" id="${message.id}">show</g:link>
                    <g:link action="edit" id="${message.id}">edit</g:link>
                    <g:if test="${message.abortable}">
                        <g:link action="abort" id="${message.id}"
                                onclick="return confirm('Are you sure?');">abort</g:link>
                    </g:if>
                    <g:link action="delete" id="${message.id}"
                            onclick="return confirm('Are you sure?');">delete</g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

    <div class="paginate">
        <g:paginate total="${resultList.totalCount}"/>
    </div>
</div>
</body>
</html>
