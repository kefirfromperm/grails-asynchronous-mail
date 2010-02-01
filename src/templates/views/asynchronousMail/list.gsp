<%@ page import="ru.perm.kefir.asynchronousmail.MessageStatus; ru.perm.kefir.asynchronousmail.AsynchronousMailMessage" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Asynchronous Mail Message List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'')}">Home</a></span>
        </div>
        <div class="body">
            <h1>Asynchronous Mail Message List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                            <g:sortableColumn property="id" title="Id"/>
                   	        <g:sortableColumn property="to" title="To" />
                   	        <g:sortableColumn property="subject" title="Subject" />
                   	        <th>Status</th>
                   	        <g:sortableColumn property="createDate" title="Create Date" />
                            <th>Attachments</th>
                            <th>&nbsp;</th>
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${list}" status="i" var="message">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                            <td>${message.id}</td>
                            <td>${fieldValue(bean:message, field:'to')}</td>
                            <td>${fieldValue(bean:message, field:'subject')}</td>
                            <td>${fieldValue(bean:message, field:'status')}</td>
                            <td><g:formatDate date="${message.createDate}"/></td>
                            <td>${message.attachments?.size()}</td>
                            <td>
                                <g:link action="show" id="${message.id}">show</g:link>
                                <g:link action="edit" id="${message.id}">edit</g:link>
                                <g:if test="${message.status == MessageStatus.CREATED || message.status == MessageStatus.ATTEMPTED}">
                                    <g:link action="abort" id="${message.id}" onclick="return confirm('Are you sure?');">abort</g:link>
                                </g:if>
                            </td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${total}" />
            </div>
        </div>
    </body>
</html>
