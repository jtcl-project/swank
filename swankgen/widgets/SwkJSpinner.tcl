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

append specialVars {
}

append specialInits {
        SpinnerNumberModel model = new SpinnerNumberModel(1.0,0.0,100.0,1.0);
        setModel(model);
}
  


append specialMethods {
     public void setVarName(Interp interp, String name) throws TclException {
//            commandListener.setVarName(interp,name);
     }
     public void setIncrement(double step) {
        Object model = getModel();
         if ((model != null) && (model instanceof SpinnerNumberModel)) {
            SpinnerNumberModel spinModel = (SpinnerNumberModel) model;
            spinModel.setStepSize(step);
         }
     }
     public double getIncrement() {
         double result = 1.0;
         Object model = getModel();
         if ((model != null) && (model instanceof SpinnerNumberModel)) {
            SpinnerNumberModel spinModel = (SpinnerNumberModel) model;
            result = spinModel.getStepSize().doubleValue();
         }
        return result;
     }
     public void setFrom(double min) {
         Object model = getModel();
         if ((model != null) && (model instanceof SpinnerNumberModel)) {
            SpinnerNumberModel spinModel = (SpinnerNumberModel) model;
            spinModel.setMinimum(min);
         }
     }
     public double getFrom() {
         double result = 0.0;
         Object model = getModel();
         if ((model != null) && (model instanceof SpinnerNumberModel)) {
            SpinnerNumberModel spinModel = (SpinnerNumberModel) model;
            result = ((Double) spinModel.getMinimum()).doubleValue();
         }
        return result;
     }
     public void setTo(double max) {
        Object model = getModel();
         if ((model != null) && (model instanceof SpinnerNumberModel)) {
            SpinnerNumberModel spinModel = (SpinnerNumberModel) model;
            spinModel.setMaximum(max);
         }

     }
     public double getTo() {
        double result = 10.0;
         Object model = getModel();
         if ((model != null) && (model instanceof SpinnerNumberModel)) {
            SpinnerNumberModel spinModel = (SpinnerNumberModel) model;
            result = ((Double) spinModel.getMaximum()).doubleValue();
         }
        return result;
     }
     public Object getValues() {
        java.util.List list = null;
        Object model = getModel();
        if (model instanceof SpinnerListModel) {
            SpinnerListModel spinModel = (SpinnerListModel) model;
            list =  (java.util.List) spinModel.getList();
        } else {
            list = new ArrayList();
        }
        return list;
     }
     public void setValues(java.util.List values) {
        Object model = getModel();
        if ((values == null) || (values.size() == 0)) {
            if ((model == null) || !(model instanceof SpinnerNumberModel)) {
                SpinnerNumberModel numberModel = new SpinnerNumberModel(1.0,0.0,100.0,1.0);
                setModel(numberModel);
            }
        } else {
            if ((model == null) || !(model instanceof SpinnerListModel)) {
                SpinnerListModel spinModel = new SpinnerListModel(values);
                setModel(spinModel);
            } else {
                SpinnerListModel spinModel = (SpinnerListModel) model;
                spinModel.setList(values);
            }
        }
     }

}

lappend specialGets "setValues spinlist values -values"
lappend specialGets "setFrom double from -from"
lappend specialGets "setTo double to -to"
lappend specialGets "setIncrement double increment -increment"

