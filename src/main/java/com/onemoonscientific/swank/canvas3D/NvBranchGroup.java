package com.onemoonscientific.swank.canvas3D;

import java.util.*;
import javax.media.j3d.*;



/**
 *
 * @author brucejohnson
 */
public class NvBranchGroup extends BranchGroup {
    public static Hashtable tagTable = new Hashtable();
    /**
     *
     */
    public static Hashtable groupTable = new Hashtable();
    public static int nextId = 1;
    public Vector tags = new Vector();
    public int id;
    /**
     *
     */
    public int iChild;

    NvBranchGroup() {
        /*
            id = nextId;
            System.out.println("add "+id);
            nextId++;
            groupTable.put(Integer.toString(id),this);
            tags.addElement(Integer.toString(id));
         */
         setCapability(ALLOW_CHILDREN_READ);
         setCapability(ALLOW_CHILDREN_WRITE);
    }

    /*
    public void remove() {
            System.out.println("remove "+id);
            groupTable.remove(Integer.toString(id));
    }

    public void addTag(String string)
    {
            for (int i=0;i<tags.size();i++) {
                    if (((String) tags.elementAt(i)).equals(string)) {
                            return;
                    }
            }
            tags.addElement(string);
    }
    public static Vector getTagList(String searchTag)
    {
    Vector result = new Vector();
    NvBranchGroup nvBG=null;
    String tag;
        Enumeration     e = groupTable.elements();
        while (e.hasMoreElements()) {
            nvBG = (NvBranchGroup) e.nextElement();
            for (int i=0;i<nvBG.tags.size();i++) {
                    tag = (String) nvBG.tags.elementAt(i);
                    System.out.println("tag "+i+" "+searchTag+" "+tag);
                    if (tag.equals(searchTag)) {
                            result.addElement(nvBG);
                    }
            }
        }
       return(result);
    }
     */
}
