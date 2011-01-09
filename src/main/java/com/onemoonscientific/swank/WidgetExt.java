/*
 *
 *
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file \"LICENSE\" for information on usage and redistribution
 * of this file.
 * IN NO EVENT SHALL THE AUTHORS OR DISTRIBUTORS BE LIABLE TO
 * ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR
 * CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OF THIS
 * SOFTWARE, ITS DOCUMENTATION, OR ANY DERIVATIVES THEREOF,
 * EVEN IF THE AUTHORS HAVE BEEN ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * THE AUTHORS AND DISTRIBUTORS SPECIFICALLY DISCLAIM ANY
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, AND NON-INFRINGEMENT.  THIS SOFTWARE
 * IS PROVIDED ON AN "AS IS" BASIS, AND THE AUTHORS AND
 * DISTRIBUTORS HAVE NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 *
 */
package com.onemoonscientific.swank;


/*
 * NvExtension.java --
 *
 */
import tcl.lang.*;


/*
 * This class implements a simple Tcl extension package "NvExtension". This
 * extension contains one Tcl command "nvcmd".  See the API documentation of
 * the tcl.lang.Extension class for details.
 */
public class WidgetExt extends Extension {
    /*
     * Create all the commands in the Simple package.
     */

    private static final String[] validCmds = {
        "button", "checkbutton", "entry", "frame", "html", "jcombobox",
        "jdesktoppane", "jdialog", "jfilechooser", "jinternalframe", "jmenu",
        "jmenubar", "joptionpane", "jpasswordfield", "jpopupmenu",
        "jprogressbar", "jscrollpane", "jsplitpane", "jtabbedpane", "jtable",
        "jtree", "jwindow", "label", "labelframe", "listbox", "menu",
        "menubutton", "message", "panedwindow", "radiobutton", "scale",
        "scrollbar", "text", "filedialog", "spinbox", "jtoolbar"
    };

