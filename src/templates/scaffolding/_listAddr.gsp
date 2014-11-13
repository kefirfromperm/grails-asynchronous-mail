<%@ page contentType="text/html;charset=UTF-8" %>
<g:each in="${it}" var="addr" status="j"><g:if test="${j != 0}">,</g:if>
<a href="mailto:${addr.encodeAsURL()}">${addr.encodeAsHTML()}</a></g:each>
