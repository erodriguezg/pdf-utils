package cl.zeke.framework.pdf.core.flyingsaucer;

import cl.zeke.framework.pdf.core.api.resource.FileResource;
import cl.zeke.framework.pdf.core.api.resource.Resource;
import cl.zeke.framework.pdf.core.api.resource.ResourceType;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by eduar on 09/04/2017.
 */
public class CustomReplacedElementFactory implements ReplacedElementFactory {

    private static final Logger LOG = LoggerFactory.getLogger(CustomReplacedElementFactory.class);

    private final ReplacedElementFactory superFactory;

    private final Map<String, FileResource> imgFiles;

    public CustomReplacedElementFactory(ReplacedElementFactory superFactory, Resource[] resources) {
        this.superFactory = superFactory;
        imgFiles = filtrarImagenes(resources);
    }

    private Map<String, FileResource> filtrarImagenes(Resource[] resources) {
        if (resources == null || resources.length == 0) {
            return Collections.emptyMap();
        }
        return Arrays.asList(resources).stream()
                .filter(r -> r.getResourceType() == ResourceType.IMG)
                .collect(Collectors.toMap(r -> r.getName(), r -> (FileResource) r));
    }

    @Override
    public ReplacedElement createReplacedElement(LayoutContext layoutContext, BlockBox blockBox, UserAgentCallback userAgentCallback, int cssWidth, int cssHeight) {

        Element element = blockBox.getElement();
        if (element == null) {
            return null;
        }

        String nodeName = element.getNodeName();
        String srcName = element.getAttribute("src");
        String widthText = element.getAttribute("width");
        String heightText = element.getAttribute("height");

        if (!"img".equalsIgnoreCase(nodeName)
                && (srcName == null || srcName.trim().isEmpty())
                && !imgFiles.containsKey(srcName.trim())) {
            return superFactory.createReplacedElement(layoutContext, blockBox, userAgentCallback, cssWidth, cssHeight);
        }

        FileResource fileResource = imgFiles.get(srcName.trim());
        try (InputStream inputStream = new FileInputStream(fileResource.getFile())) {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            Image image = Image.getInstance(bytes);
            FSImage fsImage = new ITextFSImage(image);

            if (fsImage != null) {
                if ((cssWidth != -1) || (cssHeight != -1)) {
                    fsImage.scale(cssWidth, cssHeight);
                }
                return new ITextImageElement(fsImage);
            }
        } catch (IOException | BadElementException ex) {
            LOG.error("error al escribir imagen en pdf. ", ex);
            throw new IllegalStateException(ex);
        }

        return superFactory.createReplacedElement(layoutContext, blockBox, userAgentCallback, cssWidth, cssHeight);
    }

    @Override
    public void reset() {
        superFactory.reset();
    }

    @Override
    public void remove(Element e) {
        superFactory.remove(e);
    }

    @Override
    public void setFormSubmissionListener(FormSubmissionListener listener) {
        superFactory.setFormSubmissionListener(listener);
    }
}
