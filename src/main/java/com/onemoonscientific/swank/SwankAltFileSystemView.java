package com.onemoonscientific.swank;

import tcl.lang.*;

import java.io.*;

import java.lang.reflect.*;


import javax.swing.filechooser.*;

/**
 * This class is necessary due to an annoying bug on Windows NT where
 * instantiating a JFileChooser with the default FileSystemView will
 * cause a "drive A: not ready" error every time. I grabbed the
 * Windows FileSystemView impl from the 1.3 SDK and modified it so
 * as to not use java.io.File.listRoots() to get fileSystem roots.
 * java.io.File.listRoots() does a SecurityManager.checkRead() which
 * causes the OS to try to access drive A: even when there is no disk,
 * causing an annoying "abort, retry, ignore" popup message every time
 * we instantiate a JFileChooser!
 *
 * Instead of calling listRoots() we use a straightforward alternate
 * method of getting file system roots.
 */
public class SwankAltFileSystemView extends FileSystemView {

    private static final Object[] noArgs = {};
    private static final Class[] noArgTypes = {};
    private static Method listRootsMethod = null;
    private static boolean listRootsMethodChecked = false;
    private boolean smode = false;
    private boolean debug = false;
    Interp interp = null;

    public SwankAltFileSystemView(Interp interp) {
        this.interp = interp;
    }

    @Override
    public File createFileObject(File dir, String filename) {
        return new SwankFile(interp, dir.getPath(), filename);
    }

    @Override
    public File createFileObject(String path) {
        return new SwankFile(interp, path);
    }

    /*  protected  File createFileSystemRoot(File f) {
    // Creates a new File object for f with correct behavior for a file system root directory.
    return null;
    }
     */
    public File createNewFolder(File containingDir) throws IOException {
        SwankFile result = new SwankFile(interp,
                containingDir.getPath() + "/NewFolder");

        if (result.mkdir()) {
            return result;
        }

        return null;
    }

    /*
    public File getChild(File parent, String fileName)
    {
    try {
    interp.eval("::fileSystemView::getChild {"+parent.toString()+"} {"+fileName+"}");
    }
    catch (TclException tclE)
    {
    return null;
    }
    return new File(interp.getResult().toString());
    }
     */
    @Override
    public File getDefaultDirectory() {
        //    Return the user's default starting directory for the file chooser.
        if (smode) {
            return super.getDefaultDirectory();
        } else {
            try {
                interp.eval("::fileSystemView::getDefaultDirectory");
            } catch (TclException tclE) {
                return null;
            }

            //System.out.println("gotdef "+interp.getResult().toString());
            return new SwankFile(interp, interp.getResult().toString());
        }
    }

    @Override
    public File[] getFiles(File dir, boolean useFileHiding) {
        if (debug) {
            System.out.println("getFiles " + dir);
        }

        if (smode) {
            return super.getFiles(dir, useFileHiding);
        } else {
            if (dir instanceof SwankFile) {
                if (debug) {
                    System.out.println("getFiles sk " + dir.getName());
                }

                return ((SwankFile) dir).listFiles();
            } else {
                if (debug) {
                    System.out.println("getFiles nk " + dir.getName());
                }

                return new SwankFile(interp, dir.getPath()).listFiles();
            }
        }
    }

    /*static FileSystemView getFileSystemView() {
    }
     */
    @Override
    public File getHomeDirectory() {
        if (smode) {
            return super.getHomeDirectory();
        } else {
            try {
                interp.eval("::fileSystemView::getHomeDirectory");
            } catch (TclException tclE) {
                return null;
            }

            // if (debug)  System.out.println("gothome "+interp.getResult().toString());
            return new SwankFile(interp, interp.getResult().toString());
        }
    }

    @Override
    public File getParentDirectory(File dir) {
        if (debug) {
            System.out.println("getParent " + dir.getName());
        }

        if (smode) {
            return super.getParentDirectory(dir);
        } else {
            String p = dir.getParent();

            if (p == null) {
                return null;
            }

            return new SwankFile(interp, p);
        }
    }

    @Override
    public File[] getRoots() {
        if (debug) {
            System.out.println("getRoots");
        }

        if (smode) {
            return super.getRoots();
        } else {
            return new SwankFile[]{
                        new SwankFile(interp, "" + SwankFile.ftpseparator)
                    };
        }
    }

    @Override
    public String getSystemDisplayName(File f) {
        if (debug) {
            System.out.println("getSystemDisplayName " + f.toString());
        }

        //return super.getSystemDisplayName(f);
        return f.getName();

        /*  //Name of a file, directory, or folder as it would be displayed in a system file browser.
        try {
        interp.eval("::fileSystemView::createFileObject {"+f.toString()+"}");
        }
        catch (TclException tclE)
        {
        return null;
        }
        return interp.getResult().toString();
         */
    }

    /*
    public Icon getSystemIcon (File f) {
    return icon;
    }
     */
    @Override
    public String getSystemTypeDescription(File f) {
        if (debug) {
            System.out.println("getSystemTypeDescription " + f.toString());
        }

        //return super.getSystemTypeDescription(f);
        return f.getName();

        /*
        //    Type description for a file, directory, or folder as it would be displayed in a system file browser.
        try {
        interp.eval("::fileSystemView::getSystemTypeDescription {"+f.toString()+"}");
        }
        catch (TclException tclE)
        {
        return null;
        }
        return interp.getResult().toString();
         */
    }

