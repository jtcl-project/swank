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
/**
 *
 * @author  JOHNBRUC
 * @version
 */
package com.onemoonscientific.swank.chart;

import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import java.util.*;

public class BoxAndWhiskerData extends DefaultBoxAndWhiskerCategoryDataset {

    static HashMap datasetMap = new HashMap();
    static int id = 0;
    String name = "";

    public BoxAndWhiskerData() {
        name = "xyData" + id;
        datasetMap.put(name, (Object) this);
        id++;
    }

    public BoxAndWhiskerData(String name) {
        this.name = name;
        datasetMap.put(name, (Object) this);
    }

    public void remove(String name) {
        datasetMap.remove(name);
    }

    public static BoxAndWhiskerTableData get(String name) {
        return (BoxAndWhiskerTableData) datasetMap.get(name);
    }

    public String getName() {
        return name;
    }
}

