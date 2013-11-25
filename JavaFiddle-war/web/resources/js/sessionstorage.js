function getCurrentFileID() {
    if (!supportsSessionStorage()) { return false; }
    return sessionStorage.getItem("currentFileID");
}

function setCurrentFileID(id) {
    if (!supportsSessionStorage()) { return false; }
    sessionStorage.setItem("currentFileID", id);
}

function pushOpenedTab(id) {
    if (!supportsSessionStorage()) { return false; }
    var data = JSON.parse(sessionStorage.getItem('openedtabs'));
    if (data === null)
        data = [];
    if (data.indexOf(id) == -1)
        data.push(id);
    
    sessionStorage.setItem('openedtabs', JSON.stringify(data));
}

function getFirstOpenedTab() {
    if (!supportsSessionStorage()) { return false; }
    var data = JSON.parse(sessionStorage.getItem('openedtabs'));
    if (data === null)
        data = [""];
    
    return data[0];
}

function getLastOpenedTab() {
    if (!supportsSessionStorage()) { return false; }
    var data = JSON.parse(sessionStorage.getItem('openedtabs'));
    if (data === null)
        data = [""];
    
    return data[data.length-1];
}

function clearOpenedTab(id) {
    if (!supportsSessionStorage()) { return false; }
    var data = JSON.parse(sessionStorage.getItem('openedtabs'));
    if (data === null)
        return;
    var index = data.indexOf(id);
    if (index > -1) {
        data.splice(index, 1);
    }
    
    sessionStorage.setItem('openedtabs', JSON.stringify(data));
}

function openedTabs() {
    if (!supportsSessionStorage()) { return false; }
    return JSON.parse(sessionStorage.getItem('openedtabs'));    
}

function isOpened(id) {
    if (!supportsSessionStorage()) { return false; }
    var data = JSON.parse(sessionStorage.getItem('openedtabs'));
    if (data === null)
        return false;
    if (data.indexOf(id) >= 0)
        return true;
    return false;
}

function getCurrentFileText(id) {
    if (!supportsSessionStorage()) { return false; }
    var data = sessionStorage.getItem("javafiddle.openedtabs." + id);
    var history = JSON.parse(sessionStorage.getItem("javafiddle.openedtabs." + id + "_history"));
    if (data != null) {
        javaEditor.setValue(data);
        javaEditor.setHistory(history);
    } else {
        getFileRevision(id);
        javaEditor.clearHistory();
    }
}

function addCurrentFileText(id) {
    if (!supportsSessionStorage() || id == "" || id == null) { return false; }
    sessionStorage.setItem("javafiddle.openedtabs." + id, javaEditor.getValue());
    sessionStorage.setItem("javafiddle.openedtabs." + id + "_history", JSON.stringify(javaEditor.getHistory()));
}

function removeCurrentFileText(id) {
    if (!supportsSessionStorage()) { return false; }
    sessionStorage.removeItem("javafiddle.openedtabs." + id);
    sessionStorage.removeItem("javafiddle.openedtabs." + id + "_history");
}

// UTILS

function supportsSessionStorage() {
    try {
        return 'sessionStorage' in window && window['sessionStorage'] !== null;
    } catch (e) {
        return false;
    }
}