    @Override
    public boolean isComputerNode(File dir) {
        if (debug) {
            System.out.println("isCN " + dir);
        }

        boolean isComputerNode = false;

        if (smode) {
            isComputerNode = super.isComputerNode(dir);
        } else {
            isComputerNode = false;
        }

        if (debug) {
            System.out.println("isComputerNode " + isComputerNode);
        }

        return isComputerNode;

        /*    try {
        interp.eval("::fileSystemView::getSystemTypeDescription {"+dir.toString()+"}");
        return TclBoolean.get(interp, interp.getResult());
        }
        catch (TclException tclE)
        {
        return false;
        }
         */
    }

    @Override
    public boolean isDrive(File dir) {
        if (debug) {
            System.out.println("isDrive " + dir);
        }

        boolean isDrive = false;

        if (smode) {
            isDrive = super.isDrive(dir);
        } else {
            isDrive = false;
        }

        if (debug) {
            System.out.println("isDrive " + isDrive);
        }

        return isDrive;

        /*
        try {
        interp.eval("::fileSystemView::getSystemTypeDescription {"+dir.toString()+"}");
        return TclBoolean.get(interp, interp.getResult());
        }
        catch (TclException tclE)
        {
        return false;
        }
         */
    }

    @Override
    public boolean isFileSystem(File dir) {
        if (debug) {
            System.out.println("isFS " + dir);
        }

        boolean isFileSystem = false;

        if (smode) {
            isFileSystem = super.isFileSystem(dir);
        } else {
            isFileSystem = false;
        }

        if (debug) {
            System.out.println("isFileSystem " + isFileSystem);
        }

        return isFileSystem;

        /*
        try {
        interp.eval("::fileSystemView::getSystemTypeDescription {"+f.toString()+"}");
        return TclBoolean.get(interp, interp.getResult());
        }
        catch (TclException tclE)
        {
        return false;
        }
         */
    }

    @Override
    public boolean isFileSystemRoot(File dir) {
        if (debug) {
            System.out.println("isFSRoot " + dir);
        }

        boolean isFileSystemRoot = false;

        if (smode) {
            isFileSystemRoot = super.isFileSystemRoot(dir);
        } else {
            isFileSystemRoot = false;
        }

        if (debug) {
            System.out.println("isFileSystemRoot " + isFileSystemRoot);
        }

        return isFileSystemRoot;

        /*
        try {
        interp.eval("::fileSystemView::getSystemTypeDescription {"+dir.toString()+"}");
        return TclBoolean.get(interp, interp.getResult());
        }
        catch (TclException tclE)
        {
        return false;
        }
         */
    }

    @Override
    public boolean isFloppyDrive(File dir) {
        if (debug) {
            System.out.println("isFloppyDrive " + dir);
        }

        boolean isFloppyDrive = false;

        if (smode) {
            isFloppyDrive = super.isFloppyDrive(dir);
        } else {
            isFloppyDrive = false;
        }

        if (debug) {
            System.out.println("isFloppyDrive " + isFloppyDrive);
        }

        return isFloppyDrive;

        /*
        try {
        interp.eval("::fileSystemView::getSystemTypeDescription {"+dir.toString()+"}");
        return TclBoolean.get(interp, interp.getResult());
        }
        catch (TclException tclE)
        {
        return false;
        }
         **/
    }

    @Override
    public boolean isHiddenFile(File f) {
        boolean isHiddenFile = false;

        if (smode) {
            isHiddenFile = super.isHiddenFile(f);
        } else {
            isHiddenFile = false;
        }

        if (debug) {
            System.out.println("isHiddenFile " + isHiddenFile);
        }

        return isHiddenFile;
    }

    @Override
    public boolean isParent(File folder, File file) {
        if (debug) {
            System.out.println("isParent " + folder + " " + file);
        }

        boolean isParent = false;

        if (smode) {
            isParent = super.isParent(folder, file);
        } else {
            isParent = false;
        }

        if (debug) {
            System.out.println("isParent " + isParent);
        }

        return isParent;

        /*
        //    On Windows, a file can appear in multiple folders, other than its parent directory in the filesystem.
        if ((folder == null) || (file == null)) {
        return false;
        }
        try {
        interp.eval("::fileSystemView::getSystemTypeDescription {"+folder.toString()+"} {"+file.toString()+"}");
        return TclBoolean.get(interp, interp.getResult());
        }
        catch (TclException tclE)
        {
        return false;
        }
         */
    }

    @Override
    public boolean isRoot(File f) {
        if (debug) {
            System.out.println("isRoot");
        }

        boolean isRoot = false;

        if (smode) {
            isRoot = super.isRoot(f);
        } else {
            if (f instanceof SwankFile && (f.getParent() == null)
                    && (f.getName().length() == 0)) {
                isRoot = true;
            } else {
                isRoot = false;
            }
        }

        if (debug) {
            System.out.println("isRoot " + isRoot);
        }

        return isRoot;
    }

    @Override
    public Boolean isTraversable(File f) {
        if (debug) {
            System.out.println("isTravers " + f.getPath());
        }

        Boolean isTraversable;

        if (smode) {
            isTraversable = super.isTraversable(f);
        } else {
            isTraversable = Boolean.valueOf(true);
        }

        if (debug) {
            System.out.println("isTraversable " + isTraversable);
        }

        return isTraversable;

        /*
        //Returns true if the file (directory) can be visited.
        // Returns a File object constructed from the given path string
        try {
        interp.eval("::fileSystemView::getSystemTypeDescription {"+f.toString()+"}");
        return new Boolean(TclBoolean.get(interp, interp.getResult()));
        }
        catch (TclException tclE)
        {
        return null;
        }
         */
    }

    static class FileSystemRoot extends File {

        public FileSystemRoot(File f) {
            super(f, "");
        }

        public FileSystemRoot(String s) {
            super(s);
        }

        @Override
        public boolean isDirectory() {
            return true;
        }
    }
}
