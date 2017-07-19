package com.github.erodriguezg.pdfutils.core.flyingsaucer;

/**
 * Created by takeda on 04-01-16.
 */

import com.github.erodriguezg.pdfutils.core.PdfGeneratorAdapter;
import com.github.erodriguezg.pdfutils.core.api.PdfOptions;
import com.github.erodriguezg.pdfutils.core.api.resource.FileResource;
import com.github.erodriguezg.pdfutils.core.api.resource.Resource;
import com.github.erodriguezg.pdfutils.core.api.resource.ResourceType;
import com.lowagie.text.DocumentException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FlyingSaucerGenerator extends PdfGeneratorAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(FlyingSaucerGenerator.class);

    private static final String HTML_ELEMENT_NAME = "html";
    private static final String HTML_HEADER_ELEMENT_NAME = "head";
    private static final String HTML_BODY_ELEMENT_NAME = "body";
    private static final String HTML_IMG_ELEMENT_NAME = "img";

    @Override
    public byte[] generar(byte[] xhtmlByte, Resource[] resources, PdfOptions options) {
        File xhtmlFileTemp = null;
        try {
            xhtmlFileTemp = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
            try (InputStream xhtmlInputStream = preProceso(xhtmlByte, resources);
                 OutputStream xhtmlOutputStream = new FileOutputStream(xhtmlFileTemp)
            ) {
                IOUtils.copy(xhtmlInputStream, xhtmlOutputStream);
                ITextRenderer renderer = new ITextRenderer();
                addFonts(renderer, filtrarResources(resources, ResourceType.FONT));
                renderer.setDocument(xhtmlFileTemp);
                renderer.layout();
                ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
                renderer.createPDF(pdfOutputStream);
                return pdfOutputStream.toByteArray();
            }
        } catch (DocumentException | IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (xhtmlFileTemp != null && !xhtmlFileTemp.delete()) {
                LOG.warn("no se elimino archivo '{}'", xhtmlFileTemp);
            }
        }
    }

    private void addFonts(ITextRenderer renderer, Map<String, FileResource> fontsMaps) throws IOException, DocumentException {
        for (Map.Entry<String, FileResource> entry : fontsMaps.entrySet()) {
            renderer.getFontResolver().addFont(entry.getValue().getFile().getAbsolutePath(), entry.getKey(),
                    "utf-8", true, null);
        }
    }

    private InputStream preProceso(byte[] xhtmlByte, Resource[] resources) {
        try (InputStream inputStream = new ByteArrayInputStream(translateEntityReferences(xhtmlByte))) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc = procesadorEstructuraBase(dBuilder, doc);
            procesadorCss(doc, filtrarResources(resources, ResourceType.CSS));
            procesadorImagenes(doc, filtrarResources(resources, ResourceType.IMG));
            return docToInputStream(doc);
        } catch (IOException | SAXException | TransformerException | ParserConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    /*
    Se encarga de crear una estructura fija de html con head y body
     */
    private Document procesadorEstructuraBase(DocumentBuilder builder, Document docEntrada) {
        Document docSalida = builder.newDocument();
        Element elementHtml = docSalida.createElement(HTML_ELEMENT_NAME);
        docSalida.appendChild(elementHtml);
        Element elementHeader = docEntrada.getElementsByTagName(HTML_HEADER_ELEMENT_NAME).getLength() > 0 ?
                (Element) docEntrada.getElementsByTagName(HTML_HEADER_ELEMENT_NAME).item(0) : null;
        Element elementBody = docEntrada.getElementsByTagName(HTML_BODY_ELEMENT_NAME).getLength() > 0 ?
                (Element) docEntrada.getElementsByTagName(HTML_BODY_ELEMENT_NAME).item(0) : null;
        if (elementHeader == null) {
            elementHeader = docSalida.createElement(HTML_HEADER_ELEMENT_NAME);
        }
        if (elementBody == null) {
            elementBody = docSalida.createElement(HTML_BODY_ELEMENT_NAME);
        }
        elementHtml.appendChild(docSalida.importNode(elementHeader, true));
        elementHtml.appendChild(docSalida.importNode(elementBody, true));
        return docSalida;
    }

    /*
        agrega css en la head
     */
    private void procesadorCss(Document doc, Map<String, FileResource> cssFilesMap) {
        if (cssFilesMap == null || cssFilesMap.isEmpty()) {
            return;
        }
        Element header = (Element) doc.getElementsByTagName(HTML_HEADER_ELEMENT_NAME).item(0);
        for (Map.Entry<String, FileResource> entry : cssFilesMap.entrySet()) {
            Element newStyle = doc.createElement("link");
            newStyle.setAttribute("rel", "stylesheet");
            newStyle.setAttribute("type", "text/css");
            newStyle.setAttribute("media", "all");
            newStyle.setAttribute("href", normalizarDirectorio(entry.getValue().getFile()));
            header.appendChild(newStyle);
        }
    }

    /*
    reemplaza img con imagenes de FileResource
     */
    private void procesadorImagenes(Document doc, Map<String, FileResource> imgFilesMap) {
        NodeList imgNodes = doc.getElementsByTagName(HTML_IMG_ELEMENT_NAME);
        if (imgFilesMap == null || imgFilesMap.isEmpty() || imgNodes.getLength() == 0) {
            return;
        }
        for (int i = 0; i < imgNodes.getLength(); i++) {
            Element imgElement = (Element) imgNodes.item(i);
            String srcValue = imgElement.getAttribute("src");
            FileResource fileResource = imgFilesMap.get(srcValue);
            if (fileResource != null) {
                imgElement.setAttribute("src", normalizarDirectorio(fileResource.getFile()));
            }
        }
    }

    private InputStream docToInputStream(Document doc) throws TransformerException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Source xmlSource = new DOMSource(doc);
        Result outputTarget = new StreamResult(outputStream);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, "application/xhtml+xml");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//W3C//DTD XHTML 1.0 Strict//EN");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
        transformer.transform(xmlSource, outputTarget);
        byte[] salidaBytesXml = outputStream.toByteArray();
        String logXml = new String(salidaBytesXml);
        LOG.debug("xml modificado post-procesado: {} ", logXml);
        return new ByteArrayInputStream(salidaBytesXml);
    }

    private Map<String, FileResource> filtrarResources(Resource[] resources, ResourceType resourceType) {
        if (resources == null || resources.length == 0) {
            return Collections.emptyMap();
        }
        return Arrays.asList(resources).stream()
                .filter(r -> r.getResourceType() == resourceType)
                .collect(Collectors.toMap(r -> r.getName(), r -> (FileResource) r));
    }

    private String normalizarDirectorio(File file) {
        String pathsNormalizados = FilenameUtils.separatorsToUnix(file.getPath());
        return pathsNormalizados.split(":").length == 1 ?
                pathsNormalizados.split(":")[0] : pathsNormalizados.split(":")[1];
    }

    private byte[] translateEntityReferences(byte[] xmlBytes) throws UnsupportedEncodingException {
        String newXml = new String(xmlBytes, "utf-8");
        Map<String, String> entityRefs = new HashMap<>();
        entityRefs.put("&nbsp;", "&#160;");
        entityRefs.put("&quot;", "&#34;");
        entityRefs.put("&amp;", "&#38;");
        entityRefs.put("&lt;", "&#60;");
        entityRefs.put("&gt;", "&#62;");
        entityRefs.put("&laquo;", "&#171;");
        entityRefs.put("&raquo;", "&#187;");
        entityRefs.put("&Aacute;", "&#193;");
        entityRefs.put("&Eacute;", "&#201;");
        entityRefs.put("&Iacute;", "&#205;");
        entityRefs.put("&Oacute;", "&#211;");
        entityRefs.put("&Uacute;", "&#218;");
        entityRefs.put("&Ntilde;", "&#209;");
        entityRefs.put("&aacute;", "&#225;");
        entityRefs.put("&eacute;", "&#233;");
        entityRefs.put("&iacute;", "&#237;");
        entityRefs.put("&oacute;", "&#243;");
        entityRefs.put("&uacute;", "&#250;");
        entityRefs.put("&nacute;", "&#241;");
        for (Map.Entry<String, String> er : entityRefs.entrySet()) {
            newXml = newXml.replace(er.getKey(), er.getValue());
        }
        return newXml.getBytes();
    }

}
