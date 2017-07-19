package com.github.erodriguezg.pdfutils.core.api.resource;

/**
 * Created by takeda on 04-01-16.
 */

import java.io.File;

public class FileResource extends Resource {

    private File file;

    public FileResource(String name, ResourceType resourceType, File file) {
        this.file = file;
        this.setName(name);
        this.setResourceType(resourceType);
    }

    public File getFile() {
        return file;
    }

}