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

proc swkMakeWidgetTypeCmd {} {
global widgetType widgetVar  specialImports specialWidgetTypeCmds

	if {[string match *Canvas* $widgetType]} {
	    set subdir .canvas
	} else {
		set subdir ""
	}

set swankCmd [string tolower [string range $widgetType 4 end]]
set widgetCmd "

/*
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file \"LICENSE\" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
*/




package com.onemoonscientific.swank$subdir;
import java.util.*;
import java.lang.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.tree.*;
import tcl.lang.*;
$specialImports

/** This class implements the $swankCmd command. */
public class ${widgetType}Cmd implements Command
\{
    /** Method creates the new widget object and calls configuration methods to process
     * command arguments.
     * @param interp The interpreter in which to create the command.
     * @param argv Argument array provides name of widget and command to be created in the first array element
     * and configuration options in the subsequent elements.
     * @throws TclException Exception thrown if an error occurs in creating the widget and the corresponding
     * Jacl command.
     */    

  public void
    cmdProc (Interp interp, TclObject argv\[\])
  throws TclException
  \{
    int i;
    if (argv.length < 2)
      \{
        throw new TclNumArgsException (interp, 1, argv, \"pathName ?options?\");
      \}
     if (!argv\[1\].toString().startsWith(\".\")) \{
          throw new TclException (interp, \"bad window path name \\\"\"+argv\[1\].toString()+\"\\\"\");
     \}
     ${widgetType} ${widgetVar} = null;
     String className = argv\[0\].toString().substring(0,1).toUpperCase()+argv\[0\].toString().substring(1);
     if (Widgets.exists(interp, argv\[1\].toString())) \{
        ${widgetVar} = (${widgetType}) Widgets.get(interp, argv\[1\].toString());
        if (${widgetVar}.isCreated()) \{
              throw new TclException (interp, \"window name \\\"\"+argv\[1\].toString()+\"\\\" already exists in parent\");
        \}
	${widgetVar}.className = new String(className);
     \} else \{
     if ((argv\[1\].toString().length() > 1) && Character.isUpperCase(argv\[1\].toString().charAt(1))) \{
          throw new TclException (interp, \"window name starts with an upper-case letter: \\\"\"+argv\[1\].toString().charAt(1)+\"\\\"\");
     \}
        ${widgetVar} = new ${widgetType} (interp, argv\[1\].toString(),className);
	${widgetVar}.className = new String(className);
        LinkedList children = null;
	interp.createCommand(argv\[1\].toString(), new ${widgetType}WidgetCmd());
        TclObject tObj = ReflectObject.newInstance (interp, ${widgetType}.class, ${widgetVar});
	tObj.preserve();
        ${widgetType}Configure.configure(interp,${widgetVar},argv,2);
        Widgets.addNewWidget(interp,argv\[1\].toString(), tObj);
        \}
        $specialWidgetTypeCmds
        ${widgetVar}.setCreated(true);
        BindCmd.addDefaultListeners(interp, ${widgetVar});
      	interp.setResult (argv\[1\].toString());
  \}

\}
"

return $widgetCmd

}
