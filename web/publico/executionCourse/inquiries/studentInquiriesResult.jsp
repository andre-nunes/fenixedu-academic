<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@page import="net.sourceforge.fenixedu._development.PropertiesManager"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.net.URLEncoder"%>
<html:xhtml/>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<h2><bean:message key="label.teachingInquiries.studentInquiriesResults" bundle="INQUIRIES_RESOURCES"/></h2>

<logic:present role="PERSON">
<logic:present name="studentInquiriesCourseResults">
	<logic:iterate id="courseResult" name="studentInquiriesCourseResults" type="net.sourceforge.fenixedu.dataTransferObject.inquiries.StudentInquiriesCourseResultBean" >
		<p class="mtop2">
			<bean:message key="link.teachingInquiries.cuResults" bundle="INQUIRIES_RESOURCES"/> - 
			<html:link page="<%= "/executionCourse.do?method=showInquiryCourseResult&resultId=" + courseResult.getStudentInquiriesCourseResult().getIdInternal() %>" target="_blank">
				<bean:write name="courseResult" property="studentInquiriesCourseResult.executionCourse.nome" /> - 				
				<bean:write name="courseResult" property="studentInquiriesCourseResult.executionDegree.degreeCurricularPlan.name" />
			</html:link>
		</p>

		<logic:notEmpty name="courseResult" property="studentInquiriesTeachingResults">
			<ul>
				<logic:iterate id="teachingResult" name="courseResult" property="studentInquiriesTeachingResults" type="net.sourceforge.fenixedu.domain.inquiries.StudentInquiriesTeachingResult">
					<li>
						<html:link page="<%= "/executionCourse.do?method=showInquiryTeachingResult&resultId=" + teachingResult.getIdInternal() %>" target="_blank">
							<bean:write name="teachingResult" property="professorship.teacher.person.name" />
							&nbsp;(<bean:message name="teachingResult" property="shiftType.name"  bundle="ENUMERATION_RESOURCES"/>)<br/>
						</html:link>
					</li>			
				</logic:iterate>
			</ul>
		</logic:notEmpty>
	</logic:iterate>
	
	<logic:empty name="studentInquiriesCourseResults">
		<br/>
		<bean:message key="message.teachingInquiries.noResults.reasons" bundle="INQUIRIES_RESOURCES"/>
	</logic:empty>
</logic:present>
</logic:present>

<logic:notPresent role="PERSON">
	<bean:define id="executionCourse" name="executionCourse" type="net.sourceforge.fenixedu.domain.ExecutionCourse"/>
	<br/>
	<br/>
	<bean:message key="message.inquiries.information.not.public" bundle="INQUIRIES_RESOURCES"/>
	<%
		String port = request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort();

		String value = request.getScheme() + "://" + request.getServerName()
			+ port + request.getContextPath() + executionCourse.getSite().getReversePath() + "/resultados-quc";
		session.setAttribute("ORIGINAL_REQUEST", Boolean.TRUE);
		session.setAttribute("ORIGINAL_URI", value);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("service", value);
		session.setAttribute("ORIGINAL_PARAMETER_MAP", map);
		session.setAttribute("ORIGINAL_ATTRIBUTE_MAP", map);

	boolean isCasEnabled = PropertiesManager.getBooleanProperty("cas.enabled");
		if (isCasEnabled) {
			String casValue = request.getScheme() + "://" + request.getServerName()
				+ port + request.getContextPath() + "/loginCAS.do";
			String urlSuffix = "?service=" + casValue;
		    String loginPage = PropertiesManager.getProperty("cas.loginUrl") + urlSuffix;
	%>
			<html:link href="<%= loginPage %>">Login</html:link>
	<%
		} else {
			String urlSuffix = "?service=" + value;
			String loginPage = PropertiesManager.getProperty("login.page") + urlSuffix;
	%>
			<html:link href="<%= loginPage %>">Login</html:link>
	<%
		}
	%>
	
</logic:notPresent>