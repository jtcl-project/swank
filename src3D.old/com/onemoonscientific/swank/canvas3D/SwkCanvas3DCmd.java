package com.onemoonscientific.swank.canvas3D;

import com.onemoonscientific.swank.*;

import tcl.lang.*;


import java.lang.*;




class SwkCanvas3DCmd implements Command {
    public void cmdProc(Interp interp, TclObject[] argv)
        throws TclException {
        int i;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv, "pathName ?options?");
        }

        if (!argv[1].toString().startsWith(".")) {
            throw new TclException(interp,
                "bad window path name \"" + argv[1].toString() + "\"");
        }

        String className = argv[0].toString().substring(0, 1).toUpperCase() +
            argv[0].toString().substring(1);
        SwkCanvas3D swkCanvas3D = new SwkCanvas3D(interp, argv[1].toString(),
                className);

        //molCanvas3D.className = new String(className);
        // MolCanvas3DWidgetCmd.configure(interp,molCanvas3D,argv,2);
        interp.createCommand(argv[1].toString(), new SwkCanvasWidgetCmd());

        TclObject tObj = ReflectObject.newInstance(interp, SwkCanvas3D.class,
                swkCanvas3D);
        tObj.preserve();
        Widgets.addNewWidget(interp,argv[1].toString(), tObj);
        interp.setResult(argv[1].toString());
        swkCanvas3D.setCreated(true);

        BindCmd.addDefaultListeners(interp, swkCanvas3D);

        //BindCmd.applyBindings(interp,tObj,"Canvas3D");
    }
}
