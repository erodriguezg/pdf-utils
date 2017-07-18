package cl.zeke.framework.pdf.core.flyingsaucer;

import cl.zeke.framework.pdf.core.api.PdfGenerator;
import cl.zeke.framework.pdf.core.api.XhtmlGenerator;
import cl.zeke.framework.pdf.core.api.resource.FileResource;
import cl.zeke.framework.pdf.core.api.resource.Resource;
import cl.zeke.framework.pdf.core.api.resource.ResourceType;
import cl.zeke.framework.pdf.core.xhtml.velocity.VelocityXhtmlGenerator;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collections;
import java.util.UUID;

/**
 * Created by eduar on 03/04/2017.
 */
public class FlyingSaucerGeneratorIT {

    private static final Logger LOG = LoggerFactory.getLogger(FlyingSaucerGeneratorIT.class);

    private PdfGenerator pdfGenerator;

    @Before
    public void before() {
        pdfGenerator = new FlyingSaucerGenerator();
    }

    @Test
    public void holaMundoTest() {
        XhtmlGenerator xhtmlGenerator = new VelocityXhtmlGenerator("<body><h1>hola mundo</h1></body>", Collections.<String, Object>emptyMap());
        byte[] bytePdf = pdfGenerator.generar(xhtmlGenerator, null);
        escribirDebugPdf(bytePdf);
        Assert.assertNotNull(bytePdf);
    }

    @Test
    public void holaMundoConImagen() {
        XhtmlGenerator xhtmlGenerator = new VelocityXhtmlGenerator(
                "<body><h1>Hola Mundo</h1><img src=\"foto\" style=\"width:100%\" ></img></body>", Collections.<String, Object>emptyMap());

        FileResource imagenFoto = new FileResource("foto", ResourceType.IMG,
                new File(FlyingSaucerGeneratorIT.class.getResource("/foto.jpg").getFile()));

        byte[] bytePdf = pdfGenerator.generar(xhtmlGenerator, new Resource[] {imagenFoto});
        escribirDebugPdf(bytePdf);
        Assert.assertNotNull(bytePdf);
    }

    @Test
    public void holaMundoConEstilo() {
        XhtmlGenerator xhtmlGenerator = new VelocityXhtmlGenerator(
                "<body>" +
                        "<h1>Hola Mundo</h1>" +
                        "<div class=\"centered\">" +
                        "<img src=\"foto\"></img>" +
                        "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut " +
                        "labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris " +
                        "nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate " +
                        "velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, " +
                        "sunt in culpa qui officia deserunt mollit anim id est laborum.</p>" +
                        "</div><" +
                        "/body>", Collections.<String, Object>emptyMap());

        FileResource cssFile = new FileResource("estilo1", ResourceType.CSS,
                new File(FlyingSaucerGeneratorIT.class.getResource("/estilo1.css").getFile()));

        FileResource imagenFoto = new FileResource("foto", ResourceType.IMG,
                new File(FlyingSaucerGeneratorIT.class.getResource("/foto.jpg").getFile()));

        FileResource seasideFont = new FileResource("seaside", ResourceType.FONT,
                new File(FlyingSaucerGeneratorIT.class.getResource("/seaside.ttf").getFile()));

        FileResource walkwayFont = new FileResource("walkway", ResourceType.FONT,
                new File(FlyingSaucerGeneratorIT.class.getResource("/walkway.ttf").getFile()));

        byte[] bytePdf = pdfGenerator.generar(xhtmlGenerator,
                new Resource[] {cssFile, imagenFoto, seasideFont, walkwayFont});
        escribirDebugPdf(bytePdf);
        Assert.assertNotNull(bytePdf);
    }


