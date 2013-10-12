$(document).ready(function(){
   setContentHeight();
});

function setContentHeight() {
    var $header = $('#header');
    var $content = $('#content');
    var $tree = $("#treepanel");
    var $code = $("#codetext");
    var $codearea = $("#textarea");
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
    
}

