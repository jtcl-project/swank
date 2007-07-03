proc getSetter {widgetVar mode} {
    switch $mode {
        "int" {
            set result {int value = TclInteger.get(interp, argv[i+1]);}
        }
        "boolean" {
            set result {boolean value = TclBoolean.get(interp, argv[i+1]);}
        }
        "float" {
            set result {float value = (float) TclDouble.get(interp, argv[i+1]);}
        }
        "double" {
           set result {double value = TclDouble.get(interp, argv[i+1]);}
        }
        "java.lang.String" {
           set result {String value = argv[i+1].toString();}
        }
        "justify" {
           set result {String value = SwankUtil.getJustify(interp,argv[i+1]);}
        }
        "orient" {
           set result {int value = SwankUtil.getOrient(interp,argv[i+1]);}
        }
        "wrap" {
           set result {String value = SwankUtil.getWrap(interp,argv[i+1]);}
        }
        "state" {
           set result {String value = SwankUtil.getState(interp,argv[i+1]);}
        }
        "tkRelief" {
           set result {String value = SwankUtil.getTkRelief(interp,argv[i+1]);}
        }
        "tkSize" {
           set result "int value = SwankUtil.getTkSize(interp,(Component) this,argv\[i+1\]);"
        }
        "tkSizeD" {
           set result "double value = SwankUtil.getTkSizeD(interp,(Component) this,argv\[i+1\]);"
        }
        "tkSizeDI" {
           set result "double value = SwankUtil.getTkSizeD(interp,(Component) this,argv\[i+1\]);"
        }
        "tkSelectMode" {
           set result {int value = SwankUtil.getTkSelectMode(interp,argv[i+1]);}
        }
        "anchor" {
           set result {Object value  = SwankUtil.getAnchor(interp,argv[i+1]);}
        }
        "anchor2" {
           set result {Object value  = SwankUtil.getAnchorConstants(interp,argv[i+1].toString());}
        }
        "menu" {
           set result {Object value  = SwankUtil.getMenu(interp,argv[i+1].toString());}
        }
        "tkRectangle" {
           set result "Object value  = SwankUtil.getTkRectangle(interp,(Component) this,argv\[i+1\]);"
        }
        "tkRectangleCorners" {
           set result "Object value  = SwankUtil.getTkRectangleCorners(interp,(Component) this,argv\[i+1\]);"
        }
        "bitmap" {
           set result {Object value  = SwankUtil.getImageIcon(interp,argv[i+1]);
           String sValue = argv[i+1].toString();
           }
        }
        "javax.swing.Icon" {
           set result {Object value  = SwankUtil.getImageIcon(interp,argv[i+1]);}
        }
        "java.io.File" {
           set result {Object value  = SwankUtil.getFile(interp,argv[i+1]);}
        }
        "java.net.URL" {
           set result {Object value  = SwankUtil.getURL(interp,argv[i+1]);}
        }
        "options" {
           set result {Object value  = SwankUtil.getOptions(interp,argv[i+1]);}
        }
    }
    return $result
}
    
