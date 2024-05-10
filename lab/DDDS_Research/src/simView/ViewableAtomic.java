/*      Copyright 2002 Arizona Board of regents on behalf of
 *                  The University of Arizona
 *                     All Rights Reserved
 *         (USE & RESTRICTION - Please read COPYRIGHT file)
 *
 *  Version    : DEVSJAVA 3.0 Beta
 *  Date       : 02-22-03
 */


package simView;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;
import util.*;

/**
 * An atomic devs component that is capable of creating and updating a
 * graphical view of itself.
 *
 * @author  Jeff Mather
 */
public class ViewableAtomic
    //extends atomic implements ViewableComponent
    extends scalableAtomic
             implements ViewableComponent,addTestInputInterface
{
    /**
     * Holds member variables in common with other viewable-component classes.
     * If you are looking for a member variable and can't find it, it's
     * likely in here.
     */
    protected ViewableComponentBase base = new ViewableComponentBase();

    /**
     * A graphical view on this atomic's state.
     */
    protected AtomicView view;

    protected SimView.ModelView myModelView = null;
    /**
     * the parent of this atomic model
     */
    protected ViewableDigraph myParent;

    /**
     * The background color to use with this atomic's view.
     */
    protected Color backgroundColor = new Color(155, 155, 155);

    /**
     * Constructor.
     *
     * @param   name        The name to give this devs atomic component.
     */
    public ViewableAtomic(String name) {super(name);}

    /**
     * A convienence constructor.
     */
    public ViewableAtomic() {super("ViewableAtomic");}

    /**
     * Returns this atomic's sigma value as a nicely formatted string.
     */
    public String getFormattedSigma()
    {
        // if this atomic's sigma is equal to infinity
        double sigma = getSigma();
        if (sigma >= Double.POSITIVE_INFINITY) {
            return "infinity";
        }

        // return this atomic's sigma formatted to three places
        return (new DecimalFormat("0.000")).format(sigma);
    }

    /**
     * See member variable accessed.
     */
    public ComponentView getView() {return view;}

    /**
     * See member variable accessed.
     */
    public AtomicView getAtomicView() {return view;}

    /**
     * Returns this atomic's phase value as a nicely formatted string.
     */
    public String getFormattedPhase()
    {
        // if this atomic has no current phase, return null
        String phase = getPhase();
        if (phase == null) return null;

        // if this atomic's phase can be converted to a double, return
        // the double formatted to three places, otherwise just return the
        // phase the way it is
        try {
            double numericValue = Double.parseDouble(phase);
            phase = (new DecimalFormat("0.000")).format(numericValue);
        } catch (NumberFormatException e) {}
        return phase;
    }

    /**
     * See ViewableComponent method implemented.
     */
    public int getNumInports()
    {
        return mh.getInports().size();
    }

    /**
     * See ViewableComponent method implemented.
     */
    public int getNumOutports()
    {
        return mh.getOutports().size();
    }

    /**
     * Creates this atomic's view on itself.
     *
     * @param   modelView       The sim-view model-view that will contain the
     *                          created view.
     */
    public void createView(SimView.ModelView modelView)
    {
        // create the view
      myModelView = modelView;
        view = new AtomicView(this, modelView);

        // if this atomic is supposed to be hidden, don't show the view
        if (isHidden()) view.setVisible(false);
    }

    public AtomicView createView(ViewableAtomic atomic, SimView.ModelView modelView, boolean showbit)
   {
       // create the view
      myModelView = modelView;
       view = new AtomicView(atomic, modelView);

       // if this atomic is supposed to be hidden, don't show the view
       if(showbit==false)
       if (isHidden()) view.setVisible(false);
       return view;
    }




    /**
     * See ViewableComponent method implemented.
     */
    public List getInportNames()
    {
        return ViewableComponentUtil.getPortNames(mh.getInports());
    }

    /**
     * See ViewableComponent method implemented.
     */
    public List getOutportNames()
    {
        return ViewableComponentUtil.getPortNames(mh.getOutports());
    }

    /**
     * See member variable accessed.
     */
    public void setBackgroundColor(Color color) {backgroundColor = color;}

    /**
     * See member variable accessed.
     */
    public Color getBackgroundColor() {return backgroundColor;}

    /**
     * See full-signature method.
     */
    public void addTestInput(String portName, entity value) {addTestInput(portName, value, 0);}

    /**
     * Creates a test input structure from the given information,
     * and adds that input structure to the list of test inputs for
     * the given port.
     *
     * @param   portName    The name of the port on which to inject the value.
     * @param   value       The value to inject.
     * @param   e           The amount of simulation time to wait before
     *                      injecting the value.
     */
    public void addTestInput(String portName, entity value, double e)
    {
        ViewableComponentUtil.addTestInput(portName, value, e,
            base.testInputs, base.testInputsByPortName);
    }

    /**
     * See base member variable accessed.
     */
    public List getTestInputs()
    {
        return base.testInputs;
    }

    /**
     * See ViewableComponent method implemented.
     */
    public List getTestInputs(String portName)
    {
        return (List)base.testInputsByPortName.get(portName);
    }

    /**
     * See ViewableComponent method implemented.
     */
    public atomicSimulator getSimulator()
    {
        return (atomicSimulator)getSim();
    }

    /**
     * See member variable accessed.
     */
    public Point getPreferredLocation() {return base.preferredLocation;}

    /**
     * See member variable accessed.
     */
    public void setPreferredLocation(Point location) {base.preferredLocation = location;}

    /**
     * See parent method.
     */
    public void setSigma(double sigma)
    {
        super.setSigma(sigma);

        // inform the view
        if (view != null) {
            view.sigmaChanged();
        }
    }

    /**
     * Returns the text that should be displayed as this atomic's tooltip.
     * This can be overridden to display any info about this atomic that is
     * desired.  Note that the expected format is HTML (so, use "<br>"
     * instead of "\n" for line breaks).
     */
    public String getTooltipText()
    {
        return "class: <font size=\"-2\">" + getClass().getName() + "</font><br>" +
            "phase: " + getFormattedPhase() + "<br>" +
            "sigma: " + getFormattedSigma() + "<br>" +
            "tL: " + getFormattedTL() + "<br>" +
            "tN: " + getFormattedTN();
    }

    /**
     * Returns this atomic's time-of-last-event value as a nicely
     * formatted string.
     */
    protected String getFormattedTL()
    {
        // return this atomic's TL value formatted to three places
        return (new DecimalFormat("0.000")).format(
            ((atomicSimulator)mySim).getTL());
    }

    /**
     * Returns this atomic's time-of-next-event value as a nicely
     * formatted string.
     */
    protected String getFormattedTN()
    {
        // return this atomic's TN value formatted to three places
        return (new DecimalFormat("0.000")).format(
            ((atomicSimulator)mySim).getTN());
    }

    /**
     * See member variable accessed.
     */
    public String getLayoutName()
    {
        return base.layoutName != null ? base.layoutName :
            ViewableComponentUtil.buildLayoutName(name);
    }

    /**
     * See member variable accessed.
     */
    public void setLayoutName(String name) {base.layoutName = name;}

    /**
     * See member variable accessed.
     */
    public boolean isHidden() {return base.hidden;}

    /**
     * See member variable accessed.
     */
     public void setHidden(boolean hidden) {base.hidden = hidden;}



     //changes added by saurabh, provided by Dr. Zeigler
     public void addNameTestInput(String port,String name,double elapsed){
      addTestInput(port,new entity(name),elapsed);
    }

    public void addNameTestInput(String port,String name){
      addTestInput(port,new entity(name),0);
    }

    public void addPortTestInput(String port,double elapsed){
      addTestInput(port,new entity(),elapsed);
    }

    public void addPortTestInput(String port){
      addTestInput(port,new entity(),0);
    }
    public void addRealTestInput(String port,double value,double elapsed){
      addTestInput(port,new doubleEnt(value),elapsed);
    }
    public void addRealTestInput(String port,double value){
      addTestInput(port,new doubleEnt(value),0);
    }

    public void addIntTestInput(String port,int value,double elapsed){
      addTestInput(port,new intEnt(value),elapsed);
    }
    public void addIntTestInput(String port,int value){
      addTestInput(port,new intEnt(value),0);
    }


//by saurabh

    public void setMyParent(ViewableDigraph dg){
    myParent = new ViewableDigraph("none");
     myParent =dg;
    }

    public ViewableDigraph getMyParent(){return myParent;}

    public void removeInport(String port){
      s.s("----------------------");
      super.removeInport(port);
       updateComponentView();
    }

     public void removeOutport(String port){
       s.s("----------------------");
       super.removeOutport(port);
       updateComponentView();
    }

    public void addInport(String modelName, String port){

      super.addInport(modelName,port);  //can drop the boolean addAtRunTIme version

      ViewableDigraph P = (ViewableDigraph)getParent();
      IODevs a = (IODevs)P.withName(modelName);
      if (a instanceof ViewableAtomic){
        s.s("----------------------");
        s.s("Inport addedVA: "+port);
        ((ViewableAtomic)a).updateComponentView();
      }


    }

    public void addOutport(String modelName, String port){

      super.addOutport(modelName,port);

      ViewableDigraph P = (ViewableDigraph)getParent();
      IODevs a = (IODevs)P.withName(modelName);
      if (a instanceof ViewableAtomic){
        s.s("----------------------");
        s.s("Outport addedVA: "+port);
        ((ViewableAtomic)a).updateComponentView();
      }
    }

    public void updateComponentView(){

      ViewableDigraph parent = (ViewableDigraph)getParent();
      //s.s("Inside ViewableAtomic update component");
      // save the current Model Couplings
      coordinator cd = parent.getCoordinator();
      couprel savedCr = cd.getModelCoupling(this.getName());
      try{
        //clear the couplings from the modelView window
        //remove the component View from the window
        myModelView.getSim().usePrevModelLayout = true;
        myModelView.savingModelViewCouplingsRemove(this,savedCr);

        //create a new componentView
        //put back the saved couplings
        s.s("Updating the componenet: "+this.getName());
        myModelView.savedModelViewCouplingsAdd(this,savedCr);
        myModelView.repaint();
        myModelView.getSim().usePrevModelLayout = false;

      }
      catch(Exception ex){
            ex.printStackTrace();
        return;
      }
      myModelView.repaint();

    }
/*
    public void addModel(IODevs iod){
      ViewableDigraph P = (ViewableDigraph)getParent();
      P.add(iod);
      coordinator PCoord = P.getCoordinator();
      PCoord.setNewSimulator((IOBasicDevs)iod);
      }
*/

}


