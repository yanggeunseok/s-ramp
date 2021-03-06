<%@page import="java.text.DateFormat"%>
<%@page import="org.overlord.sramp.ui.shared.beans.Version"%>
<%
	Version version = Version.getCurrentVersion();
	String buildDate = DateFormat.getDateTimeInstance().format(version.getDate());
%>
<!doctype html>
<html>
	<head>
	    <meta http-equiv="content-type" content="text/html; charset=UTF-8"></meta>
	    <link href="resources/css/reset.css" media="all" type="text/css" rel="stylesheet"></link>
	    <link href="resources/css/widgets.css?v=<%= version.getVersion() %>" media="all" type="text/css" rel="stylesheet"></link>
	    <link href="resources/css/dialogs.css?v=<%= version.getVersion() %>" media="all" type="text/css" rel="stylesheet"></link>
	    <link href="resources/css/views.css?v=<%= version.getVersion() %>" media="all" type="text/css" rel="stylesheet"></link>
	    <link href="resources/css/srampui.css?v=<%= version.getVersion() %>" media="all" type="text/css" rel="stylesheet"></link>
		<title>JBoss Overlord - S-RAMP Explorer (GWT)</title>
	    <script type="text/javascript" src="srampui/srampui.nocache.js"></script>
	</head>
	<body>
		<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
		<noscript>
			<div style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
				Your web browser must have JavaScript enabled
				in order for this application to display correctly.
			</div>
		</noscript>
		<div id="main">
			<div id="header">
				<h1>S-RAMP Explorer</h1>
			</div>
			<div id="breadcrumb-wrapper">
			</div>
			<div id="content-wrapper">
				<div id="content">
					<div id="app-loading">
						<h1>Application loading, please wait...</h1>
					</div>
				</div>
			</div>
			<div id="footer">
				<span>S-RAMP Explorer, Version <%= version.getVersion() %> (<em><%= buildDate %></em>)</span>
				<br />
				&copy; 2012 - <a href="http://www.jboss.org">JBoss</a> <a href="http://www.jboss.org/overlord">Overlord</a>
			</div>
		</div>
	</body>
</html>