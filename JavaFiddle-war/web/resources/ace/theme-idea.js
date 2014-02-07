ace.define('ace/theme/idea', ['require', 'exports', 'module' , 'ace/lib/dom'], function(require, exports, module) {

exports.isDark = false;
exports.cssClass = "ace-idea";
exports.cssText = "/* CSS style content from idea's default pygments highlighter template.\
Cursor and selection styles from textmate.css. */\
.ace-idea .ace_gutter {\
background: #e8e8e8;\
color: #AAA;\
}\
.ace-idea  {\
background: #fff;\
color: #000;\
}\
.ace-idea .ace_keyword {\
color: #000088;\
font-weight: bold;\
}\
.ace-idea .ace_string {\
color: #00a000;\
font-weight: bold;\
}\
.ace-idea .ace_variable.ace_class {\
color: teal;\
}\
.ace-idea .ace_constant.ace_numeric {\
color: #0000ff;\
}\
.ace-idea .ace_constant.ace_buildin {\
color: #0086B3;\
}\
.ace-idea .ace_support.ace_function {\
color: #0086B3;\
}\
.ace-idea .ace_comment {\
color: #808080;\
font-style: italic;\
}\
.ace-idea .ace_variable.ace_language  {\
color: #0086B3;\
}\
.ace-idea .ace_paren {\
font-weight: bold;\
}\
.ace-idea .ace_boolean {\
font-weight: bold;\
}\
.ace-idea .ace_string.ace_regexp {\
color: #009926;\
font-weight: normal;\
}\
.ace-idea .ace_variable.ace_instance {\
color: teal;\
}\
.ace-idea .ace_constant.ace_language {\
color: #000088;\
font-weight: bold;\
}\
.ace-idea .ace_cursor {\
color: black;\
}\
.ace-idea .ace_marker-layer .ace_active-line {\
background: #ffffd7;\
}\
.ace-idea .ace_marker-layer .ace_selection {\
background: rgb(181, 213, 255);\
color: white;\
}\
.ace-idea.ace_multiselect .ace_selection.ace_start {\
box-shadow: 0 0 3px 0px white;\
border-radius: 2px;\
}\
/* bold keywords cause cursor issues for some fonts */\
/* this disables bold style for editor and keeps for static highlighter */\
.ace-idea.ace_nobold .ace_line > span {\
font-weight: normal !important;\
}\
.ace-idea .ace_marker-layer .ace_step {\
background: rgb(252, 255, 0);\
}\
.ace-idea .ace_marker-layer .ace_stack {\
background: rgb(164, 229, 101);\
}\
.ace-idea .ace_marker-layer .ace_bracket {\
background: #99ccff;\
color: black;\
}\
.ace-idea .ace_gutter-active-line {\
background-color : rgba(0, 0, 0, 0.07);\
}\
.ace-idea .ace_marker-layer .ace_selected-word {\
background: #e4e4ff;\
}\
.ace-idea .ace_print-margin {\
width: 1px;\
background: #e8e8e8;\
}\
.ace-idea .ace_indent-guide {\
background: url(\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAACCAYAAACZgbYnAAAAE0lEQVQImWP4////f4bLly//BwAmVgd1/w11/gAAAABJRU5ErkJggg==\") right repeat-y;\
}";

    var dom = require("../lib/dom");
    dom.importCssString(exports.cssText, exports.cssClass);
});
