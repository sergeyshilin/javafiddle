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
    return data[data.length-1];
}


// CURRENT TEXT OF FILE BY ID

function addCurrentFileText() {
    if (!supportsSessionStorage())
        return false;
    var id = sessionStorage.getItem("currentFileID");
    if (id === null || id === '')
        return false;
    sessionStorage.setItem("openedtabs." + id, javaEditor.getValue());
    sessionStorage.setItem("openedtabs." + id + "_history", JSON.stringify(javaEditor.getHistory()));
}

function getCurrentFileText() {
    if (!supportsSessionStorage())
        return false;
    var id = sessionStorage.getItem("currentFileID");
    var text = sessionStorage.getItem("openedtabs." + id);
    var history = JSON.parse(sessionStorage.getItem("openedtabs." + id + "_history"));
    if (text !== null)
        javaEditor.setValue(text);
    else
        getFileRevision(id);
    if (history !== null)
        javaEditor.setHistory(history);
    else
        javaEditor.clearHistory();
}

function getOpenedFileText(id) {
    if (!supportsSessionStorage())
        return false;
    var text = sessionStorage.getItem("openedtabs." + id);
    if (text !== null)
        return text;
    else
        return false;
}

// MODIFIED

function modifiedList() {
    return getListFromStorage('modified');
}

function isModified(id) {
    return isExist('modified', id);
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

function unModifiedTab() {
    removeTabFromList('modified', getCurrentFileID());
    $("#tabpanel").find(".active").removeClass("modified");
}

function unModifiedTabs() {
    if (!supportsSessionStorage())
        return false;
    sessionStorage.removeItem("modified");
}


// CLOSE TAB

function closeTabInStorage(id) {
    if (!supportsSessionStorage())
        return false;
    removeTabFromList("openedtabs", id);
    sessionStorage.removeItem("openedtabs." + id);
    sessionStorage.removeItem("openedtabs." + id + "_history");
    removeTabFromList("modified", id); 
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
    if (key === null || id === null)
        return false;
    var data = getListFromStorage(key);
    if (data.indexOf(id) === -1) {
        data.push(id);
        sessionStorage.setItem(key, JSON.stringify(data));
    }
}

function removeTabFromList(key, id) {
    var data = getListFromStorage(key, id);
    var index = data.indexOf(id);
    if (index > -1) {
        data.splice(index, 1);
        sessionStorage.setItem(key, JSON.stringify(data));
    }
}