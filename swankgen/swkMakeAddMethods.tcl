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

proc swkMakeAddMethods {methodGroups} {
global widgetType widgetVar

set components {
	java.awt.Component
	java.lang.Object
	java.awt.PopupMenu
	javax.swing.colorchooser.AbstractColorChooserPanel
	javax.swing.Action
	javax.swing.JMenuItem
	javax.swing.JMenu
	javax.swing.table.TableColumn
	javax.swing.filechooser.FileFilter
	java.awt.Dimension
}
set throwClause "if (tObj == null) \{throw new TclException(interp, \"Object not found\");\n\}"


set result ""
set usesString 0
set usesObjectString 0
foreach "methodClass methods" $methodGroups {
foreach method $methods {
   if {[string match add* $method]} {
   	if {[llength $method] == 2} {
   	    if {![string match *Listen* $method]} {
   		if {![string match *Component* $method]} {
   			if {![string match *Notify* $method]} {
   				if {[regexp  {(add)(.*)( )(.*)} $method all a b c]} {
                                	set argType [lindex $method 1]
                                	set option [lindex $method 0]
                                	set dashOption -[string tolower [string range $option 3 3]][string range $option 4 end]
					if {[lsearch $components $argType ] >= 0} {
							set cmd ""
              						append cmd "if (object instanceof $argType) \{\n"
							append cmd "\n${option}(($argType) object);\n"
							append cmd "Object widgetObj = $widgetVar;
                                                        if (widgetObj instanceof JComponent) {
                                                                ((JComponent) widgetObj).revalidate();
                                                        }\n"
							append cmd "return;\n"
							append cmd "\}\n"
		
                                                	append result $cmd\n
                                       	} elseif {$argType == "java.lang.String"} {
							set usesString 1
							set stringCommand $option
                                        } else {
							puts "not comp $method"
					}
                                       	if {$argType == "java.lang.Object"} {
							set usesObjectString 1
							set objectCommand $option
					}
				}
   			}
   		}
	} 
    } else {
		if {[lindex $method 0] != "add"} {
		}
    } 
    }
}
}

if {$usesString} {
	set cmd ""
	append cmd " else \{"
	append cmd "\n${stringCommand}(tobjString);\n"
	append cmd "return;\n"
	append cmd "\}"
	append result $cmd\n
} elseif {$usesObjectString != 0} {
	set cmd ""
	append cmd " else \{"
	append cmd "\n${objectCommand}(tobjString);\n"
	append cmd "return;\n"
	append cmd "\}"
	append result $cmd\n
} else {
	#set cmd $throwClause
	#append result $cmd\n
}
	
return $result
}
