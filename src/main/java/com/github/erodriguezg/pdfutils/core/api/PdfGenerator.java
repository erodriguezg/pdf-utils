package com.github.erodriguezg.pdfutils.core.api;

/**
 * Created by takeda on 04-01-16.
 */

import com.github.erodriguezg.pdfutils.core.api.resource.Resource;

import java.net.URL;

public interface PdfGenerator {

    byte[] generar(XhtmlGenerator xhtmlGenerator, Resource[] resources);

    byte[] generar(XhtmlGenerator xhtmlGenerator, Resource[] resources, PdfOptions options);

    byte[] generar(URL xhtmlURL, Resource[] resources);

    byte[] generar(URL xhtmlURL, Resource[] resources, PdfOptions options);

    byte[] generar(byte[] xhtmlByte, Resource[] resources);

    byte[] generar(byte[] xhtmlByte, Resource[] resources, PdfOptions options);

}
