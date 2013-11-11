var PATH = "";
var javaEditor;

$(document).ready(function(){
   setContentHeight();
   buildTree();
   loadTabs();
   loadContent();
   $("body").click(function() {
       closeAllPopUps();
   });
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
    removeTab(parent);
    parent.remove();
}

function selectTab(li) {
    $tabs = $("#tabpanel");
    $tabs.find(".active").removeClass("active");
    li.addClass("active");
    var id = li.attr("id");
    setCurrentFileID(id);
    getCurrentFileText(id);
}

function showPopup(content, params) {
    var options = {
        width: 300,
        height: 300
    };
    if(arguments.length > 1) {
        options = params;
    }
    var html = "<div id=\"opaco\" class=\"hidden\"></div>";
    html += "<div id=\"popup\" class=\"hidden\"></div>";
    html += "<div id=\"popup_bug\" class=\"hidden\">";
    html += "<div class=\"bug\">";
    if( Object.prototype.toString.call(content) === '[object String]' ) {
        html += (content === "" || content === null) ? "" : content;
    }
    html += "</div>";
    html += "</div>";
    
    $div = $('<div/>', {
        html: html
    });
    
    $("body").append($div);
    $("#popup").attr("style", "height: " + options.height + "px !important; width: " + options.width + "px !important");
    if( Object.prototype.toString.call(content) === '[object Object]' ) {
        $("#popup_bug .bug").html("");
        $("#popup_bug .bug").append(content);
    }
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
    $.fn.alignCenter = function() {
       var marginLeft =  - $(this).width()/2 + 'px';
       var marginTop =  - $(this).height()/2 + 'px';
       return $(this).css({'margin-left':marginLeft, 'margin-top':marginTop});
    };

    $.fn.togglePopup = function(){
      if($('#popup').hasClass('hidden'))
      {
        $('#opaco').height($(document).height()).toggleClass('hidden').fadeTo('slow', 0.7)
                     .click(function(){$(this).togglePopup();});

        $('#popup')
          .html($(this).html())
          .alignCenter()
          .toggleClass('hidden');
      }
      else
      {
        $('#opaco').toggleClass('hidden').removeAttr('style').unbind('click');
        $('#popup').toggleClass('hidden');
      }
    };
}

function loadMainMenu() {
    $menu = $("#main_menu");
    $('#main_menu div.link').each(function() {
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
    
    $menu.click(function(event){
        $("#context_menu").remove();
        event.stopPropagation();
    });
}

function loadContent() {
    loadProjectTree();
    loadMainMenu();
    loadTreeOperation();
    loadToogles();
}

function openJavaClass($el) {
    var id = $el.closest('li').attr('id') + "_" + $el.text();
    var name = $el.text();
    var cl = $el.attr('class');
    
    setCurrentFileID(id);
    getCurrentFileText(id);
    
    var li;
    if(!isOpened(id))
        li = addTabToPanel(id, name, cl);
    else {
        li = document.getElementById(id);
        li = $("#tabpanel").find(li);
    }
    selectTab(li);
    
}

function getCurrentFileText(id) {
    $.ajax({
        url: PATH + '/webapi/revisions/classfile',
        type: 'GET',
        dataType: "json",
        data: {id: id},
        async: false,
        success: function(data) {
            javaEditor.setValue(data);
        }
    });
}

function addTabToPanel(id, name, cl) {
    addTab(id);
    var li = $('<li id="'+ id +'" class="'+ cl +' active" onclick="selectTab($(this))">'+ name +'<div class="close" onclick="closeTab($(this).parent())"></div></li>');
    $("#tabpanel").append(li);
    return li;
}

function getCurrentFileID() {
    var id = "";
    $.ajax({
        url: PATH + '/webapi/current/openedfileid',
        type:'GET',
        dataType: "text",
        async: false,
        success: function(data) {
            id = data;
        }
    }); 
    return id;
}

function setCurrentFileID(id) {
    $.ajax({
        url: PATH + '/webapi/current/openedfileid',
        type: 'POST',
        data: JSON.stringify(id),
        contentType: "application/json",
        async: false,
        success: function() {

        }
    }); 
}

function loadTabs() {
    $.ajax({
        url: PATH + '/webapi/current/openedtabs',
        type: 'GET',
        contentType: "application/json",
        success: function(data) {
            for(var i = 0; i < data.length; i++) {
                var file = getFileDataById(data[i]);
                var cl = file["type"];
                var name = file["name"];
                var li = $('<li id="'+ data[i] +'" class="'+ cl +'" onclick="selectTab($(this))">'+ name +'<div class="close" onclick="closeTab($(this).parent())"></div></li>');
                $("#tabpanel").append(li);
            }
        }
    }); 
}

function getFileDataById(id) {
    var filedata;

    $.ajax({
        url: PATH + '/webapi/tree/filedata',
        type: 'GET',
        data: {id: id},
        dataType: "json",
        async: false,
        success: function(data) {
            filedata = data;
        }
    }); 
    
    return filedata;
}

function isOpened(id) {
    var result = false;
    
    $.ajax({
        url: PATH + '/webapi/current/opened',
        type: 'GET',
        data: {id: id},
        dataType: "json",
        async: false,
        success: function(data) {
            result = data;
        }
    });  
    
    return result;
}

function addTab(id) {
    $.ajax({
        url: PATH + '/webapi/current/openedtabs',
        type: 'POST',
        data: JSON.stringify(id),
        contentType: "application/json",
        success: function() {
             
        }
    }); 
}

function removeTab(li) {
    var id = li.attr("id");
    
    $.ajax({
        url: PATH + '/webapi/current/remove',
        type: 'POST',
        data: JSON.stringify(id),
        contentType: "application/json",
        success: function() {
             
        }
    }); 
}

function loadTreeOperation() {
    $("#tree a").each(function() {
        this.oncontextmenu = function() {return false;};  
        $(this).mousedown(function(event) {
            var classname = $(this).attr("class").split(" ")[0];
            switch(classname) {
                case "class": 
                case "interface": 
                case "exception": 
                case"annotation": 
                case "runnable":
                    switch(event.which) {
                        case 1:
                            openJavaClass($(this));
                            break;
                        case 3: 
                            showContextMenu($(this), event, "file");
                            break;
                    }
                    break;
                case "package":
                    switch(event.which) {
                        case 3: 
                            showContextMenu($(this), event, "package");
                            break;
                    }
                    break;
                case "root":
                    switch(event.which) {
                        case 3: 
                            showContextMenu($(this), event, "root");
                            break;
                    }
                    break;
            }
        });
    });
}

function showContextMenu($el, event, classname) {
    $("#context_menu").remove();
    var elementname = $el.html();
    var id = $el.parent().attr("id");
    var x = event.clientX;
    var y = event.clientY;
    var context_menu_id = "context_menu";
    $ul = $('<ul/>', {
        id: context_menu_id
    });
    $ul.css('margin-left', x + 'px');
    $ul.css('margin-top', y + 'px');
    $ul.appendTo('body');
   
    switch(classname) {
        case "file":
            $li = $('<li/>', {text: 'Открыть'});
            $li.click(function() {
                openJavaClass($el);
                $ul.remove();
            });
            $li.appendTo($ul);
            
            $('<li/>', {text: 'Переименовать'}).appendTo($ul);
            
            $li = $('<li/>', {text: 'Удалить'});
            $li.click(function() {
                deleteFromProject(id, classname, elementname);
                $ul.remove();
            });
            $li.appendTo($ul);
            break;
        case "package":
            $('<li/>', {text: 'Добавить...'}).appendTo($ul);
            $('<li/>', {text: 'Переименовать'}).appendTo($ul);
            
            $li = $('<li/>', {text: 'Удалить'});
            $li.click(function() {
                deleteFromProject(id, classname, elementname);
                $ul.remove();
            });
            $li.appendTo($ul);
            break;
        case "root":
            $('<li/>', {text: 'Запустить'}).appendTo($ul);
            $('<li/>', {text: 'Переименовать'}).appendTo($ul);
            $('<li/>', {text: 'Настройки проекта'}).appendTo($ul);
            $li = $('<li/>', {text: 'Удалить'});
            $li.click(function() {
                deleteFromProject(id, classname, elementname);
                $ul.remove();
            });
            $li.appendTo($ul);
            break;
    }
    $("#context_menu").click(function(event) {
        event.stopPropagation();
    });
}

function closeAllPopUps() {
    $("#context_menu").remove();
    $("#main_menu").find('.active').removeClass("active");
    $("#main_menu").find('.show').removeClass('show'); 
}

function deleteFromProject(id, classname, name) {
    $div = drawPopup(id, classname, name);
    
    var params = {
        width: 500,
        height: 150
    };
    showPopup($div, params);
}

function drawPopup(id, classname, name) {
    var cn = "";
    switch(classname) {
        case "file":
            cn = "файл";
            break;
        case "package":
            cn = "пакет";
            break;
        case "root":
            cn = "проект";
            break;
    }
    $div = $('<div/>', {
        id: "delete_confirm"
    });
    $div.append("<span class='title'>Вы уверены, что хотите удалить " + cn + " " + name + "?</span>");
    
    $buttons = $('<div/>', {
        class: "buttons"
    });
    
    $confirm = $('<div/>', {
        text: "Подтвердить",
        id: "confirm",
        class: "button"
    });
    $confirm.attr("onclick", "removeFromProject('"+ id +"')");
    
    $decline = $('<div/>', {
        text: "Отмена",
        id: "decline",
        class: "button"
    });
    $decline.attr("onclick", "$('#popup_bug').togglePopup();");
    
    $buttons.append($confirm);
    $buttons.append($decline);
    
    $div.append($buttons);
    return $div;
}

function removeFromProject(id) {
    var idString = String(id);
    $.ajax({
        url: PATH + '/webapi/tree/remove',
        type: 'POST',
        data: idString,
        contentType: "application/json",
        success: function() {
            var removeId = "#" + idString;
            $(removeId).remove();
            $("#context_menu").remove();
            $('#popup_bug').togglePopup(); 
        }
    });     
}
