<%@ include file="/init.jsp" %>

<c:forEach var="themeVariablesName" items="${themeVariablesNames}">
	${themeVariablesName}<br/>
</c:forEach>