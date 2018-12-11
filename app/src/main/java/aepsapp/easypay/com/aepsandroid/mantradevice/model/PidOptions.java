package aepsapp.easypay.com.aepsandroid.mantradevice.model;

import android.text.TextUtils;
import android.util.Log;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.StringWriter;

@Root(name = "PidOptions", strict = false)
public class PidOptions {

    public PidOptions() {
    }

    @Attribute(name = "ver", required = false)
    public String ver;

    @Element(name = "Opts", required = false)
    public Opts Opts;

    @Element(name = "CustOpts", required = false)
    public CustOpts CustOpts;


    public static String getPIDOptions(String wadh)
    {
        try {
            String posh = "UNKNOWN";
            Opts opts = new Opts();
            opts.fCount = "1";
            opts.fType = "0";
            opts.iCount = "0";
            opts.iType = "0";
            opts.pCount = "0";
            opts.pType = "0";
            opts.format = "1";
            opts.pidVer = "2.0";
            opts.timeout = "10000";

            // get aadhar XML data
            if (!TextUtils.isEmpty(wadh)) {
                opts.wadh = wadh;
                opts.format = "0";
            }
            else
                opts.format = "1";

            opts.posh = posh;
            opts.env = "P";

            PidOptions pidOptions = new PidOptions();
            pidOptions.ver = "1.0";
            pidOptions.Opts = opts;

            Serializer serializer = new Persister();
            StringWriter writer = new StringWriter();
            serializer.write(pidOptions, writer);
            return writer.toString();
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
        return null;
    }
}
