$(document).ready(function(){
   setContentHeight();
});

function setContentHeight() {
    var MINSCREENWIDTH = 1300;
    var $header = $('#header');
    var $content = $('#content');
    var $tree = $("#treepanel");
    var $code = $("#codetext");
    var $codearea = $("#textarea");
    var $tabpanel = $("#tabpanel");
    var height = $(window).height() - $header.height();
    var width = $(window).width();
    
    /**
     * set size for main content
     */
    $content.height(height);
    $content.width(width);    
    
    /**
     * set position and size for left panel
     */
    $tree.height(height - 30);
    $tree.css("margin", "15px");
    
    /**
     * set position and size for right panel
     */
    $code.height(height - 30);
    $code.width(width - $tree.width() - 50);
    $code.css("margin", "15px 15px 15px 0");
    
    /**
     * set position and size of textarea
     */
    $codearea.height($code.height() - 20);
    $codearea.width($code.width() - 20);
    $codearea.css("margin", "10px 0 0 10px");
    
    /**
     * set position of tabpanel
     */
    var sizer = parseInt($(".CodeMirror-sizer").css("margin-left"));
    var margin = ($(window).width()) < MINSCREENWIDTH ? 340 : (35 + $tree.width() + sizer);
    $tabpanel.css("margin-left", (margin - 340) + "px");
    $tabpanel.width($(window).width() - margin - 50);
}

function closeTab(parent) {
    parent.remove();
}

function selectTab(li) {
    $tabs = $("#tabpanel");
    $tabs.find(".active").removeClass("active");
    li.addClass("active");
}

function showPopup(content) {
    var html = "<div id=\"opaco\" class=\"hidden\"></div>";
    html += "<div id=\"popup\" class=\"hidden\"></div>";
    html += "<div id=\"popup_bug\" class=\"hidden\">";
    html += "<div class=\"bug\">";
    html += (content === "" || content === null) ? "" : content;
    html += "</div>";
    html += "</div>";
    var div = document.createElement('div');
    div.innerHTML = html;
    $("body").append(div);
    $('#popup_bug').togglePopup();
}

