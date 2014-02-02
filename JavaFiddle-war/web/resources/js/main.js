var PATH = "";
var javaEditor;
var PROJECTPARAM;
var $elClicked;

// EVENTS

String.prototype.endsWith = function(suffix) {
    return this.indexOf(suffix, this.length - suffix.length) !== -1;
};

$(document).ready(function(){
    loadLiHarmonica();
    var projecthash = PROJECTPARAM;
    if(projecthash !== "") {
        getProject(projecthash);
        invalidateSession();
    }
    javaEditor.setOption("readOnly", true);
    buildTree();
    loadTabs();
    setJavaEditorSize();
    loadPopupMenu();
    $(function() {
        $('.dropdown-toggle').dropdown();
    });
    
    if (typeof String.prototype.endsWith != 'function') {
        String.prototype.endsWith = function(str) {
            return this.substring(this.length - str.length, this.length) === str;
        };
    };
    
    javaEditor.on("change", function() {
        pushModifiedTab();
        hidePopups();
    });

    javaEditor.on("mousedown", function() {
        hidePopups();
        $("#main_menu").find(".open").removeClass("open");
    });

    javaEditor.on("focus", function() {
        hidePopups();
        $("#main_menu").find(".open").removeClass("open");
    });
});
                        
$(window).resize(function() {
    setJavaEditorSize();
});

$("#codetext").resize(function() {
    setJavaEditorSize();
});

$(document).click(function () {
    hidePopups();
});

window.onbeforeunload = (function() {
    if (isEmpty('modified'))
        return;
    addCurrentFileText();
    return "WARNING! The project has unsaved files. When the session expires, any unsaved changes will be lost!";
});

function setJavaEditorSize() { 
    javaEditor.setSize(null, $("#codetext").height());
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
    
    if (getCurrentFileID() == "about_tab" || getCurrentFileID() == "shortcuts_tab") {
        $('#latest_update').text("Static tab.");
        return;
    }
        
    if (time == "")
        $('#latest_update').text("Last changes: not saved yet.");
    else {
        moment.lang('en');
        var earlier = moment(time);
        $('#latest_update').text("Last changes: " + earlier.from(moment()));
    }
}

function closeTabsFromPackage(id) {
    var $pack = $('#' + id);
    $pack.children("ul").children("li").each(function() {
        var subid = $(this).attr("id");
        if($(this).children("a").hasClass("package"))
            closeTabsFromPackage(subid);
        else 
            closeTab($('#' + subid + '_tab'));
    });
}

function closeAllTabs() {
    var $panel = $("#tabpanel");
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
                    if (!(pck.name == "<default_package>"))
                        $('#node_' + pck.parentId + '_list').append('<li id = "node_' + pck.id + '"><a href="#" class="package" onclick="changeNodeState($(this));">' + pck.name + '</a><ul id ="node_' + pck.id + '_list"\></li>');
                }   
                for (var j = 0; j < proj.packages.length; j++) {
                    var pack = proj.packages[j];
                    for (var k = 0; k < pack.files.length; k++) {
                        var file = pack.files[k];
                        var parent = (pack.name == "<default_package>") ? proj.id : pack.id; 
                        $('#node_' + parent + '_list').append('<li id = "node_' + file.id + '"><a href="#" class="' + file.type + '" onclick="openTabFromTree($(this));">' + file.name + '</a></li>');
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
        }
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

function loadProjectByHash(hash) {
    if(hashIsCorrect()) {
        getProject(hash);
    } else {
        $("#result-msg").text("Project with this hash does not exist");
        $("#result-msg").css("color", "red");
    }
}

function toggleProjectTreePanel() {
    var $treepanel = $("#treepanel");
    if($treepanel.css("display") == "none")
        $treepanel.css("display", "block");
    else
        $treepanel.css("display", "none");
}

function toggleHeader() {
    var $header = $("#header-top");
    if($header.css("display") === "none")
        $header.css("display", "flex");
    else
        $header.css("display", "none");
    setJavaEditorSize();
}


// TOGGLES

function loadPopupMenu() {
    $(function () {
        $("#treepanel").on("contextmenu", "div", function(e) {
            var $contextMenu = $("#treeMenu");
            hidePopups();
            $elClicked = $(this);
            $contextMenu.css({
                display: "block",
                left: e.pageX,
                top: e.pageY
            });
            return false;           
        });
        $("#tree").on("contextmenu", "a", function(e) {
            var $contextMenu;
            var classname = $(this).attr("class").split(" ")[0];
            switch(classname) {
            case "root":    
                $contextMenu = $("#projectMenu");
                break;
            case "sources":
                $contextMenu = $("#srcMenu");
                break;
            case "package":
                $contextMenu = $("#packageMenu");
                break;
            case "class": 
            case "interface": 
            case "exception": 
            case "annotation": 
            case "runnable":
            case "enum":
                $contextMenu = $("#fileMenu");
                break;
            default:
                return;
            }
            
            hidePopups();
            $elClicked = $(this);
            $contextMenu.css({
                display: "block",
                left: e.pageX,
                top: e.pageY
            });
            return false;
        });
    });
}

function hidePopups() {
    $("#treeMenu").hide();
    $("#projectMenu").hide();
    $("#srcMenu").hide();
    $("#packageMenu").hide();
    $("#fileMenu").hide();
}

// FILE REVISIONS (SERVICES)

function saveFile(id) {
    if(arguments.length === 0) {
        id = getCurrentFileID();
        addCurrentFileText();
        $('#latest_update').text("Saving...");
    }
    
    var time = new Date().getTime();
    $.ajax({
        url: PATH + '/webapi/revisions',
        type:'POST', 
        data: {id: id, timeStamp: time, value: getOpenedFileText(id)},
        success: function() {
            unModifiedTab(id);
            addCurrentFileTimeStamp(time);
            if (isCurrent(id))
                $('#latest_update').text("All changes saved.");
        },
        error: function(jqXHR) {
            if (jqXHR.status == 406)
                $('#latest_update').text("Saving isn't acceptable.");
        }
    });
}

function saveAllFiles() {
    addCurrentFileText();
    
    $('#latest_update').text("Saving...");
    
    modifiedList().forEach(function(entry) {
        saveFile(entry);
    });
    
    $('#latest_update').text("All files saved");
}

function saveProject() {
    saveAllFiles();
    
    $.ajax({
        url: PATH + '/webapi/revisions/project',
        type:'POST', 
        contentType: "application/json",
        success: function(data) {
            $('#latest_update').text("Project saved with hash: " + data);
        },
        error: function() {
            $('#latest_update').text("Project hasn't been saved.");
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
        },
        error: function(jqXHR) {
            if (jqXHR.status == 406)
                $('#latest_update').text("File not found in project.");
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
            if (jqXHR.status == 410) {
                $('#latest_update').text("File not found in project.");
                filedata = false;
            }
        }
    }); 
    
    return filedata;
}



/**
 * Compilation and Executing
 */

function compile() {
    if(!isConsoleOpened()) {
        toggleConsoleWindow();
    }
    
    visualizeProcess("compile");
    
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
    
    visualizeProcess("run");
    
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
            else if(result == 0)
                stopProcess();
        }
    });
}    

