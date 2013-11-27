package com.javafiddle.web.services.utils;

import java.util.ArrayList;
import java.util.List;

public class SaveAllFilesRequest {
    List<AddFileRevisionRequest> files = new ArrayList<>();

    public List<AddFileRevisionRequest> getFiles() {
        return files;
    }

    public void setFiles(List<AddFileRevisionRequest> files) {
        this.files = files;
    }
}
