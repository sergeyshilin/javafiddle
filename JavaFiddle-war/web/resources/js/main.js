var PATH = "";
var javaEditor;
var PROJECTPARAM;


// EVENTS

$(document).ready(function(){
    loadMainMenu();
    loadLiHarmonica();
    var projecthash = PROJECTPARAM;
    if(projecthash !== "") {
        getProject(projecthash);
        invalidateSession();
    }
    buildTree();
    loadTabs();
    loadToggles();
    $("body").click(function() {
        closeAllPopUps();
    });
    setObjectsSize();
}); 

$(window).resize(function() {
    setObjectsSize();
});

$("#codetext").resize(function() {
    setObjectsSize();
});

window.onbeforeunload = (function() {
    if (isEmpty('modified'))
        return;
    addCurrentFileText();
    return "ВНИМАНИЕ! В проекте есть несохраненные файлы. Когда сессия истечет, все несохраненные изменения будут потеряны!";
});

function setObjectsSize() { 
    javaEditor.setSize(null, $("#codetext").height());
}

// MENU

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


// TABS

function loadTabs() {
    var opened = getCurrentFileID();
    openedTabs().forEach(function(entry) {
        var file = getFileDataById(entry);
        if (file !== false) {
            var cl = file["type"];
            var name = file["name"];
            var li = addTabToPanel(entry, name, cl);
            if (entry == opened)
                selectTab(li);
            if (isModified(entry))
                $("#" + entry).addClass("modified");
        } else {
            var cl = "notfound";
            var name = "not_found";
            var li = addTabToPanel(entry, name, cl);
            if (entry == opened)
                selectTab(li);
        }
            
    });
}

function openTabFromTree($el) {
    var id = $el.closest('li').attr('id') + "_tab";
    var name = $el.text();
    var cl = $el.attr('class');
    
    var li;
    if(!isOpened(id))
        li = addTabToPanel(id, name, cl);
    else {
        li = document.getElementById(id);
        li = $("#tabpanel").find(li);
    }
    selectTab(li);
}

function addTabToPanel(id, name, cl) {
    pushOpenedTab(id);
    
    var li = $('<li id="'+ id +'" class="'+ cl +'" onclick="selectTab($(this))">'+ name +'<div class="close" onclick="closeTab($(this).parent())"></div></li>');
    $("#tabpanel").append(li);
    return li;
}

function selectTab(li) {
    var id = li.attr("id");
    
    if (openedTabs().indexOf(id) === -1)
        return false;
    
    if (isCurrent(id) && $("#tabpanel").find(".active") === li)
        return false;

    if (openedTabs().indexOf($("#tabpanel").find(".active").attr("id")) > -1)
        addCurrentFileText();
    
    $("#tabpanel").find(".active").removeClass("active");
    li.addClass("active");
    setCurrentFileID(id);
    getReadOnly(id);
    getCurrentFileText();
    changeLastUpdateLabel();
    javaEditor.focus();
}

function selectEmptyTab() {
    javaEditor.setValue("");
    javaEditor.clearHistory();
    javaEditor.setOption("readOnly", true);  
    $('#latest_update').text("");
    
}

function closeTab(parent) {
    var id = parent.attr("id");
    closeTabInStorage(id);
    parent.remove();
    setCurrentFileID("");
    
    id = getLastOpenedTab();
    if (id !== null) {
        var li = document.getElementById(id);
        li = $("#tabpanel").find(li);
        selectTab(li);
    } else {
        selectEmptyTab();
    }
}

function changeLastUpdateLabel() {
    var time = getCurrentFileTimeStamp();
    if (time == "")
        $('#latest_update').text("Последнее изменение: еще не сохранялось");
    else {
        moment.lang('ru');
        var earlier = moment(time);
        $('#latest_update').text("Последнее изменение: " + earlier.from(moment()));
    }
}

function closeTabsFromPackage(id) {
    $pack = $('#' + id);
    $pack.children("ul").children("li").each(function() {
        var subid = $(this).attr("id");
        if($(this).children("a").hasClass("project"))
            closeTabsFromPackage(subid);
        else 
            closeTab($('#' + subid + '_tab'));
    });
}

function closeAllTabs() {
    $panel = $("#tabpanel");
    $panel.children("li").each(function() {
       closeTab($(this));
    });
}


// TREE

