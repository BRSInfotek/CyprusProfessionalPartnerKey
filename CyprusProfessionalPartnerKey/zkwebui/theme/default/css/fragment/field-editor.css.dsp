
.number-box {
	display: inline-block; 
	white-space:nowrap;
}

.number-box .grid-editor-input.z-decimalbox {
}

.datetime-box {
	white-space:nowrap;
}
.datetime-box .z-datebox {
}
.datetime-box .z-timebox {
}

span.grid-combobox-editor {
	width: 100% !important;
	position: relative;
}

.grid-combobox-editor input {
	width: 100% !important;
	padding-right: 26px;
	border-bottom-right-radius: 6px;
	border-top-right-radius: 6px;
	border-right: 0px;
}

.grid-combobox-editor.z-combobox-disabled input {
	border-bottom-right-radius: 3px;
	border-top-right-radius: 3px;
	border-right: 1px solid #cfcfcf;
	padding-right: 5px;
}

.grid-combobox-editor .z-combobox-button {
	position: absolute;
	right: 0px;
	border-bottom-right-radius: 3px;
	border-top-right-radius: 3px;
	border-bottom-left-radius: 0px;
	border-top-left-radius: 0px;
}

.grid-combobox-editor input:focus {
	border-right: 0px;
}
	
.grid-combobox-editor input:focus + .z-combobox-button {
	border-left: 1px solid #0000ff;
}

.editor-input.z-combobox + .editor-button {
	background-color: #F7F7F7;
	width: 22px;
    height: 24px;
    min-height: 24px;
    right: 0px;
    top: 1px;
    border: 1px solid #CFCFCF;
    border-radius: 0;
    border-left: 1px solid transparent;
}
.editor-input.z-combobox > .z-combobox-input {
	border-bottom-right-radius: 0;
	border-top-right-radius: 0;
}

.editor-box .grid-editor-input.z-textbox {
}

.grid-editor-button {
}

.grid-editor-button img {
}

<%-- chart --%>
.chart-field {
	padding: 10px; 
	border: 1px solid lightgray !important;
}

.field-label {
	position: relative; 
	float: right;
}

.image-field {
	cursor: pointer;
	border: 1px solid #C5C5C5;
	height: 24px;
	width: 24px;
}
.image-field.image-field-readonly {
	cursor: default;
	border: none;
}
.image-fit-contain {
	object-fit: contain;
}
.z-cell.image-field-cell {
	z-index: 1;
}

.html-field {
	cursor: pointer;
	border: 1px solid #C5C5C5;
	overflow: auto;
}

.dashboard-field-panel.z-panel, .dashboard-field-panel.z-panel > .z-panel-body,  .dashboard-field-panel.z-panel > .z-panel-body > .z-panelchildren  {
	overflow: visible;
}

.cyprus-mandatory, .cyprus-mandatory input, .cyprus-mandatory a {
    border-color:red;
}
