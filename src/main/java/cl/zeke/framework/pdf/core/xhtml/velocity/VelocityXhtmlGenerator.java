package cl.zeke.framework.pdf.core.xhtml.velocity;

/**
 * Created by takeda on 04-01-16.
 */

import cl.zeke.framework.pdf.core.api.XhtmlGenerator;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class VelocityXhtmlGenerator implements XhtmlGenerator {

    private static Logger log = LoggerFactory.getLogger(VelocityXhtmlGenerator.class);

    static {
        /* first, we init the runtime engine.  Defaults are fine. */
        Velocity.clearProperty("runtime.log.logsystem.class");
        Velocity.addProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        Velocity.init();
    }

    private static SimpleDateFormat SDF = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");

    private String template;
    private Map<String, Object> scope;
    private Reader reader;

    public VelocityXhtmlGenerator(String template, Map<String, Object> scope) {
        this.template = template;
        this.scope = scope;
    }

    public VelocityXhtmlGenerator(Reader readerTemplate, Map<String, Object> scope) {
        this.reader = readerTemplate;
        this.scope = scope;
    }

    @Override
    public byte[] generar() {
        String marca = "xhtml_" + SDF.format(new Date());
        StringWriter sw = new StringWriter();
        VelocityContext context = crearVelocityContext();

        if (template != null) {
            Velocity.evaluate(context, sw, marca, template);
        } else if (reader != null) {
            Velocity.evaluate(context, sw, marca, reader);
        } else {
            throw new IllegalStateException("No template!");
        }
        return sw.toString().getBytes();
    }


    private VelocityContext crearVelocityContext() {
        /* lets make a Context and put data into it */
        VelocityContext context = new VelocityContext();
        if (scope == null) {
            return context;
        }
        for (String key : scope.keySet()) {
            context.put(key, scope.get(key));
        }
        return context;
    }

}