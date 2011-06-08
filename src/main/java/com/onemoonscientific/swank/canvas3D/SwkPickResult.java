package com.onemoonscientific.swank.canvas3D;
   public class SwkPickResult {
        SwkShape swkShape;
        int index;
        int vertexIndex;
        SwkPickResult(final SwkShape swkShape, final int index,final int vertexIndex) {
            this.swkShape = swkShape;
            this.index = index;
            this.vertexIndex = vertexIndex;
        }
    }