function compileAndRun() {
    
    if(!isConsoleOpened()) {
        toggleConsoleWindow();
    }
    
    visualizeProcess("compileandrun");
    
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

function openStaticTab(name) {
    var id;
    var nm;
    var cl;
    
    switch(name) {
        case "about":
            id = "about_tab";
            nm = "About";
            cl = "help";
            break;
        case "shortcuts":
            id = "shortcuts_tab";
            nm = "Shortcuts";
            cl = "help";
            break;            
        default:
            return;
    }

    setReadOnly(id);
    var li;
    if(!isOpened(id))
        li = addTabToPanel(id, nm, cl);
    else {
        li = document.getElementById(id);
        li = $("#tabpanel").find(li);
    }
    selectTab(li);
}

function visualizeProcess(process) {
    disableProcess();
    
    switch(process) {
        case "compile":
            $("#compile").css("background-image", "url('resources/img/stop.ico')");
            $("#compile").attr("onclick", "stopProcess();");
            break;
        case "run":
            $("#run").css("background-image", "url('resources/img/stop.ico')");
            $("#run").attr("onclick", "stopProcess();");
            break;
        case "compileandrun":
            $("#compileandrun").css("background-image", "url('resources/img/stop.ico')");
            $("#compileandrun").attr("onclick", "stopProcess();");
            break;        
    }
}

function disableProcess() {
    $("#compile").attr("onclick", "return false;");
    $("#run").attr("onclick", "return false;");
    $("#compileandrun").attr("onclick", "return false;");
    $("#compile").css("background-image", "url('resources/img/compile-block.ico')");
    $("#run").css("background-image", "url('resources/img/run-block.ico')");
    $("#compileandrun").css("background-image", "url('resources/img/compileandrun-block.ico')");
}

function stopProcess() {
    $("#compile").attr("onclick", "compile();");
    $("#run").attr("onclick", "execute();");
    $("#compileandrun").attr("onclick", "compileAndRun();");
    $("#compile").css("background-image", "url('resources/img/compile.ico')");
    $("#run").css("background-image", "url('resources/img/playback_play.ico')");
    $("#compileandrun").css("background-image", "url('resources/img/compileandrun.ico')");
}

function hashIsCorrect(hash) {
    var result = false;
    $.ajax({
        url: PATH + '/webapi/tree/righthash',
        type: 'GET',
        data: {hash: hash},
        dataType: "json",
        async: false,
        success: function(data) {
            result = data;
        }
    });
    return result;
}

function toggleConsoleWindow() {
    $compilation = $("#compilation-window");
    if($compilation.hasClass("closed")) {
        $("#compilation-window").css("display", "block");
        $compilation.removeClass("closed");
    } else {
        $compilation.addClass("closed");
        $("#compilation-window").css("display", "none");
    }
    setJavaEditorSize();
    setJavaEditorSize();
}

function isConsoleOpened() {
    if($("#compilation-window").hasClass("closed"))
        return false;
    return true;
}