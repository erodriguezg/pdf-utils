package cl.zeke.framework.pdf.core.api;

/**
 * Created by takeda on 04-01-16.
 */

import cl.zeke.framework.pdf.core.api.resource.Resource;

import java.net.URL;

public interface PdfGenerator {

    byte[] generar(XhtmlGenerator xhtmlGenerator, Resource[] resources);

    byte[] generar(XhtmlGenerator xhtmlGenerator, Resource[] resources, PdfOptions options);

    byte[] generar(URL xhtmlURL, Resource[] resources);

    byte[] generar(URL xhtmlURL, Resource[] resources, PdfOptions options);

    byte[] generar(byte[] xhtmlByte, Resource[] resources);

    byte[] generar(byte[] xhtmlByte, Resource[] resources, PdfOptions options);

}
