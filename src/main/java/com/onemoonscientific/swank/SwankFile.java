package com.onemoonscientific.swank;

import tcl.lang.*;

import java.io.File;
import java.io.FilenameFilter;

import java.text.*;

import java.util.*;

public class SwankFile extends File {

    final static DateFormat dateformat = new SimpleDateFormat("MMM dd yyyy");
    final static String PATHDELIMS = "/\\";
    public final static char ftpseparator = '/';
    private Interp interp;
    private boolean checked;
    private boolean isDirectory;
    private boolean isFile;
    private boolean canRead;
    private boolean canWrite;
    private boolean isAbsolute;
    private boolean isHidden = false;
    private String path;
    private long length;
    private long modified;
    private boolean debug = false;
    boolean smode = false;

    public SwankFile(Interp interp, String parent) {
        super(parent);
        this.interp = interp;
        path = parent;
    }

    public SwankFile(Interp interp, String parent, String name) {
        this(interp, parent);

        if ((path.length() > 0)
                && (path.charAt(path.length() - 1) == ftpseparator)) {
            path += name;
        } else {
            path += (ftpseparator + name);
        }
    }

    public boolean canRead() {
        if (smode) {
            canRead = super.canRead();
        } else {
            if (!checked) {
                exists();
            }
        }

        if (debug) {
            System.out.println("canRead " + canRead);
        }

        return canRead;
    }

    public boolean canWrite() {
        if (smode) {
            canWrite = super.canWrite();
        } else {
            if (!checked) {
                exists();
            }
        }

        if (debug) {
            System.out.println("canWrite " + canWrite);
        }

        return canWrite;
    }

    public boolean equals(Object obj) {
        boolean equals = false;

        if (smode) {
            equals = super.equals(obj);
        } else {
            if (obj == null) {
                if (debug) {
                    System.out.println("equal null");
                }

                return false;
            }

            if (debug) {
                System.out.println(path);
            }

            if (debug) {
                System.out.println(obj.toString());
            }

            if (!checked) {
                exists();
            }

            equals = path.equals(obj.toString());
        }

        if (debug) {
            System.out.println("equals " + equals);
        }

        return equals;
    }

    public boolean exists() {
        if (debug) {
            System.out.println("exists");
        }

        boolean exists = false;

        if (smode) {
            exists = super.exists();
        } else {
            TclObject[] args = null;

            //System.out.println("exists "+path);
            try {
                interp.eval("::swankFile::exists {" + path + "}");
                args = TclList.getElements(interp, interp.getResult());
                isDirectory = false;
                isFile = false;
                canRead = false;
                canWrite = false;
                isAbsolute = false;
                length = 0;

                if ((args == null) || (args.length == 0)) {
                    return false;
                }

                if (args[0].toString().equals("0")) {
                    return false;
                }

                for (int i = 1; i < args.length; i += 2) {
                    if (args[i].toString().startsWith("isdir")) {
                        isDirectory = TclBoolean.get(interp, args[i + 1]);
                    } else if (args[i].toString().startsWith("isfi")) {
                        isFile = TclBoolean.get(interp, args[i + 1]);
                    } else if (args[i].toString().startsWith("pathtype")) {
                        isAbsolute = args[i + 1].toString().equals("absolute");
                    } else if (args[i].toString().startsWith("canr")) {
                        canRead = TclBoolean.get(interp, args[i + 1]);
                    } else if (args[i].toString().startsWith("canw")) {
                        canWrite = TclBoolean.get(interp, args[i + 1]);
                    } else if (args[i].toString().startsWith("len")) {
                        length = Long.parseLong(args[i + 1].toString());
                    } else if (args[i].toString().startsWith("mtim")) {
                        modified = Long.parseLong(args[i + 1].toString());
                    }
                }
            } catch (TclException tclE) {
                return false;
            }

            checked = true;
            exists = isDirectory || isFile;
        }

        if (debug) {
            System.out.println("exists " + exists);
        }

        return exists;
    }

    public String getAbsolutePath() {
        if (debug) {
            System.out.println("getAbsPath");
        }

        if (smode) {
            return super.getAbsolutePath();
        } else {
            if (!isAbsolute()) {
                try {
                    //System.out.println("get abspath");
                    interp.eval("::swankFile::getAbsolutePath {" + path + "}");

                    return interp.getResult().toString();
                } catch (TclException tclE) {
                }
            }

            return path;
        }
    }

    public String getCanonicalPath() {
        if (debug) {
            System.out.println("canCanPath");
        }

        if (smode) {
            return super.getAbsolutePath();
        } else {
            return getAbsolutePath();
        }
    }

    public String getName() {
        if (debug) {
            System.out.println("getName");
        }

        if (smode) {
            return getName();
        } else {
            String result = null;
            StringTokenizer st = new StringTokenizer(path, PATHDELIMS);

            while (st.hasMoreTokens()) {
                result = st.nextToken();
            }

            if ((result == null)
                    || ((result.length() > 1) && (result.charAt(1) == ':'))) {
                result = "";
            }

            return result;
        }
    }

    public String getParent() {
        if (debug) {
            System.out.println("getParent");
        }

        if (smode) {
            return super.getParent();
        } else {
            String result = null;
            String part = null;
            StringTokenizer st = new StringTokenizer(getAbsolutePath(),
                    PATHDELIMS);

            while (st.hasMoreTokens()) {
                if (result == null) {
                    result = "";
                } else {
                    result += part;
                }

                part = ftpseparator + st.nextToken();
            }

            if ((result != null) && (result.length() == 0) && isAbsolute()) {
                result += ftpseparator;
            }

            return result;
        }
    }