function buildTree() {
    $("#tree").empty();
    $.ajax({
        url: PATH + '/webapi/tree',
        type: 'GET',
        dataType: "json",
        async: false,
        success: function(data) {
            for (var i = 0; i < data.projects.length; i++) {
                var proj = data.projects[i];
                $('#tree').append('<li id = "node_' + proj.id + '" class="open"><a href="#" class="root">' + proj.name + '</a><ul id ="node_' + proj.id + '_src"\></li>');
                $('#node_' + proj.id + '_src').append('<li id = "node_' + proj.id + '_srcfolder" class="open"><a href="#" class="sources">src</a><ul id ="node_' + proj.id + '_list"\></li>');
                $("#projectname").text(proj.name);
                setProjectId(proj.id);
                for (var j = 0; j < proj.packages.length; j++) {
                    var pck = proj.packages[j];
                    if (!(pck.name == "!default_package"))
                        $('#node_' + pck.parentId + '_list').append('<li id = "node_' + pck.id + '"><a href="#" class="package">' + pck.name + '</a><ul id ="node_' + pck.id + '_list"\></li>');
                }   
                for (var j = 0; j < proj.packages.length; j++) {
                    var pack = proj.packages[j];
                    for (var k = 0; k < pack.files.length; k++) {
                        var file = pack.files[k];
                        if (pack.name == "!default_package")
                            $('#node_' + proj.id + '_list').append('<li id = "node_' + file.id + '"><a href="#" class="' + file.type + '">' + file.name + '</a></li>');
                        else
                            $('#node_' + pack.id + '_list').append('<li id = "node_' + file.id + '"><a href="#" class="' + file.type + '">' + file.name + '</a></li>');
                     }
                }   
            }
            $(function () {
                $('#tree').liHarmonica({
                    onlyOne: false,
                    speed: 100
                });
            });
            openedNodesList().forEach(function(entry) {
                $("#" + entry).children('a').addClass('harOpen');
                $("#" + entry).children('ul').addClass('opened');
            });
            loadTreeOperation();
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
                case "annotation": 
                case "runnable":
                case "enum":
                    switch(event.which) {
                        case 1:
                            openTabFromTree($(this));
                            break;
                        case 3: 
                            showContextMenu($(this), event, "file");
                            break;
                    }
                    break;
                case "package":
                    switch(event.which) {
                        case 1:
                            changeNodeState($(this));
                            break;                        
                        case 3: 
                            showContextMenu($(this), event, "package");
                            break;
                    }
                    break;
                case "sources":
                    switch(event.which) {  
                        case 1:
                            changeNodeState($(this));
                            break;   
                        case 3: 
                            showContextMenu($(this), event, "sources");
                            break;
                    }
                    break;
                case "root":
                    switch(event.which) {
                        case 1:
                            changeNodeState($(this));
                            break;   
                        case 3: 
                            showContextMenu($(this), event, "root");
                            break;
                    }
                    break;
            }
        });
    });
}