    public void init(Interp interp) {
        Extension.loadOnDemand(interp, "button",
                "com.onemoonscientific.swank.SwkJButtonCmd");
        Extension.loadOnDemand(interp, "label",
                "com.onemoonscientific.swank.SwkJLabelCmd");
        Extension.loadOnDemand(interp, "checkbutton",
                "com.onemoonscientific.swank.SwkJCheckBoxCmd");
        Extension.loadOnDemand(interp, "jcombobox",
                "com.onemoonscientific.swank.SwkJComboBoxCmd");
        Extension.loadOnDemand(interp, "jdialog",
                "com.onemoonscientific.swank.SwkJDialogCmd");
        Extension.loadOnDemand(interp, "jfilechooser",
                "com.onemoonscientific.swank.SwkJFileChooserCmd");
        Extension.loadOnDemand(interp, "filedialog",
                "com.onemoonscientific.swank.SwkFileDialogCmd");
        Extension.loadOnDemand(interp, "jcolorchooser",
                "com.onemoonscientific.swank.SwkJColorChooserCmd");
        Extension.loadOnDemand(interp, "toplevel",
                "com.onemoonscientific.swank.SwkJFrameCmd");
        Extension.loadOnDemand(interp, "jwindow",
                "com.onemoonscientific.swank.SwkJWindowCmd");
        Extension.loadOnDemand(interp, "listbox",
                "com.onemoonscientific.swank.SwkJListCmd");
        Extension.loadOnDemand(interp, "jmenubar",
                "com.onemoonscientific.swank.SwkJMenuBarCmd");
        Extension.loadOnDemand(interp, "jmenu",
                "com.onemoonscientific.swank.SwkJMenuCmd");
        Extension.loadOnDemand(interp, "frame",
                "com.onemoonscientific.swank.SwkJPanelCmd");
        Extension.loadOnDemand(interp, "labelframe",
                "com.onemoonscientific.swank.SwkLabelFrameCmd");
        Extension.loadOnDemand(interp, "jpopupmenu",
                "com.onemoonscientific.swank.SwkJPopupMenuCmd");
        Extension.loadOnDemand(interp, "jprogressbar",
                "com.onemoonscientific.swank.SwkJProgressBarCmd");
        Extension.loadOnDemand(interp, "radiobutton",
                "com.onemoonscientific.swank.SwkJRadioButtonCmd");
        Extension.loadOnDemand(interp, "scale",
                "com.onemoonscientific.swank.SwkJSliderCmd");
        Extension.loadOnDemand(interp, "jsplitpane",
                "com.onemoonscientific.swank.SwkJSplitPaneCmd");
        Extension.loadOnDemand(interp, "panedwindow",
                "com.onemoonscientific.swank.SwkJSplitPaneCmd");
        Extension.loadOnDemand(interp, "spinbox",
                "com.onemoonscientific.swank.SwkJSpinnerCmd");
        Extension.loadOnDemand(interp, "jtabbedpane",
                "com.onemoonscientific.swank.SwkJTabbedPaneCmd");
        Extension.loadOnDemand(interp, "jtoolbar",
                "com.onemoonscientific.swank.SwkJToolBarCmd");
        Extension.loadOnDemand(interp, "joptionpane",
                "com.onemoonscientific.swank.SwkJOptionPaneCmd");
        Extension.loadOnDemand(interp, "jtable",
                "com.onemoonscientific.swank.SwkJTableCmd");
        Extension.loadOnDemand(interp, "message",
                "com.onemoonscientific.swank.SwkJTextAreaCmd");
        Extension.loadOnDemand(interp, "jpasswordfield",
                "com.onemoonscientific.swank.SwkJPasswordFieldCmd");
        Extension.loadOnDemand(interp, "entry",
                "com.onemoonscientific.swank.SwkJTextFieldCmd");
        Extension.loadOnDemand(interp, "text",
                "com.onemoonscientific.swank.SwkJTextPaneCmd");
        Extension.loadOnDemand(interp, "scrollbar",
                "com.onemoonscientific.swank.SwkJScrollBarCmd");
        Extension.loadOnDemand(interp, "jscrollpane",
                "com.onemoonscientific.swank.SwkJScrollPaneCmd");
        Extension.loadOnDemand(interp, "jtree",
                "com.onemoonscientific.swank.SwkJTreeCmd");
        Extension.loadOnDemand(interp, "menubutton",
                "com.onemoonscientific.swank.SwkSMenuButtonCmd");
        Extension.loadOnDemand(interp, "menu",
                "com.onemoonscientific.swank.SwkJMenuCmd");
        Extension.loadOnDemand(interp, "html",
                "com.onemoonscientific.swank.SwkJEditorPaneCmd");
        Extension.loadOnDemand(interp, "jinternalframe",
                "com.onemoonscientific.swank.SwkJInternalFrameCmd");
        Extension.loadOnDemand(interp, "jdesktoppane",
                "com.onemoonscientific.swank.SwkJDesktopPaneCmd");

        Extension.loadOnDemand(interp, "cursor",
                "com.onemoonscientific.swank.CursorCmd");
        Extension.loadOnDemand(interp, "image",
                "com.onemoonscientific.swank.ImageCmd");
        Extension.loadOnDemand(interp, "option",
                "com.onemoonscientific.swank.OptionCmd");
        Extension.loadOnDemand(interp, "bind",
                "com.onemoonscientific.swank.BindCmd");
        Extension.loadOnDemand(interp, "bindtags",
                "com.onemoonscientific.swank.BindTagsCmd");
        Extension.loadOnDemand(interp, "event",
                "com.onemoonscientific.swank.EventCmd");
        Extension.loadOnDemand(interp, "grid",
                "com.onemoonscientific.swank.GridCmd");
        Extension.loadOnDemand(interp, "pack",
                "com.onemoonscientific.swank.PackCmd");
        Extension.loadOnDemand(interp, "place",
                "com.onemoonscientific.swank.PlaceCmd");
        Extension.loadOnDemand(interp, "destroy",
                "com.onemoonscientific.swank.DestroyCmd");
        Extension.loadOnDemand(interp, "raise",
                "com.onemoonscientific.swank.RaiseCmd");
        Extension.loadOnDemand(interp, "lower",
                "com.onemoonscientific.swank.LowerCmd");
        Extension.loadOnDemand(interp, "selection",
                "com.onemoonscientific.swank.SelectionCmd");
        Extension.loadOnDemand(interp, "winfo",
                "com.onemoonscientific.swank.WinfoCmd");
        Extension.loadOnDemand(interp, "wm", "com.onemoonscientific.swank.WmCmd");
        Extension.loadOnDemand(interp, "font",
                "com.onemoonscientific.swank.FontCmd");
        Extension.loadOnDemand(interp, "focus",
                "com.onemoonscientific.swank.FocusCmd");
        Extension.loadOnDemand(interp, "bell",
                "com.onemoonscientific.swank.BellCmd");
        Extension.loadOnDemand(interp, "thread",
                "com.onemoonscientific.swank.ThreadCmd");
        Extension.loadOnDemand(interp, "jlabel",
                "com.onemoonscientific.swank.SwkJLabelCmd");
        Extension.loadOnDemand(interp, "clipboard",
                "com.onemoonscientific.swank.ClipboardCmd");
        Extension.loadOnDemand(interp, "eventrecorder",
                "com.onemoonscientific.swank.EventRecorderCmd");
        Extension.loadOnDemand(interp, "tk_messageBox",
                "com.onemoonscientific.swank.TkMessageBox");
        Extension.loadOnDemand(interp, "colorpicker",
                "com.onemoonscientific.swank.SwkColorPicker");
        Extension.loadOnDemand(interp, "embed",
                "com.onemoonscientific.swank.EmbedCmd");
        Extension.loadOnDemand(interp, "password",
                "com.onemoonscientific.swank.PasswordCmd");
    }
}