    public String getPath() {
        if (debug) {
            System.out.println("getPath");
        }

        if (smode) {
            return super.getPath();
        } else {
            return path;
        }
    }

    public int hashCode() {
        if (debug) {
            System.out.println("hash");
        }

        if (smode) {
            return super.hashCode();
        } else {
            return path.hashCode() ^ 1234321;
        }
    }

    public boolean isAbsolute() {
        if (smode) {
            isAbsolute = super.isAbsolute();
        } else {
            if (!checked) {
                exists();
            }
        }

        if (debug) {
            System.out.println("isAbsolute " + isAbsolute);
        }

        return isAbsolute;
    }

    public boolean isDirectory() {
        if (smode) {
            isDirectory = super.isDirectory();
        } else {
            if (!checked) {
                exists();
            }
        }

        if (debug) {
            System.out.println("isDirectory " + isDirectory);
        }

        return isDirectory;
    }

    public boolean isFile() {
        if (smode) {
            isFile = super.isFile();
        } else {
            if (!checked) {
                exists();
            }
        }

        if (debug) {
            System.out.println("isFile " + isFile);
        }

        return isFile;
    }

    public boolean isHidden() {
        if (smode) {
            isHidden = super.isHidden();
        } else {
            if (!checked) {
                exists();
            }
        }

        if (debug) {
            System.out.println("isHidden " + isHidden);
        }

        return isHidden;
    }

    public long lastModified() {
        if (debug) {
            System.out.println("lastMod");
        }

        if (smode) {
            return super.lastModified();
        } else {
            if (!checked) {
                exists();
            }

            return modified;
        }
    }

    public long length() {
        if (debug) {
            System.out.println("length");
        }

        if (smode) {
            return super.length();
        } else {
            if (!checked) {
                exists();
            }

            return length;
        }
    }

    public String[] list() {
        if (debug) {
            System.out.println("list");
        }

        if (smode) {
            return super.list();
        } else {
            return list(null);
        }
    }

    public String[] list(FilenameFilter filter) {
        if (debug) {
            System.out.println("list fnamefilter");
        }

        if (smode) {
            return super.list(filter);
        } else {
            Vector names = new Vector();
            TclObject[] fileList = null;

            try {
                interp.eval("::swankFile::listFiles {" + path + "}");
                fileList = TclList.getElements(interp, interp.getResult());
            } catch (TclException tclE) {
                if (debug) {
                    System.out.println("list in catch " + tclE.toString());
                }
            }

            if (fileList != null) {
                SwankFile f;

                for (int i = 0; i < fileList.length; i++) {
                    f = new SwankFile(interp, fileList[i].toString());

                    if ((filter == null) || filter.accept(this, f.getName())) {
                        names.addElement(f.getName());
                    }
                }
            }

            String[] result = new String[names.size()];
            names.copyInto(result);

            return result;
        }
    }

    public File[] listFiles() {
        if (debug) {
            System.out.println("listFiles");
        }

        if (smode) {
            return super.listFiles();
        } else {
            return listFiles((FileFilter) null);
        }
    }

    public File[] listFiles(FileFilter filter) {
        if (debug) {
            System.out.println("listfiles FileFilter");
        }

        if (smode) {
            return super.listFiles((java.io.FileFilter) filter);
        } else {
            Vector files = new Vector();
            TclObject[] fileList = null;

            try {
                if (debug) {
                    System.out.println("calling listFiles with " + path);
                }

                interp.eval("::swankFile::listFiles {" + path + "}");

                if (debug) {
                    System.out.println("getting result");
                }

                fileList = TclList.getElements(interp, interp.getResult());

                if (debug) {
                    System.out.println("got result");
                }
            } catch (TclException tclE) {
                if (debug) {
                    System.out.println("listFiles in catch " + tclE.toString());
                }
            }

            if (fileList != null) {
                SwankFile f;

                for (int i = 0; i < fileList.length; i++) {
                    if ((fileList[i] != null)
                            && (fileList[i].toString().length() != 0)) {
                        f = new SwankFile(interp, fileList[i].toString());

                        if (f != null) {
                            if ((filter == null) || filter.accept(f)) {
                                files.addElement(f);
                            }
                        }
                    }
                }
            }

            SwankFile[] result = new SwankFile[files.size()];
            files.copyInto(result);

            return result;
        }
    }

    /*
    public String getParent() {
    try {
    interp.eval("::swankFile::getParent {"+path+"}");
    return interp.getResult().toString();
    }
    catch (TclException tclE)
    {
    }
    return "";
    }
     */
    public boolean mkdir() {
        try {
            interp.eval("::swankFile::mkdir {" + getPath() + "}");

            return TclBoolean.get(interp, interp.getResult());
        } catch (TclException tclE) {
            return false;
        }
    }

    public boolean renameTo(File dest) {
        try {
            interp.eval("::swankFile::renameTo {" + getName() + "} {"
                    + dest.getName() + "}");

            return TclBoolean.get(interp, interp.getResult());
        } catch (TclException tclE) {
            return false;
        }
    }

    interface FileFilter {

        public boolean accept(File pathname);
    }
}