    @Test
    public void generadorTexto() {
        XhtmlGenerator xhtmlGenerator = new VelocityXhtmlGenerator(
                "<body>" +
                        "<p><strong style=\"background-color: rgb(238, 238, 238); color: black;\">   RESOLUCION O Nº.: </strong><span style=\"background-color: rgb(238, 238, 238); color: black;\">________________/</span></p><p><br/></p><p><strong style=\"background-color: rgb(238, 238, 238); color: black;\">   MAT.: </strong><span style=\"background-color: rgb(238, 238, 238); color: black;\">XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX</span></p><p><br/></p><p><strong style=\"background-color: rgb(238, 238, 238); color: black;\">   SANTIAGO,</strong></p><p class=\"ql-align-justify\"><br/></p><p class=\"ql-align-justify\"><br/></p><p class=\"ql-align-justify\"><span style=\"background-color: rgb(238, 238, 238);\">\t</span><strong style=\"background-color: rgb(238, 238, 238); color: black;\">VISTO:</strong></p><p class=\"ql-align-justify\"><br/></p><p><span style=\"background-color: rgb(238, 238, 238);\">\t</span><span style=\"background-color: rgb(238, 238, 238); color: black;\">xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</span></p><p><span style=\"background-color: rgb(238, 238, 238);\">\t</span></p><p><span style=\"background-color: rgb(238, 238, 238);\">\t</span><strong style=\"background-color: rgb(238, 238, 238); color: black;\">CONSIDERANDO:</strong></p><p><span style=\"background-color: rgb(238, 238, 238);\">\t</span></p><p class=\"ql-align-justify\"><span style=\"background-color: rgb(238, 238, 238);\">\t</span><strong style=\"background-color: rgb(238, 238, 238);\">1.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</strong><span style=\"background-color: rgb(238, 238, 238); color: black;\">Que,XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.</span></p><p class=\"ql-align-justify\"><br/></p><p class=\"ql-align-justify\"><span style=\"background-color: rgb(238, 238, 238);\">\t</span><strong style=\"background-color: rgb(238, 238, 238);\">2.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</strong><span style=\"background-color: rgb(238, 238, 238);\">XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX</span></p><p><br/></p><p class=\"ql-align-justify\"><span style=\"background-color: rgb(238, 238, 238);\">\t</span><strong style=\"background-color: rgb(238, 238, 238);\">3.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</strong></p><p class=\"ql-align-justify\"><span style=\"background-color: rgb(238, 238, 238);\">\t</span><strong style=\"background-color: rgb(238, 238, 238);\">4.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</strong></p><p><br/></p><p><span style=\"background-color: rgb(238, 238, 238);\">\t</span><strong style=\"background-color: rgb(238, 238, 238); color: black;\">RESUELVO:</strong></p><p class=\"ql-align-justify\"><br/></p><p class=\"ql-align-justify\"><span style=\"background-color: rgb(238, 238, 238);\">\t</span><strong style=\"background-color: rgb(238, 238, 238);\">1.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</strong><span style=\"background-color: rgb(238, 238, 238);\">XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX</span></p><p class=\"ql-align-justify\"><br/></p><p class=\"ql-align-justify\">\t<strong style=\"background-color: rgb(238, 238, 238); color: black;\">2.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</strong><span style=\"background-color: rgb(238, 238, 238);\">XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX</span></p><p class=\"ql-align-justify\"><br/></p><p class=\"ql-align-justify\">\t<strong style=\"background-color: rgb(238, 238, 238); color: black;\">3.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</strong><span style=\"background-color: rgb(238, 238, 238); color: black;\">XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX</span></p><p><br/></p><p class=\"ql-align-justify\"><br/></p><p class=\"ql-align-center\"><span style=\"background-color: rgb(238, 238, 238);\">\t</span><span style=\"background-color: rgb(238, 238, 238); color: black;\">Anótese y notifíquese, </span></p><p><br/></p><p><br/></p><p><br/></p><p><br/></p><p><br/></p><p><br/></p><p><br/></p><p class=\"ql-align-center\"><strong style=\"background-color: rgb(238, 238, 238); color: black;\">xxxxxxxxxxxxxxxxxxxxxxxxxxxx</strong></p><p class=\"ql-align-center\"><strong style=\"background-color: rgb(238, 238, 238); color: black;\">xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</strong></p><p class=\"ql-align-center\"><strong style=\"background-color: rgb(238, 238, 238); color: black;\">&nbsp;(Puede firmar el director o subdirector) </strong></p><p><br/></p><p><br/></p><p><br/></p><p><strong style=\"background-color: rgb(238, 238, 238); color: black;\">&nbsp;XXXX/XXXX/xxxx</strong></p><p class=\"ql-align-justify\"><strong style=\"background-color: rgb(238, 238, 238); color: black;\">Distribución:</strong></p><p class=\"ql-align-justify\"><span style=\"background-color: rgb(238, 238, 238); color: black;\">1.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Xxxxxxxxxxxxxxxxxxxx </span></p><p class=\"ql-align-justify\"><span style=\"background-color: rgb(238, 238, 238); color: black;\">2.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;xxxxxxxxxxxxxxxxxxxx</span></p><p class=\"ql-align-justify\"><span style=\"background-color: rgb(238, 238, 238); color: black;\">3.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Subdirección de Control del Gasto y Financiamiento Electoral</span></p><p class=\"ql-align-justify\"><span style=\"background-color: rgb(238, 238, 238); color: black;\">4.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Oficina de Partes</span></p><p><br/></p><p><br/></p>" +
                        "</body>",
                Collections.<String, Object>emptyMap());
        byte[] bytePdf = pdfGenerator.generar(xhtmlGenerator, null);
        escribirDebugPdf(bytePdf);
        Assert.assertNotNull(bytePdf);
    }


    private void escribirDebugPdf(byte[] bytePdf) {
        try{
            File fileTemp = File.createTempFile(UUID.randomUUID().toString(), ".pdf");
            LOG.info("file pdf creado: {}", fileTemp);
            try(InputStream inputStream = new ByteArrayInputStream(bytePdf); OutputStream outputStream = new FileOutputStream(fileTemp)) {
                IOUtils.copy(inputStream, outputStream);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
