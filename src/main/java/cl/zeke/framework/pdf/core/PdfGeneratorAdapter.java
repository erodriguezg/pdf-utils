package cl.zeke.framework.pdf.core;

import cl.zeke.framework.pdf.core.api.PdfGenerator;
import cl.zeke.framework.pdf.core.api.PdfOptions;
import cl.zeke.framework.pdf.core.api.XhtmlGenerator;
import cl.zeke.framework.pdf.core.api.resource.Resource;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author eduardo
 */
public abstract class PdfGeneratorAdapter implements PdfGenerator {

    @Override
    public byte[] generar(XhtmlGenerator xhtmlGenerator, Resource[] resources) {
        return generar(xhtmlGenerator, resources, null);
    }

    @Override
    public byte[] generar(XhtmlGenerator xhtmlGenerator, Resource[] resources, PdfOptions options) {
        if (xhtmlGenerator == null) {
            throw new IllegalArgumentException("XhtmlGenerator is null!");
        }
        byte[] xhtmlBytes = xhtmlGenerator.generar();
        return generar(xhtmlBytes, resources, options);
    }

    @Override
    public byte[] generar(URL xhtmlURL, Resource[] resources) {
        return generar(xhtmlURL, resources, null);
    }

    @Override
    public byte[] generar(URL xhtmlURL, Resource[] resources, PdfOptions options) {
        if (xhtmlURL == null) {
            throw new IllegalArgumentException("URL is null!");
        }

        byte[] xhtmlBytes = null;
        try (InputStream is = xhtmlURL.openStream()) {
            xhtmlBytes = IOUtils.toByteArray(is);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }

        return generar(xhtmlBytes, resources, options);
    }

    @Override
    public byte[] generar(byte[] xhtmlByte, Resource[] resources) {
        return generar(xhtmlByte, resources, null);
    }
}
