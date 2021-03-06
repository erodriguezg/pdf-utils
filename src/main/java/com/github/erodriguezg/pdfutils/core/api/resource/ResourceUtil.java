package com.github.erodriguezg.pdfutils.core.api.resource;

/**
 * Created by takeda on 04-01-16.
 */

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface ResourceUtil {


    static Resource[] getResourcesByType(Resource[] resources, ResourceType inputType) {

        List<Resource> resourceList = new ArrayList();

        if (resources == null || resources.length == 0) {
            return resourceList.toArray(new Resource[0]);
        }

        for (Resource resource : resources) {
            if (resource.getResourceType() == inputType) {
                resourceList.add(resource);
            }
        }

        return resourceList.toArray(new Resource[0]);
    }

    static byte[] getResourceBytes(Resource resource) {

        if (resource instanceof FileResource) {
            File fileResource = ((FileResource) resource).getFile();
            try (InputStream is = new FileInputStream(fileResource)) {
                return IOUtils.toByteArray(is);
            } catch (IOException ex) {
                throw new IllegalStateException("error obtener bytes! " + resource.getName(), ex);
            }
        } else if (resource instanceof RawResource) {
            return ((RawResource) resource).getRaw();
        }

        throw new IllegalStateException("Illegal Resource Format!");
    }

    static File crearFileTmp(RawResource rawResource) {
        File tmpFile;
        try {
            tmpFile = File.createTempFile(UUID.randomUUID().toString(), rawResource.getName() + "." + rawResource.getResourceType().name());
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
        try (OutputStream os = new FileOutputStream(tmpFile);
             InputStream is = new ByteArrayInputStream(rawResource.getRaw())) {
            IOUtils.copy(is, os);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
        return tmpFile;
    }

}