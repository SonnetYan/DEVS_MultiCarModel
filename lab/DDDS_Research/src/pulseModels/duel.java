
package pulseModels;

import simView.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;

import java.util.*;
import java.awt.*;

public class duel extends  ViewableDigraph{

public duel(){
super("duel");

addInport("orderToShoot");
addOutport("outDeadA");
addOutport("outDeadB");

addTestInput("orderToShoot",new entity());


ViewableDigraph fast = new duelist("fast",.4);
//fast.setBlackBox(true);
ViewableDigraph slow = new duelist("slow",.8);
// slow.setBlackBox(true);

add(fast);
add(slow);

addCoupling(this,"orderToShoot",fast,"orderToShoot");
addCoupling(this,"orderToShoot",slow,"orderToShoot");

addCoupling(fast,"outShot",slow,"getHit");
addCoupling(slow,"outShot",fast,"getHit");

addCoupling(fast,"outDead",this,"outDeadA");
addCoupling(slow,"outDead",this,"outDeadB");
}

    /**
     * Automatically generated by the SimView program.
     * Do not edit this manually, as such changes will get overwritten.
     */
    public void layoutForSimView()
    {
        preferredSize = new Dimension(689, 456);
        ((ViewableComponent)withName("fast")).setPreferredLocation(new Point(5, 25));
        ((ViewableComponent)withName("slow")).setPreferredLocation(new Point(1, 257));
    }
}