package pulseModels;

import simView.*;
import genDevs.plots.*;
import java.awt.*;
import java.lang.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import GenCol.*;

import java.util.*;

public class Graph extends  ViewableDigraph{

  public Graph() {
    super("Graph");
    ViewableAtomic gnA, gnB, gnC, gnD, gnE, gnF;
    addInport( "start" );
    addOutport( "result" );
    addTestInput( "start", new entity( " :0" ) );
    try {
      gnA = new GraphNode( "A", 1, 1, 2 );
      add( gnA );
      gnB = new GraphNode( "B", 2, 1, 2 );
      add( gnB );
      gnC = new GraphNode( "C", 4, 2, 1 );
      add( gnC );
      gnD = new GraphNode( "D", 1, 1, 2 );
      add( gnD );
      gnE = new GraphNode( "E", 5, 2, 1 );
      add( gnE );
      gnF = new GraphNode( "F", 1, 2, 1 );
      add( gnF );
      addCoupling( this, "start", gnA, "in1" );
      addCoupling( gnA, "out1", gnB, "in1" );
      addCoupling( gnA, "out2", gnD, "in1" );
      addCoupling( gnB, "out1", gnC, "in1" );
      addCoupling( gnD, "out2", gnE, "in2" );
      addCoupling( gnC, "out1", gnF, "in1" );
      addCoupling( gnE, "out1", gnF, "in2" );
      addCoupling( gnB, "out2", gnE, "in1" );
      addCoupling( gnD, "out1", gnC, "in2" );
      addCoupling( gnF, "out1", this, "result" );
   } catch ( Exception e ) { System.out.println( e ); }

  }

    /**
     * Automatically generated by the SimView program.
     * Do not edit this manually, as such changes will get overwritten.
     */
    public void layoutForSimView()
    {
        preferredSize = new Dimension(930, 572);
        ((ViewableComponent)withName("B")).setPreferredLocation(new Point(202, 68));
        ((ViewableComponent)withName("E")).setPreferredLocation(new Point(423, 386));
        ((ViewableComponent)withName("A")).setPreferredLocation(new Point(16, 260));
        ((ViewableComponent)withName("D")).setPreferredLocation(new Point(181, 379));
        ((ViewableComponent)withName("F")).setPreferredLocation(new Point(605, 261));
        ((ViewableComponent)withName("C")).setPreferredLocation(new Point(407, 65));
    }
}
