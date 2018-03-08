package com.github.erodriguezg.pdfutils.core.xhtml.velocity;

/**
 * Created by takeda on 04-01-16.
 */

import com.github.erodriguezg.pdfutils.core.api.XhtmlGenerator;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.Reader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class VelocityXhtmlGenerator implements XhtmlGenerator {

    static {
        /* first, we init the runtime engine.  Defaults are fine. */
        Velocity.clearProperty("runtime.log.logsystem.class");
        Velocity.addProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        Velocity.init();
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
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

        String marca = "xhtml_" + sdf.format(new Date());
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
        for (Map.Entry<String,Object> entry : scope.entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }
        return context;
    }

}