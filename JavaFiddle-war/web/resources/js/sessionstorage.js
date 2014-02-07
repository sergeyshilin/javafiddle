var editor;

// CURRENT FILE

function setCurrentFileID(id) {
    if (!supportsSessionStorage() || id === null || id === '') 
        return false;
    sessionStorage.setItem("currentFileID", id);
    sessionStorage.setItem("currentFileIDChanged", "true");
}

function getCurrentFileID() {
    if (!supportsSessionStorage())
        return false;
    return sessionStorage.getItem("currentFileID");
}

function isCurrent(id) {
    if (getCurrentFileID() === id)
        return true;
    return false;
}


// OPENED TABS

function openedTabs() {
   return getListFromStorage('openedtabs'); 
}

function isOpened(id) {
    return isExist('openedtabs', id);
}

function pushOpenedTab(id) {
    return pushToList('openedtabs', id);
}

function getFirstOpenedTab() {
    return getListFromStorage('openedtabs')[0];
}

function getLastOpenedTab() {
    var data = getListFromStorage('openedtabs');
    if (data.length > 0)
        return data[data.length-1];
    else 
        return null;
}


// CURRENT TEXT OF FILE BY ID

function addCurrentFileText() {
    if (!supportsSessionStorage())
        return false;
    var id = sessionStorage.getItem("currentFileID");
    if (id === null || id === '')
        return false;
    
    var session = editor.session;
    state = {};
    state.value = session.getValue();
    state.selection = session.selection.toJSON();
    state.options = session.getOptions();
    state.mode = session.getMode().$id;
    state.folds = session.getAllFolds().map(function(fold) {
        return {
            start       : fold.start,
            end         : fold.end,
            placeholder : fold.placeholder
        };
    });
    state.scrollTop = session.getScrollTop();
    state.scrollLeft = session.getScrollLeft();

    sessionStorage.setItem("openedtabs." + id, JSON.stringify(state));
}

function getCurrentFileText() {
    if (!supportsSessionStorage())
        return false;
    var id = sessionStorage.getItem("currentFileID");
    var modified = isModified(id);
    var state = JSON.parse(sessionStorage.getItem("openedtabs." + id));
    if (state !== null) {
         var session = editor.session;
        session.setValue(state.value);
        session.selection.fromJSON(state.selection);
        session.setOptions(state.options);
        session.setMode(state.mode);
        try {
            state.folds.forEach(function(fold){
                session.addFold(fold.placeholder, 
                    Range.fromPoints(fold.start, fold.end));
            });
        } catch(e) {}
        session.setScrollTop(state.scrollTop);
        session.setScrollTop(state.scrollLeft);

        editor.session.getUndoManager().reset();
        editor.setReadOnly(false);
    } else
        getFileRevision(id);
    
    changeModifiedState(id, modified);
}

function getOpenedFileText(id) {
    if (!supportsSessionStorage())
        return false;
    var data = sessionStorage.getItem("openedtabs." + id);
    if (data !== null) {
        var state = JSON.parse(data);
        return state.value;
    }else
        return false;
}

// MODIFIED

function modifiedList() {
    return getListFromStorage('modified');
}

function isModified(id) {
    return isExist('modified', id);
}

function changeModifiedState(id, state) {
    if (state) {
        pushToList('modified', getCurrentFileID());
        $("#tabpanel").find(".active").addClass("modified");
    } else {
        removeTabFromList('modified', id);
        $("#" + id).removeClass("modified");
    }
}

function pushModifiedTab() {
    if (!supportsSessionStorage())
        return false;
    if (sessionStorage.getItem("currentFileIDChanged") === "true")
        sessionStorage.setItem("currentFileIDChanged", "false");
    else {
        pushToList('modified', getCurrentFileID());
        $("#tabpanel").find(".active").addClass("modified");
    }
}

function unModifiedTab(id) {
    if(arguments.length === 0) {
        id = getCurrentFileID();
    }
    
    removeTabFromList('modified', id);
    $("#" + id).removeClass("modified");
}

function unModifiedTabs() {
    if (!supportsSessionStorage())
        return false;
    sessionStorage.removeItem("modified");
}


// READ ONLY

function setReadOnly(id) {
    if (!supportsSessionStorage())
        return false;
    sessionStorage.setItem("readonly." + id, "true");
}

function getReadOnly(id) {
    if (!supportsSessionStorage())
        return false;
    var status = sessionStorage.getItem("readonly." + id);
    if (status !== null)
        editor.setReadOnly(true);
    else
        editor.setReadOnly(false);
}


// TIMESTAMP

function addCurrentFileTimeStamp(timestamp) {
    if (!supportsSessionStorage())
        return false;
    var id = sessionStorage.getItem("currentFileID");
    if (id === null || id === '')
        return false;
    sessionStorage.setItem("timestamp." + id, timestamp);
}

function getCurrentFileTimeStamp() {
    if (!supportsSessionStorage())
        return false;
    var id = sessionStorage.getItem("currentFileID");
    var text = sessionStorage.getItem("timestamp." + id);
    if (text !== null)
        return parseInt(text);
    else
        return "";
}


// CLOSE TAB

function closeTabInStorage(id) {
    if (!supportsSessionStorage())
        return false;
    removeTabFromList("openedtabs", id);
    sessionStorage.removeItem("timestamp." + id);
    sessionStorage.removeItem("openedtabs." + id);
//    sessionStorage.removeItem("openedtabs." + id + "_history");
    sessionStorage.removeItem("readonly." + id);
    removeTabFromList("modified", id); 
}


// TREE

function openedNodesList() {
     return getListFromStorage('openednodes');
}  

function changeNodeState($el) {
    var id = $el.parent().attr("id");
    var classList = $el[0].className.split(/\s+/);
    var removed = false;
    if (classList !== null) {
        classList.forEach(function(entry) {
            if (entry == "harOpen") {
                removeTabFromList("openednodes", id);
                removed = true;
            }
        });
    }
    if (removed === false)
        pushToList("openednodes", id);
}

function setProjectId(id) {
    sessionStorage.setItem("projectID", "node_" + id);
}

function getProjectId() {
    return sessionStorage.getItem("projectID");
}


// UTILS

function supportsSessionStorage() {
    try {
        return 'sessionStorage' in window && window['sessionStorage'] !== null;
    } catch (e) {
        return false;
    }
}

function getListFromStorage(key) {
    if (!supportsSessionStorage())
        return false;
    var data = JSON.parse(sessionStorage.getItem(key));    
    if (data === null) 
        data = [];
    return data;
}

function isExist(key, id) {
    if (getListFromStorage(key).indexOf(id) > -1)
        return true;
    return false;
} 

function isEmpty(key) {
    if (getListFromStorage(key).length === 0)
        return true;
    return false;
}

function pushToList(key, id) {
    if (!supportsSessionStorage())
        return false;
    if (key === null || id === null)
        return false;
    var data = getListFromStorage(key);
    if (data.indexOf(id) === -1) {
        data.push(id);
        sessionStorage.setItem(key, JSON.stringify(data));
    }
}

function removeTabFromList(key, id) {
    if (!supportsSessionStorage())
        return false;
    var data = getListFromStorage(key, id);
    var index = data.indexOf(id);
    if (index > -1) {
        data.splice(index, 1);
        sessionStorage.setItem(key, JSON.stringify(data));
    }
}

function invalidateSession() {
    if (!supportsSessionStorage())
        return false;
    sessionStorage.clear();
}