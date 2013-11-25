// COOKIES

function getCurrentFileID() {
    return getCookie("currentFileID");
}

function setCurrentFileID(id) {
    setCookie("currentFileID", id, "path=/");
}

function pushOpenedTab(id) {
    var data = JSON.parse(getCookie('openedtabs'));
    if (data === null)
        data = [];
    data.push(id);
    
    setCookie('openedtabs', JSON.stringify(data));
}

function getFirstOpenedTab() {
    var data = JSON.parse(getCookie('openedtabs'));
    if (data === null)
        data = [""];
    
    return data[0];
}

function getLastOpenedTab() {
    var data = JSON.parse(getCookie('openedtabs'));
    if (data === null)
        data = [""];
    
    return data[data.length-1];
}

function closeOpenedTab(id) {
    var data = JSON.parse(getCookie('openedtabs'));
    if (data === null)
        return;
    var index = data.indexOf(id);
    if (index > -1) {
        data.splice(index, 1);
    }
    
    setCookie('openedtabs', JSON.stringify(data));
}

function isOpened(id) {
    var data = JSON.parse(getCookie('openedtabs'));
    if (data === null)
        return false;
    if (data.indexOf(id) >= 0)
        return true;
    return false;
}


// LOCALSTORAGE

function getCurrentFileText(id) {
    if (!supportsLocalStorage()) { return false; }
    var data = localStorage["javafiddle.openedtabs." + id];
    if (data != null)
        javaEditor.setValue(data);
    else
        getFileRevision(id);
}

function addCurrentFileText(id) {
    if (!supportsLocalStorage() || id == "") { return false; }
    localStorage["javafiddle.openedtabs." + id] = javaEditor.getValue();
}

function removeCurrentFileText(id) {
    if (!supportsLocalStorage()) { return false; }
    localStorage.removeItem["javafiddle.openedtabs." + id];
}

// UTILS

function getCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}

function setCookie(name,value,days) {
    if (days) {
        var date = new Date();
        date.setTime(date.getTime()+(days*24*60*60*1000));
        var expires = "; expires="+date.toGMTString();
    }
    else var expires = "";
    document.cookie = name+"="+value+expires+"; path=/";
}

function deleteCookie(name) {
  setCookie(name, "", { expires: -1 });
}
