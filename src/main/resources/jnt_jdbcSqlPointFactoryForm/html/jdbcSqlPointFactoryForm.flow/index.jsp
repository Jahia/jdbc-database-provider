<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<%--@elvariable id="mailSettings" type="org.jahia.services.mail.MailSettings"--%>
<%--@elvariable id="flowRequestContext" type="org.springframework.webflow.execution.RequestContext"--%>
<%--@elvariable id="jdbcFactory" type="org.jahia.modules.jdbcDatabaseProvider.admin.JDBCMountPointFactory"--%>
<template:addResources type="javascript" resources="admin/angular.min.js"/>
<template:addResources type="javascript" resources="admin/app/folderPicker.js"/>
<template:addResources type="css" resources="admin/app/folderPicker.css"/>

<div class="folderPickerApp" ng-app="folderPicker">
    <h2><fmt:message key="jdbcSqlFactory"/></h2>
    <%@ include file="errors.jspf" %>

    <fmt:message var="selectTarget" key="jdbcSqlFactory.selectTarget"/>
    <c:set var="i18NSelectTarget" value="${functions:escapeJavaScript(selectTarget)}"/>
    <div class="box-1" ng-controller="folderPickerCtrl" ng-init='init(${localFolders}, "${jdbcFactory.localPath}", "localPath", true, "${i18NSelectTarget}")'>
        <form:form modelAttribute="jdbcFactory" method="post">
            <fieldset title="local">
                <div class="container-fluid">

                    <div class="row-fluid">
                        <form:label path="name"><fmt:message key="jdbcSqlFactory.name"/> <span style="color: red">*</span></form:label>
                        <form:input path="name"/>
                    </div>

                    <div class="row-fluid">
                        <form:label path="url"><fmt:message key="jdbcSqlFactory.url"/> <span style="color: red">*</span></form:label>
                        <form:input path="url"/>
                    </div>

                    <div class="row-fluid">
                        <form:label path="user"><fmt:message key="jdbcSqlFactory.user"/></form:label>
                        <form:input path="user"/>
                    </div>

                    <div class="row-fluid">
                        <form:label path="password"><fmt:message key="jdbcSqlFactory.password"/></form:label>
                        <form:password path="password" showPassword="true"/>
                    </div>

                    <div class="row-fluid">
                        <jsp:include page="/modules/external-provider/angular/folderPicker.jsp"/>
                    </div>
                </div>
            </fieldset>
            <fieldset>
                <div class="container-fluid">
                    <button class="btn btn-primary" type="submit" name="_eventId_save">
                        <span><fmt:message key="label.save"/></span>
                    </button>
                    <button class="btn" type="submit" name="_eventId_cancel">
                        <span><fmt:message key="label.cancel"/></span>
                    </button>
                </div>
            </fieldset>
        </form:form>
    </div>
</div>
