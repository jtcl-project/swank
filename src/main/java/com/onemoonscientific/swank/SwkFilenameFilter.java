package com.onemoonscientific.swank;

import tcl.lang.*;

import java.io.File;

import java.util.Enumeration;
import java.util.Hashtable;
import java.io.FilenameFilter;

public class SwkFilenameFilter implements FilenameFilter {

    private static String TYPE_UNKNOWN = "Type Unknown";
    private static String HIDDEN_FILE = "Hidden File";
    private Hashtable filters = null;
    private String description = null;
    private String fullDescription = null;
    private boolean useExtensionsInDescription = true;

    public SwkFilenameFilter() {
        this.filters = new Hashtable();
    }

    public SwkFilenameFilter(String extension) {
        this(extension, null);
    }

    public SwkFilenameFilter(String extension, String description) {
        this();

        if (extension != null) {
            addExtension(extension);
        }

        if (description != null) {
            setDescription(description);
        }
    }

    public SwkFilenameFilter(Interp interp, TclObject extensions, String description)
            throws TclException {
        this();

        TclObject[] argv = TclList.getElements(interp, extensions);

        for (int i = 0; i < argv.length; i++) {
            // add filters one by one
            addExtension(argv[i].toString());
        }

        if (description != null) {
            setDescription(description);
        }
    }

    public boolean accept(File path, String fileName) {
        if (path != null) {
            if (!path.isDirectory()) {
                return false;
            }

            String filter;
            Enumeration enumeration = filters.keys();

            while (enumeration.hasMoreElements()) {
                filter = (String) enumeration.nextElement();

                if (Util.stringMatch(fileName, filter)) {
                    return true;
                }

                ;
            }
        }

        return false;
    }

    public String getExtension(File f) {
        if (f != null) {
            String filename = f.getName();
            int i = filename.lastIndexOf('.');

            if ((i > 0) && (i < (filename.length() - 1))) {
                return filename.substring(i + 1).toLowerCase();
            }

            ;
        }

        return null;
    }

    public void addExtension(String extension) {
        if (filters == null) {
            filters = new Hashtable(5);
        }

        filters.put(extension.toLowerCase(), this);
        fullDescription = null;
    }

    public String getDescription() {
        if (fullDescription == null) {
            if ((description == null) || isExtensionListInDescription()) {
                fullDescription = (description == null) ? "(" : (description
                        + " (");

                // build the description from the extension list
                Enumeration extensions = filters.keys();

                if (extensions != null) {
                    fullDescription += (String) extensions.nextElement();

                    while (extensions.hasMoreElements()) {
                        fullDescription += (", "
                                + (String) extensions.nextElement());
                    }
                }

                fullDescription += ")";
            } else {
                fullDescription = description;
            }
        }

        return fullDescription;
    }

    public void setDescription(String description) {
        this.description = description;
        fullDescription = null;
    }

    public void setExtensionListInDescription(boolean b) {
        useExtensionsInDescription = b;
        fullDescription = null;
    }

    public boolean isExtensionListInDescription() {
        return useExtensionsInDescription;
    }
}
