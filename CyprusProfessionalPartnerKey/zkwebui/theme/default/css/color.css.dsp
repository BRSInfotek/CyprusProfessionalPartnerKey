<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ page contentType="text/css;charset=UTF-8" %>

<%-- Words --%>
<c:set var="Color01" value="#403E39"/>
<%-- Menu Background --%>
<c:set var="Color02" value="#EEEDEB"/>
<%-- Group Background --%>
<c:set var="Color03" value="#F3F3F1"/>
<%-- Tab Border --%>
<c:set var="Color04" value="#ABA799"/>
<%-- Input Border --%>
<c:set var="Color05" value="#FFFFFF"/>
<%-- Selection --%>
<c:set var="Color06" value="#D1CFCA"/>
<%-- Zebra Color --%>
<c:set var="Color07" value="#F9F9F8"/>
<%-- Group Bottom Line --%>
<c:set var="Color08" value="#CDCDCD"/>
<%-- Hover+Select--%>
<c:set var="Color09" value="#B2AEA6"/>
<%-- Hover --%>
<c:set var="Color10" value="#D6D6D6"/>

<c:set var="ColorWhite" value="#FFFFFF"/>
<c:set var="ColorBGTree" value="#F7F7F7"/>
<c:set var="ColorSeld" value="#0068c5"/>
<c:set var="ColorGray" value="#FFFFFF"/>
<c:set var="ColorLightGray" value="#F0F0F0"/>
<c:set var="ColorBorder" value="#999999"/>
<c:set var="aColor" value="#debe09"/>

<%-- OpenUp Ltda. Inicio --%>
<%-- Window Font --%>
<c:set var="WindowFont" value="'Open Sans', sans-serif, Verdana, Arial, Helvetica;"/>
<%-- OpenUp Ltda. Fin --%>


@import url('https://fonts.googleapis.com/css?family=Open+Sans');

 .z-textbox {
}

.z-textbox {
}

.z-column:not(.z-column-over) {
  background-color: #EBEBEB !important;
}

.z-border-layout {
}
<%-- ERPCyA end --%>


.z-textbox {
}

.z-textbox-focus, .z-textbox-focus input, {

}

div.z-tree, div.z-dottree, div.z-filetree, div.z-vfiletree {
	border:none;
}

div.z-tree-body{
	border:none;
}


tr.z-tree-row td.z-tree-row-focus tr.z-tree-row-text {
	
}

tr.z-tree-row-seld tr.z-tree-row-text {

}

tr.z-tree-row-over tr.z-tree-row-text{

}

tr.z-tree-row-over-seld tr.z-tree-row-text{
	
}


div.z-tree-header th.z-tree-col, div.z-tree-header th.z-auxheader,
div.z-dottree-header th, div.z-filetree-header th, div.z-vfiletree-header th {
	border:none;
}
tr.z-listbox-odd {
}

tr.z-list-item-focus, div.z-listcell-cnt-text{
	
}

tr.z-list-item-seld, div.z-listcell-cnt-text{

}
tr.z-list-item-over,  div.z-listcell-cnt-text{
	
}

tr.z-list-item-over-seld,  div.z-listcell-cnt-text{
	
}

div.z-listbox-header th.z-list-header, div.z-listbox-header th.z-auxheader {
	border:none;
}
div.z-listbox {
	background-color : ${Color06};
	border:none;
}
div.z-listbox-footer {
	background-color : ${Color06};
	border:none;
}
tr.cells td {
	border-bottom-color:${Color04};
	border:none;
}
div.z-grid {
	background-color:${ColorGray};
	border:none;

	-moz-box-shadow: 1px 1px 4px gray;
	-webkit-box-shadow: 1px 1px 4px gray;
	box-shadow: 1px 1px 4px #F0F0F0;
}

div.z-grid-body {
	-moz-box-shadow: 0 0 5px #888;
	-webkit-box-shadow: 0 0 5px #888;
	box-shadow: 0 0 5px #888;
	background-color:${ColorGray};
	border:none;
}

tr.z-grid-odd td.z-row-inner, tr.z-grid-odd {
	background-color : ${ColorLightGray};
}

tr.z-grid-odd .z-cell{
	background-color : ${ColorLightGray};
}


tr.z-row td.z-row-inner{
	background-color : ${ColorGray};
	margin-left:4px;
	margin-right:4px;
	<%-- border-bottom: 1px solid ${ColorBGTree}; --%>
	border-left: 0;
	<%-- border-right: 1px solid ${ColorBGTree}; --%>
}

div.z-row-cnt{
	margin-right:4px;
}

<%--
tr.z-rows{
	background-color : ${ColorGray};
}
--%>

div.z-grid-header th.z-column, div.z-grid-header th.z-auxheader {
	border:none;
}
td.z-list-group-inner div.z-list-cell-cnt {
	color : ${Color01};
}
td.z-list-group-inner {
	border-top-color: ${Color04};
	border-bottom-color:${Color08};
	border:none;
}
td.z-list-group-foot-inner div.z-list-cell-cnt {
	color : ${Color01};
}
tr.z-list-group {
	background-color: ${Color03};
}
tr.z-group {
	background-color: ${Color03};
}
td.z-group-inner {
}
.z-group-inner .z-group-cnt span, .z-group-inner .z-group-cnt {
	color : ${Color01};
}
.z-group-foot-inner .z-group-foot-cnt span, .z-group-foot-inner .z-group-foot-cnt {
	color : ${Color01};
}
.z-paging {
	border:none;
}
.z-paging-inp {
	border:none;
}
.z-paging-os .z-paging-os-cnt {
	background-color: ${Color09};
	border:none;
	color: ${Color01};
}
.z-paging-os .z-paging-os-seld:hover {
	
}

.z-paging-info, .z-paging-text{
    
}


