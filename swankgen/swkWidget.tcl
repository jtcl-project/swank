#
# 
# Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, NJ, USA
#
# See the file \"LICENSE\" for information on usage and redistribution
# of this file.
# IN NO EVENT SHALL THE AUTHORS OR DISTRIBUTORS BE LIABLE TO
# ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR
# CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OF THIS
# SOFTWARE, ITS DOCUMENTATION, OR ANY DERIVATIVES THEREOF,
# EVEN IF THE AUTHORS HAVE BEEN ADVISED OF THE POSSIBILITY OF
# SUCH DAMAGE.
#
# THE AUTHORS AND DISTRIBUTORS SPECIFICALLY DISCLAIM ANY
# WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
# WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
# PURPOSE, AND NON-INFRINGEMENT.  THIS SOFTWARE
# IS PROVIDED ON AN "AS IS" BASIS, AND THE AUTHORS AND
# DISTRIBUTORS HAVE NO OBLIGATION TO PROVIDE MAINTENANCE,
# SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
#
#

proc swkMakeResources {resources} {

set result "private static void initResources() \{
	ResourceObject resourceObject=null;
"

foreach resource $resources {
	set resourceDash [lindex $resource 0]
	set resourceName [lindex $resource 1]
	set resourceClass [lindex $resource 2]
	append result "
		resourceObject = new ResourceObject(\"$resourceName\",\"$resourceClass\");
		resourceDB.put(\"$resourceDash\",resourceObject);
	"
}
	append result \}
return $result
}


proc swkMakeWidget {} {
global widgetType widgetClass specialVars specialMethods specialInits resources specialImports
global specialListeners tcljava

set swankCmd [string tolower [string range  $widgetType 4 end]]
	if {[string match *Canvas* $widgetType]} {
	    set subdir .canvas
	} else {
		set subdir ""
	}
if {[string compare $tcljava(java.version) 1.2.0] != -1} {
	set printable Printable
	append specialImports "\nimport java.awt.print.*;"
	set printMethod "
        public int print(Graphics g,final PageFormat pageFormat,int pageIndex) throws PrinterException
        \{
          if (pageIndex > 0) \{
            return NO_SUCH_PAGE;
          \}

          Graphics2D g2 = (Graphics2D) g;
          g2.translate(pageFormat.getImageableX(),pageFormat.getImageableY());
          boolean wasBuffered = SwankUtil.disableDoubleBuffering(this);
          paint(g2);
          SwankUtil.restoreDoubleBuffering(this,wasBuffered);
          System.out.println(\"print this page\");
          return PAGE_EXISTS;
        \}
	"
} else {
	set printable ""
	set printMethod ""
}

        append specialImports "\nimport org.freehep.graphics2d.VectorGraphics;"
	set exportMethod {
            public void drawVecMode(VectorGraphics g) {
            }
            public void paintComponent(Graphics g,final PageFormat pageFormat,int pageIndex) throws PrinterException {
              if (g instanceof VectorGraphics) {
                   drawVecMode((VectorGraphics) g);
                   return;
              }
            }
	}

set SwkWidget "
/*
 * Copyright (c) 2000 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file \"LICENSE\" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
*/

package com.onemoonscientific.swank$subdir;
import java.util.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.net.URL;
import java.io.IOException;
import javax.swing.text.html.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import tcl.lang.*;
$specialImports
/** Class for objects which represent a Swank $swankCmd widget. */
public class $widgetType extends $widgetClass implements SwkWidget,Printable$specialListeners \{
String name=null;
String className=null;
LinkedList children = null;
Vector virtualBindings = null;
int active=0;
boolean created=false;
TclObject tclObject=null;
static Hashtable resourceDB = null;
Interp interp;
Insets emptyBorderInsets = new Insets(0,0,0,0);
Dimension minimumSize = null;
Vector tagList = new Vector();
$specialVars
${widgetType}(Interp interp, String name, String className) \{
        this.name=name.intern();
		this.interp = interp;
        if (resourceDB == null) \{
		resourceDB = new Hashtable();
		initResources();
	\}
        $specialInits
	tagList.add(name);
	tagList.add(\"swank\");
	tagList.add(\"all\");
\}

$printMethod
void setResourceDefaults()
{
String keyName;
TclObject tObj;

Enumeration     e = ${widgetType}.resourceDB.keys();
while (e.hasMoreElements()) {
	TclObject list1 = TclList.newInstance();
	keyName = (String) e.nextElement();
	if (keyName == null) {continue;}
	ResourceObject ro = (ResourceObject) ${widgetType}.resourceDB.get(keyName);
	if (ro == null) {continue;}
	tObj = TclString.newInstance(keyName);
	try {
	try {
        	ro.defaultVal = ${widgetType}Configure.jget(interp,this,tObj);
	} catch (IllegalComponentStateException icsE)
	{ continue;}
	} catch (TclException tclE)
	{ continue;}
}
}
public Vector getTagList ()
\{
return (tagList);
\}
public void setTagList (Interp interp, TclObject tagListObj) throws TclException
\{
TclObject tagObjs\[\] = TclList.getElements(interp,tagListObj);
tagList.setSize(0);
for (int i = 0;i<tagObjs.length;i++) \{
	tagList.add(tagObjs\[i\].toString());
\}
\}


public void  setVirtualBindings (Vector bindings)
\{
virtualBindings = bindings;
\}
public Vector  getVirtualBindings ()
\{
return (virtualBindings);
\}
public Insets getEmptyBorderInsets ()
\{
return (emptyBorderInsets);
\}
public LinkedList getChildrenList ()
\{
return (children);
\}
public void initChildrenList ()
\{
children = new LinkedList();
\}
public String getName ()
\{
return (name);
\}
 
$specialMethods
[swkMakeResources $resources]
\}
"
return $SwkWidget
}