function loadLiHarmonica() {
    (function ($) {
        $.fn.liHarmonica = function (params) {
            var p = $.extend({
                currentClass: 'cur',
                onlyOne: false,
                speed: 100
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

function addPackage(id) {
    $input = $("#addpackage input");
    var name = $input.val().toLowerCase();
    var dotname = name;
    
    if(!name.endsWith(".")) {
        dotname = name + ".";
    }
    
    if(isRightPackageName(dotname, id)) {
        $.ajax({
            url: PATH + '/webapi/tree/addPackage',
            type: 'POST',
            data: {project_id: id, name: name},
            contentType: "application/x-www-form-urlencoded",
            success: function() {
                $('#popup_bug').togglePopup(); 
                buildTree();
            }
        });  
    }
 
}

function addFile(id) {
    $input = $("#addfile input#class");
    var name = $input.val();
    var type = $("#addfile #filetype").attr("class");

    if(name.endsWith(".java")) {
        name = name.substring(0, name.length - 5);
    }
    
    if(isRightClassName(name, id)) {
        $.ajax({
            url: PATH + '/webapi/tree/addFile',
            type: 'POST',
            data: {package_id: id, name: name, type: type},
            contentType: "application/x-www-form-urlencoded",
            success: function(data) {
                $('#popup_bug').togglePopup(); 
                var li = addTabToPanel("node_"+data+"_tab", (name.endsWith(".java") ? name  : name + ".java"), type);
                selectTab(li);
                buildTree();
            }
        });   
    }
}

function renameElement(id, type) {
    var name = $("#rename input").val();
    $input = $("#rename input");
    $input.val(name);
    var correct = false;
    
    switch (type) {
        case "file":
            if(name.endsWith(".java"))
                name = name.substring(0, name.length - 5);
            correct = isRightClassName(name, id);
            break;
        case "package":
            var name = $input.val().toLowerCase();
            var dotname = name;
            if(!name.endsWith(".")) {
                dotname = name + ".";
            }
            correct = isRightPackageName(dotname, id);
            break;
        case "root":
            correct = isRightProjectName(name);
            break;
        default:
            break;
    }
    
    if(correct) {
        $.ajax({
            url: PATH + '/webapi/tree/rename',
            type: 'POST',
            data: {id: id, name: name, type: type},
            contentType: "application/x-www-form-urlencoded",
            success: function() {
                $('#popup_bug').togglePopup(); 
                buildTree();
            }
        }); 
    }
 
}

function removeFromProject(id, classname) {
    $.ajax({
        url: PATH + '/webapi/tree/remove',
        type: 'POST',
        data: id,
        contentType: "application/json",
        success: function() {
            switch(classname) {
                case "root":
                    $('#' + id).remove();
                    closeAllTabs();
                    invalidateSession();
                    buildTree();
                    break;
                case "package":
                    closeTabsFromPackage(id);
                    $('#' + id).remove();
                    break;
                case "file":
                    $('#' + id).remove();
                    closeTab($('#' + id + '_tab'));
                    break;
            }

            $("#context_menu").remove();
            $('#popup_bug').togglePopup(); 
        }
    });     
}

function closeProject() {
    var id = getProjectId();
    var name = $('#' + id).children("a").text();
    deleteFromProject(id, "root", name);
}

function toggleProjectTreePanel($li) {
    closeAllPopUps();
    if($li.hasClass('closed')) {
        $("#treepanel").css("width", "20%");
        $("#treepanel").css("display", "block");
        $li.removeClass('closed');
        $li.text("Скрыть дерево проекта");
        setObjectsSize();
    } else {
        $li.addClass('closed');
        $("#treepanel").width(0);
        $("#treepanel").css("display", "none");
        $li.text("Отобразить дерево проекта");
    }  
    setObjectsSize();
}


// TOGGLES

function loadToggles() {
    $.fn.alignCenter = function() {
       var marginLeft =  - $(this).width()/2 + 'px';
       var marginTop =  - $(this).height()/2 + 'px';
       return $(this).css({'margin-left':marginLeft, 'margin-top':marginTop});
    };

    $.fn.togglePopup = function(){
        if($('#popup').hasClass('hidden')) {
            $("#popup").empty();
            $('#opaco').height($(document).height()).toggleClass('hidden').fadeTo('slow', 0.7)
                     .click(function(){$(this).togglePopup();});

            $('#popup')
              .html($(this).html())
              .alignCenter()
              .toggleClass('hidden');
        } else  {
            $('#opaco').toggleClass('hidden').removeAttr('style').unbind('click');
            $('#popup').toggleClass('hidden');
        }
    };
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
                openTabFromTree($el);
                $ul.remove();
            });
            $li.appendTo($ul);
            
            $li = $('<li/>', {text: 'Переименовать'});
            $li.click(function() {
                showRenameWindow(id, classname, elementname);
                $ul.remove();
            });
            $li.appendTo($ul);
            
            $li = $('<li/>', {text: 'Удалить'});
            $li.click(function() {
                deleteFromProject(id, classname, elementname);
                
                $ul.remove();
            });
            $li.appendTo($ul);
            break;
        case "package":
            $li = $('<li/>', {text: 'Добавить...'});
            $li.click(function() {
                showAddFileWindow(id);
                $ul.remove();
            });
            $li.appendTo($ul);
            
            $li = $('<li/>', {text: 'Переименовать'});
            $li.click(function() {
                showRenameWindow(id, classname, elementname);
                $ul.remove();
            });
            $li.appendTo($ul);
            
            $li = $('<li/>', {text: 'Удалить'});
            $li.click(function() {
                deleteFromProject(id, classname, elementname);
                $ul.remove();
            });
            $li.appendTo($ul);
            break;
        case "sources":
            var project_id = id.substring(0, id.length - 10);
            $li = $('<li/>', {text: 'Добавить пакет'});
            $li.click(function() {
                showAddPackageWindow(project_id); 
                $ul.remove();
            });
            $li.appendTo($ul);
            $li = $('<li/>', {text: 'Добавить файл'});
            $li.click(function() {
                showAddFileWindow(id); 
                $ul.remove();
            });
            $li.appendTo($ul);
            break;
        case "root":
            $li = $('<li/>', {text: 'Запустить'});
            $li.click(function() {
                compileAndRun();
                $ul.remove();
            });
            $li.appendTo($ul);
            $li = $('<li/>', {text: 'Переименовать'});
            $li.click(function() {
                showRenameWindow(id, classname, elementname);
                $ul.remove();
            });
            $li.appendTo($ul);
            $li = $('<li/>', {text: 'Добавить пакет'});
            $li.click(function() {
                showAddPackageWindow(id); 
                $ul.remove();
            });
            $li.appendTo($ul);
            $('<li/>', {text: 'Настройки проекта'}).appendTo($ul);
            $li = $('<li/>', {text: 'Закрыть'});
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
    $div = drawDeleteWindow(id, classname, name);
    
    var params = {
        width: 500,
        height: 150
    };
    showPopup($div, params);
}


// WINDOWS

function drawDeleteWindow(id, classname, name) {
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
        id: "confirm-buttons"
    });
    
    $confirm = $('<div/>', {
        text: "Подтвердить",
        id: "confirm",
        class: "button"
    });
    $confirm.attr("onclick", "removeFromProject('"+ id +"', '"+ classname +"')");
    
    $decline = $('<div/>', {
        text: "Отменить",
        id: "decline",
        class: "button"
    });
    $decline.attr("onclick", "$('#popup_bug').togglePopup();");
    
    $buttons.append($confirm);
    $buttons.append($decline);
    
    $div.append($buttons);
    return $div;
}

function showAddPackageWindow(project_id) {
    $div = drawAddPackageWindow(project_id);
    var params = {
        width: 500,
        height: 180
    };
    showPopup($div, params);
}

function showRenameWindow(id, type, name) {
    $div = drawRenameWindow(id, type, name);
    var params = {
        width: 500,
        height: 180
    };
    showPopup($div, params);
}

function showAddFileWindow(package_id) {
    $div = drawAddFileWindow(package_id);
    var params = {
        width: 600,
        height: 380
    };
    showPopup($div, params);
}

function showProjectSettings() {
    closeAllPopUps();
    $div = drawProjectSettings();
    var params = {
        width: 400,
        height: 180
    };
    showPopup($div, params); 
}

function showRevisionsList() {
    closeAllPopUps();
    $div = drawRevisionsList();
    var params = {
        width: 400,
        height: 280
    };
    showPopup($div, params); 
}

function toggleConsoleWindow() {
    closeAllPopUps();
    $compilation = $("#compilation-window");
    if($compilation.hasClass("closed")) {
        $("#compilation-window").css("display", "block");
        $compilation.removeClass("closed");
    } else {
        $compilation.addClass("closed");
        $("#compilation-window").css("display", "none");
    }
    setObjectsSize();
    setObjectsSize();
}

function isConsoleOpened() {
    if($("#compilation-window").hasClass("closed"))
        return false;
    return true;
}

function drawProjectSettings() {
    $div = $("<div/>", {
        
    });
    
    return $div;
}

function drawRevisionsList() {
    var revisions;
    
    $.ajax({
        url: PATH + '/webapi/tree/revisionslist',
        type: 'GET',
        async: false,
        data: {id: getProjectId()},
        dataType: "json",
        contentType: "application/json",
        success: function(data) {
            revisions = data;
        }
    }); 
    
    console.log(revisions);
 
    $ul = $("<ol/>", {
        id: "revisions-list"
    });
    
    for(var i = 0; i < revisions.length; ++i) {
        $ul.append("<li><div class='revision-time'>" + revisions[i].creationDate + "</div><div class='revert'>Revert</div></li>");
    }
    
    return $ul;
}

function drawAddPackageWindow(id) {
    $div = $('<div/>', {
        id: "addpackage" 
    });
    
    $input = $('<input/>', {
        type: "text",
        value: ""
    });
    $(document).on('input', "#addpackage input", function() {
        isRightPackageName($("#addpackage input").val(), id);
        var val = $("#addpackage input").val();
        $("#addpackage input").val(val.toLowerCase());
    });
    
    $div.append("<div class='inputname'>Название:</div>");
    $div.append($input);
    $div.append("<div id='result-msg'></div>");
    
    $buttons = $('<div/>', {
        id: "confirm-buttons"
    });
    
    $confirm = $('<div/>', {
        text: "Добавить",
        id: "confirm",
        class: "button"
    });
    $confirm.attr( "onclick", "addPackage('"+ id +"')");
    
    $decline = $('<div/>', {
        text: "Отменить",
        id: "decline",
        class: "button"
    });
    $decline.attr("onclick", "$('#popup_bug').togglePopup();");
    
    $buttons.css("margin-top", "18px");
    $buttons.append($confirm);
    $buttons.append($decline);
    
    $div.append($buttons);
    
    return $div;
}

function drawAddFileWindow(id) {
    if ($(document.getElementById(id)).children("a").hasClass("sources")) {
        var projectname = getProjectName(id);
        var packagename = "<default_package>";
        var fullpath = "/" + projectname + "/";
    } else {
        var projectname = getProjectName(id);
        var packagename = $(document.getElementById(id)).children("a").text();
        var fullpath = "/" + projectname + "/" + packagename.replace(/\./g, '/') + "/";
    }
    
    $div = $('<div/>', {
        id: "addfile" 
    });
    
    $class = $('<input/>', {
        type: "text",
        id: "class",
        value: ""
    });
    $(document).on('input', "#addfile input#class", function() {
        var val = $("#addfile input#path").val();
        $("#addfile input#path").val(val.substring(0, val.lastIndexOf("/") + 1) + $("#addfile input#class").val() + ".java");
    });
    
    $type = $('<div/>', {
        id: "filetype",
        class: "class"
    });
    
    $type.attr("onclick", "showTypesList()");
    
    $typelist = $('<ul/>', {
        id: "typelist",
        class: "hidden"
    });
    $typelist.append("<li class='class' onclick=\"setFileType('class')\">Class</li>");
    $typelist.append("<li class='interface' onclick=\"setFileType('interface')\">Interface</li>");
    $typelist.append("<li class='runnable' onclick=\"setFileType('runnable')\">Runnable class</li>");
    $typelist.append("<li class='enum' onclick=\"setFileType('enum')\">Enumeration</li>");
    $typelist.append("<li class='annotation' onclick=\"setFileType('annotation')\">Annotation</li>");
    $typelist.append("<li class='exception' onclick=\"setFileType('exception')\">Exception</li>");
    
    $div.append($type);
    $div.append($typelist);
    $div.append("<div class='inputname'>Название:</div>");
    $div.append($class);
    
    $project = $('<input/>', {
        type: "text",
        id: "project",
        class: "addfileinputs",
        value: projectname
    });
    
    $project.prop('disabled', true);
    $div.append("<div class='inputname addfilename'>Проект:</div>");
    $div.append($project);
    
    $package = $('<input/>', {
        type: "text",
        id: "package",
        class: "addfileinputs",
        value: packagename
    });
    
    $package.prop('disabled', true);
    $div.append("<div class='inputname addfilename'>Пакет:</div>");
    $div.append($package);
    
    $fullpath = $('<input/>', {
        type: "text",
        id: "path",
        class: "addfileinputs",
        value: fullpath
    });
    
    $fullpath.prop('disabled', true);
    $div.append("<div class='inputname addfilename'>Путь до файла:</div>");
    $div.append($fullpath);
    
    $div.append("<div id='result-msg'></div>");
    
    $buttons = $('<div/>', {
        id: "confirm-buttons"
    });
    
    $confirm = $('<div/>', {
        text: "Добавить",
        id: "confirm",
        class: "button"
    });
    $confirm.attr( "onclick", "addFile('"+ id +"')");
    
    $decline = $('<div/>', {
        text: "Отменить",
        id: "decline",
        class: "button"
    });
    $decline.attr("onclick", "$('#popup_bug').togglePopup();");
    
    $buttons.css("margin-top", "18px");
    $buttons.append($confirm);
    $buttons.append($decline);
    
    $div.append($buttons);
    
    return $div;
}

function drawRenameWindow(id, type, name) {
    $div = $('<div/>', {
        id: "rename" 
    });
    
    $input = $('<input/>', {
        type: "text",
        value: name
    });
       
    var rename;
    
    $(document).on('input', "#rename input", function() {
        rename = $("#rename input").val();
    });
    
    $div.append("<div class='inputname'>Название:</div>");
    $div.append($input);
    $div.append("<div id='result-msg'></div>");
    
    $buttons = $('<div/>', {
        id: "confirm-buttons"
    });
    
    $confirm = $('<div/>', {
        text: "Изменить",
        id: "confirm",
        class: "button"
    });
    $confirm.attr( "onclick", "renameElement('"+ id +"', '"+ type +"')");
    
    $decline = $('<div/>', {
        text: "Отменить",
        id: "decline",
        class: "button"
    });
    $decline.attr("onclick", "$('#popup_bug').togglePopup();");
    
    $buttons.css("margin-top", "18px");
    $buttons.append($confirm);
    $buttons.append($decline);
    
    $div.append($buttons);
    
    return $div;
}

function setFileType(classname) {
    $("#addfile div#filetype").removeClass();
    $("#addfile div#filetype").addClass(classname);
    $("#addfile ul#typelist").addClass("hidden");
}

function showTypesList() {
    if(!$("#addfile ul#typelist").hasClass("hidden")) {
            $("#addfile ul#typelist").addClass("hidden");
    } else {
        $("#addfile ul#typelist").removeClass("hidden");
    }
}

function isRightPackageName(name, id) {
    var result = false;
    if(name.endsWith(".")) {      
        $.ajax({
            url: PATH + '/webapi/tree/packagename',
            type: 'GET',
            dataType: "json",
            async: false,
            data: {name: String(name.substring(0, name.length - 1)), project_id: String(id)},
            success: function(data) {
                switch(data) {
                    case "wrongname":
                        $("#result-msg").text("Неверное название пакета. \n\
                            Допустимы лишь латинские символы и цифры. \n\
                            Название пакета не должно начинаться с цифры.");
                        $("#result-msg").css("font-size", "13px");
                        break;
                    case "used":
                       $("#result-msg").text("Это имя пакета уже используется в вашем проекте.");
                       $("#result-msg").css("font-size", "15px");
                       break;
                    case "unknown":
                       $("#result-msg").text("Неизвестная ошибка.");  
                       $("#result-msg").css("font-size", "15px");
                       break;
                    case "ok":
                        result = !result;
                        break;
                }
            }
        }); 
        if(!result) {
            $("#result-msg").css("color", "red");
        }
    } else {
        $("#result-msg").text("");
        result = !result;
    } 
    return result;
}

function isRightClassName(name, id) {
    var result = false;
    
    $.ajax({
        url: PATH + '/webapi/tree/classname',
        type: 'GET',
        data: {name: name, package_id: id},
        dataType: "json",
        async: false,
        success: function(data) {
            switch(data) {
                case "wrongname":
                    $("#result-msg").text("Неверное название класса. \n\
                        Допустимы лишь латинские символы и цифры. \n\
                        Название класса не должно начинаться с цифры.");
                    $("#result-msg").css("font-size", "13px");
                    break;
                case "used":
                   $("#result-msg").text("Это имя класса уже используется в данном пакете.");
                   $("#result-msg").css("font-size", "15px");
                   break;
                case "unknown":
                   $("#result-msg").text("Неизвестная ошибка.");  
                   $("#result-msg").css("font-size", "15px");
                   break;
                case "ok":
                    result = !result;
                    break;
            }
        }
    }); 
    if(!result) {
        $("#result-msg").css("color", "red");
    }
    return result;
}

function isRightProjectName(name) {
    var result = false;
    
    $.ajax({
        url: PATH + '/webapi/tree/rightprojectname',
        type: 'GET',
        data: {name: name},
        dataType: "json",
        async: false,
        success: function(data) {
            result = data;
        }
    }); 
    
    if(!result) {
        $("#result-msg").text("Неверное название проекта. \n\
                            Допустимы лишь латинские символы и цифры. \n\
                            Название пакета не должно начинаться с цифры.");
        $("#result-msg").css("font-size", "13px"); 
        $("#result-msg").css("color", "red");
    }
    
    return result;
}


// FILE REVISIONS (SERVICES)

function saveFile(id) {
    if(arguments.length === 0) {
        id = getCurrentFileID();
        addCurrentFileText();
        $('#latest_update').text("Идет сохранение...");
    }
    
    var time = new Date().getTime();
    $.ajax({
        url: PATH + '/webapi/revisions',
        type:'POST', 
        data: {id: id, timeStamp: time, value: getOpenedFileText(id)},
        success: function() {
            unModifiedTab(id);
            addCurrentFileTimeStamp(time);
            if (isCurrent(id)) $('#latest_update').text("Все изменения сохранены");
        }
    });
}

function saveAllFiles() {
    addCurrentFileText();
    
    $('#latest_update').text("Идет сохранение...");
    
    modifiedList().forEach(function(entry) {
        saveFile(entry);
    });
    
    $('#latest_update').text("Все изменения сохранены");
}

function saveProject() {
    saveAllFiles();
    
    $.ajax({
        url: PATH + '/webapi/revisions/project',
        type:'POST', 
        contentType: "application/json",
        success: function(data) {
            $('#latest_update').text("Проект сохранен c хешем " + data);
        }
    });
}

function getProject(projecthash) {
    $.ajax({
        url: PATH + '/webapi/revisions/project',
        type:'GET',
        async: false,
        data: {projecthash : projecthash},
        dataType: "json",
        contentType: "application/json"
    });
}

function getFileRevision(id) {
    if(arguments.length === 0)
        id = getCurrentFileID();
    $.ajax({
        url: PATH + '/webapi/revisions',
        type:'GET',
        data: {id : id},
        async: false,
        dataType: "json",
        contentType: "application/json",
        success: function(data) {
            javaEditor.setValue(data.value);
            javaEditor.clearHistory();
            addCurrentFileTimeStamp(data.timeStamp);
        }
    });
}


// UTILS

String.prototype.endsWith = function(suffix) {
    return this.indexOf(suffix, this.length - suffix.length) !== -1;
};

function getFileDataById(id) {
    var filedata;
    $.ajax({
        url: PATH + '/webapi/tree/filedata',
        type: 'GET',
        data: {id: id},
        async: false,
        dataType: "json",
        contentType: "application/json",
        success: function(data) {
            filedata = data;
        },
        error: function(jqXHR) {
            filedata = false;
        }
    }); 
    
    return filedata;
}

function getProjectName(id) {
    var name;
    $.ajax({
        url: PATH + '/webapi/tree/projectname',
        type: 'GET',
        dataType: "json",
        data: {id: id},
        async: false,
        success: function(data) {
            name = data;
        }
    }); 
    return name;
}

/**
 * Compilation and Executing
 */

function compile() {
    if(!isConsoleOpened()) {
        toggleConsoleWindow();
    }
    
    $.ajax({
        url: PATH + '/webapi/run/compile',
        type: 'POST',
        contentType: "application/x-www-form-urlencoded",
        success: function() {
            $("#stdout").text("");
            poll();
        }
    });  
}

function execute() {
    if(!isConsoleOpened()) {
        toggleConsoleWindow();
    }
    
    $.ajax({
        url: PATH + '/webapi/run/execute',
        type: 'POST',
        contentType: "application/x-www-form-urlencoded",
        success: function() {
            $("#stdout").text("");
            poll();
        }
    });  
}

function poll() {
    $.ajax({
        url: "webapi/run/output",
        contentType: "application/json",
        success: function(data){
            var result = 1;
            if (data != null && $.isArray(data))
                data.forEach(function(entry) {
                    if(entry != null) {
                        if(entry.localeCompare("#END_OF_STREAM#") == 0) {
                            result = 0;
                            return;
                        }
                        $("#stdout").append(entry).append("<br>");
                    }
                });
            if (result == 1)
                poll();
        }
    });
}    

function compileAndRun() {
    
    if(!isConsoleOpened()) {
        toggleConsoleWindow();
    }
    
    $.ajax({
        url: PATH + '/webapi/run/compilerun',
        type: 'POST',
        contentType: "application/x-www-form-urlencoded",
        success: function() {
            $("#stdout").text("");
            poll();
        }
    });  
}

function sendInput() {
    var input = $("#stdinput").val();
    $.ajax({
        url: PATH + '/webapi/run/send',
        type: 'POST',
        contentType: "application/x-www-form-urlencoded",
        data: {input: input},
        success: function() {
            $("#stdinput").val("");
            $("#stdout").append("stdin: ").append(input).append("<br>");
        }
    }); 
    return false;
}

/**
 * Menu events
 */
