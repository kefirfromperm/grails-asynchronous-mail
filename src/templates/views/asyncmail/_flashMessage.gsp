<%@ page contentType="text/html;charset=UTF-8" %>
<%-- Show flash message --%>
<g:if test="${flash.message}">
    <g:set var="flashClass" value="message"/>
    <g:if test="${flash.error}">
        <g:set var="flashClass" value="${flashClass+' error'}"/>
    </g:if>
    <div class="${flashClass}">${flash.message}</div>
</g:if>
