package cl.zeke.framework.pdf.core.api.resource;

/**
 * Created by takeda on 04-01-16.
 */
public class RawResource extends Resource {

    private byte[] raw;

    public RawResource(String name, ResourceType resourceType, byte[] raw) {
        this.raw = raw;
        this.setName(name);
        this.setResourceType(resourceType);
    }

    public byte[] getRaw() {
        return raw;
    }

}