$(document).ready(function(){
   setContentHeight();
   loadContent();
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

function loadProjectTree() {
    (function ($) {
        $.fn.liHarmonica = function (params) {
            var p = $.extend({
                currentClass: 'cur',    //Класс для выделенного пункта меню
                onlyOne: true,          //true - открытым может быть только один пункт, 
                                        //false - число открытых одновременно пунктов не ограничено
                speed: 500              //Скорость анимации
            }, params);
            return this.each(function () {

                var el = $(this).addClass('harmonica'), linkItem = $('ul', el).prev('a');

                el.children(':last').addClass('last');
                $('ul', el).each(function () {
                    $(this).children(':last').addClass('last');
                    el.find('.open').children('ul').addClass('opened');
                });

                $('ul', el).prev('a').addClass('harFull');


                el.find('.' + p.currentClass).parents('ul').show().prev('a').addClass(p.currentClass).addClass('harOpen');

                linkItem.on('click', function () {
                    if ($(this).next('ul').is(':hidden')) {
                        $(this).addClass('harOpen');
                    } else {
                        $(this).removeClass('harOpen');
                    }
                    if (p.onlyOne) {
                        $(this).closest('ul').closest('ul').find('ul').not($(this).next('ul')).slideUp(p.speed).prev('a').removeClass('harOpen');
                        $(this).next('ul').slideToggle(p.speed);
                    } else {
                        $(this).next('ul').stop(true).slideToggle(p.speed);
                    }
                    return false;
                });
            });
        };
    })(jQuery);
}

function loadToogles() {
    //additional properties for jQuery object
    //align element in the middle of the screen
    $.fn.alignCenter = function() {
       //get margin left
       var marginLeft =  - $(this).width()/2 + 'px';
       //get margin top
       var marginTop =  - $(this).height()/2 + 'px';
       //return updated element
       return $(this).css({'margin-left':marginLeft, 'margin-top':marginTop});
    };

    $.fn.togglePopup = function(){
      //detect whether popup is visible or not
      if($('#popup').hasClass('hidden'))
      {
        //hidden - then display
        //when IE - fade immediately
 //       if($.browser.msie)
 //       {
 //         $('#opaco').height($(document).height()).toggleClass('hidden')
 //                    .click(function(){$(this).togglePopup();});
 //       }
 //       else
 //       //in all the rest browsers - fade slowly
 //       {
 //         $('#opaco').height($(document).height()).toggleClass('hidden').fadeTo('slow', 0.7)
 //                    .click(function(){$(this).togglePopup();});
 //       }
        $('#opaco').height($(document).height()).toggleClass('hidden').fadeTo('slow', 0.7)
                     .click(function(){$(this).togglePopup();});

        $('#popup')
          .html($(this).html())
          .alignCenter()
          .toggleClass('hidden');
      }
      else
      {
        //visible - then hide
        $('#opaco').toggleClass('hidden').removeAttr('style').unbind('click');
        $('#popup').toggleClass('hidden');
      }
    };
}

function loadMainMenu() {
    $menu = $("#main_menu");
    $('div.link').each(function() {
        $parent = $(this).parent();
        if($parent.children().length > 1) {
            $(this).click(function() {  
                $li = $(this).parent();
                if($li.hasClass('active')) {
                    $li.find('ul').removeClass('show');
                    $li.removeClass('active');
                } else {
                    $menu.find('.active').removeClass("active");
                    $menu.find('.show').removeClass('show');
                    $li.addClass('active');
                    $li.find('ul').addClass('show');
                }
            });
        }
    });
}

function loadContent() {
    loadProjectTree();
    loadMainMenu();
    loadToogles();
}

