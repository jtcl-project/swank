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
    String textVariable="";
    SwkJSpinnerListener commandListener=null;
}

append specialInits {
        SpinnerNumberModel model = new SpinnerNumberModel(1,0,100,1);
        setModel(model);
        commandListener = new SwkJSpinnerListener(interp,this);
        addChangeListener(commandListener);
 
}
append specialListeners {}
  
append specialMethods {
           public void setTextVariable(String name) {
           }
           public void setTextVariable(Interp interp, String name) throws TclException {
                 commandListener.setVarName(interp,name);
           }
            public String getTextVariable() {
                return(commandListener.getVarName());
            }


     public void setIncrement(double step) {
        Object model = getModel();
         if ((model != null) && (model instanceof SpinnerNumberModel)) {
            SpinnerNumberModel spinModel = (SpinnerNumberModel) model;
            final boolean isDouble;
            if (Math.abs(step-((int) step)) < 1.0e-6) {
                isDouble = false; 
            } else {
                isDouble = true; 
            }
            Number value = spinModel.getNumber();
            if (value instanceof Double) {
                if (isDouble) {
                   spinModel.setStepSize(step);
                } else {
                    int toValue = ((Number) spinModel.getMinimum()).intValue();
                    int fromValue = ((Number) spinModel.getMaximum()).intValue();
                    int curValue = value.intValue();
                    if ((curValue < fromValue) || (curValue > toValue)) {
                        curValue = fromValue;
                    }
                    SpinnerNumberModel numberModel = new SpinnerNumberModel(curValue,toValue,fromValue,(int) step);
                    setModel(numberModel);
                }
            } else {
                if (isDouble) {
                    double toValue = ((Number) spinModel.getMinimum()).doubleValue();
                    double fromValue = ((Number) spinModel.getMaximum()).doubleValue();
                    double curValue = value.doubleValue();
                    if ((curValue < fromValue) || (curValue > toValue)) {
                        curValue = fromValue;
                    }
                    SpinnerNumberModel numberModel = new SpinnerNumberModel(curValue,toValue,fromValue, step);
                    setModel(numberModel);
                } else {
                   spinModel.setStepSize((int) step);
                }
            }
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
            Number value = spinModel.getNumber();
            if ((value instanceof Double)) {
                spinModel.setMinimum(min);
            } else {
                spinModel.setMinimum((int) min);
            }
         }
     }
     public double getFrom() {
         double result = 0.0;
         Object model = getModel();
         if ((model != null) && (model instanceof SpinnerNumberModel)) {
            SpinnerNumberModel spinModel = (SpinnerNumberModel) model;
            result = ((Number) spinModel.getMinimum()).doubleValue();
         }
        return result;
     }
     public void setTo(double max) {
        Object model = getModel();
         if ((model != null) && (model instanceof SpinnerNumberModel)) {
            SpinnerNumberModel spinModel = (SpinnerNumberModel) model;
            Number value = spinModel.getNumber();
            if ((value instanceof Double)) {
                spinModel.setMaximum(max);
            } else {
                spinModel.setMaximum((int) max);
            }
         }
     }
     public double getTo() {
        double result = 10.0;
         Object model = getModel();
         if ((model != null) && (model instanceof SpinnerNumberModel)) {
            SpinnerNumberModel spinModel = (SpinnerNumberModel) model;
            result = ((Number) spinModel.getMaximum()).doubleValue();
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
                SpinnerNumberModel numberModel = new SpinnerNumberModel(1,0,100,1);
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
     public void setCommand(String name) {
         commandListener.setCommand(name);
     }
     public String getCommand() {
         return(commandListener.getCommand());
     }

}
set closeMethod {
    public void close() throws TclException {
        if ((getTextVariable() != null) && (getTextVariable().length() != 0)) {
             interp.untraceVar(getTextVariable(),commandListener,TCL.TRACE_WRITES| TCL.GLOBAL_ONLY);
         }
    }
}

lappend specialGets "setValues spinlist values -values"
lappend specialGets "setFrom double from -from"
lappend specialGets "setTo double to -to"
lappend specialGets "setIncrement double increment -increment"
lappend specialGets "setTextVariable textvariable TextVariable -textvariable"
lappend specialGets "setCommand java.lang.String Command"
