<%@ page import="ru.perm.kefir.asynchronousmail.MessageStatus; ru.perm.kefir.asynchronousmail.AsynchronousMailMessage" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Edit Asynchronous Mail Message</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">List</g:link></span>
        </div>
        <div class="body">
            <h1>Edit Asynchronous Mail Message</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${asynchronousMailMessageInstance}">
            <div class="errors">
                <g:renderErrors bean="${asynchronousMailMessageInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <input type="hidden" name="id" value="${message.id}" />
                <div class="dialog">
                    <table>
                        <tbody>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label>Subject:</label>
                                </td>
                                <td valign="top" class="value">
                                    ${fieldValue(bean:message,field:'subject')}
                                </td>
                            </tr> 

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="status">Status:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:message,field:'status','errors')}">
                                    <g:select from="${[MessageStatus.CREATED, MessageStatus.ABORT]}" value="${message.status}" name="status"/>
                                </td>
                            </tr> 

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="beginDate">Begin date:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:message,field:'beginDate','errors')}">
                                    <g:datePicker name="beginDate" value="${message.beginDate}" precision="minute"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="endDate">End date:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:message,field:'endDate','errors')}">
                                    <g:datePicker name="endDate" value="${message.endDate}" precision="minute" />
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="maxAttemptsCount">Max attempts count:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:message,field:'maxAttemptsCount','errors')}">
                                    <input type="text" id="maxAttemptsCount" name="maxAttemptsCount" value="${fieldValue(bean:message,field:'maxAttemptsCount')}" />
                                </td>
                            </tr> 

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="attemptInterval">Attempt interval (ms):</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:message,field:'attemptInterval','errors')}">
                                    <input type="text" id="attemptInterval" name="attemptInterval" value="${fieldValue(bean:message,field:'attemptInterval')}" />
                                </td>
                            </tr> 
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" action="update" value="Update" /></span>
                    <g:if test="${message.status == MessageStatus.CREATED || message.status == MessageStatus.ATTEMPTED}">
                    <span class="button"><g:actionSubmit class="delete" action="abort" onclick="return confirm('Are you sure?');" value="Abort" /></span>
                    </g:if>
                </div>
            </g:form>
        </div>
    </body>
</html>
