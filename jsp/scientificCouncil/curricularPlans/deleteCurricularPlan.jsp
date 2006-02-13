<%@ taglib uri="/WEB-INF/jsf_core.tld" prefix="f"%>
<%@ taglib uri="/WEB-INF/jsf_tiles.tld" prefix="ft"%>
<%@ taglib uri="/WEB-INF/html_basic.tld" prefix="h"%>

<ft:tilesView definition="scientificCouncil.masterPage" attributeName="body-inline">
	<f:loadBundle basename="resources/ScientificCouncilResources" var="scouncilBundle"/>	
	<f:loadBundle basename="resources/EnumerationResources" var="enumerationBundle"/>

	<h:outputText value="<i>#{ScientificCouncilCurricularPlanManagement.dcp.name}" escape="false"/>
	<h:outputText value=" (#{enumerationBundle[ScientificCouncilCurricularPlanManagement.dcp.curricularStage.name]})</i>" escape="false"/>
	<h:outputFormat value="<h2>#{scouncilBundle['delete.param']}</h2>" escape="false">
		<f:param value="#{scouncilBundle['curricularPlan']}" />
	</h:outputFormat>
	<h:form>
		<h:outputText escape="false" value="<input id='dcpId' name='dcpId' type='hidden' value='#{ScientificCouncilCurricularPlanManagement.dcpId}'/>"/>

		<h:messages infoClass="success0" errorClass="error0" layout="table" globalOnly="true"/>
		
		<h:outputText value="<div class='simpleblock1'/>" escape="false"/>
 			<h:outputText value="<p><b>#{scouncilBundle['curricularStage']}:</b> " escape="false"/>
			<h:outputText value="#{enumerationBundle[ScientificCouncilCurricularPlanManagement.dcp.curricularStage.name]}</p>" escape="false"/>

			<h:outputText value="<p><b>#{scouncilBundle['name']}:</b>" escape="false"/>
			<h:outputText value="#{ScientificCouncilCurricularPlanManagement.name}</p>" escape="false"/>
		<h:outputText value="</div>" escape="false"/>
		
		
		<h:outputText value="<br/><p>" escape="false"/>
		<h:commandButton styleClass="inputbutton" value="#{scouncilBundle['confirm']}"
			action="#{ScientificCouncilCurricularPlanManagement.deleteCurricularPlan}" onclick="return confirm('#{scouncilBundle['confirm.delete.curricularPlan']}')"/>
		<h:commandButton immediate="true" styleClass="inputbutton" value="#{scouncilBundle['cancel']}"
			action="curricularPlansManagement"/>
		<h:outputText value="</p>" escape="false"/>
	</h:form>

</ft:tilesView>