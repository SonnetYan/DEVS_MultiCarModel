package quantization;




import java.lang.*;
import java.awt.*;
import java.util.*;
import genDevs.modeling.*;
import GenCol.*;
import simView.*;
import util.*;
import genDevs.plots.*;

public class threeDint extends ViewableAtomic{

protected vect3DEnt lastInput,input,state,initialState,nextState;
double quantum, remainingQuant, clock;



public threeDint(String name, double Quantum, vect3DEnt state){
super(name);

addInport("in");
addInport("stop");
addOutport("out");
addOutport("outTemp");
addOutport("outPos");
addOutport("outDraw");
addTestInput("in",new vect3DEnt(0,-9.8,0),0);
addTestInput("in",new vect3DEnt(0,0,0),0);
addTestInput("in",new vect3DEnt(0,9.8,0),0);

quantum = Quantum;
initialState = state;
}

public threeDint(){
this("twoDint",1,new vect3DEnt(0,0,0));
double  root2 = 1/Math.sqrt(2);
addTestInput("in",new vect3DEnt(0,0,0),0);
addTestInput("in",new vect3DEnt(1,1,0),0);
addTestInput("in",new vect3DEnt(0,-9.8,0),0);
addTestInput("in",new vect3DEnt(root2,root2,0),0);
addTestInput("in",new vect3DEnt(root2,root2,0),.5);
addTestInput("in",new vect3DEnt(-root2,root2,0),0);
addTestInput("in",new vect3DEnt(root2,-root2,0),0);
addTestInput("in",new vect3DEnt(-root2,-root2,0),0);
addTestInput("in",new vect3DEnt(-root2,-root2,0),.5);
}

public void initialize(){
     input = new  vect3DEnt(0,0,0);
     lastInput = input;
     state = initialState;
     remainingQuant = quantum;
     sigma = 0;
     phase = state.toString();
     super.initialize();
     nextState = state;
     clock = 0.0;

 }




public static int signOf(double x){
    if (x == 0) return 0;
    else if (x > 0) return 1;
    else return -1;
}

public void setInp(vect3DEnt Input){
    lastInput = input;
    input = Input;
}

public void timeAdvance(double difference){
    if (input.norm() == 0)sigma = INFINITY;
   else sigma = Math.abs(difference/input.norm());
}

public void update(double e){

   // state.addSelf(lastInput.scalarMult(e)); //this changes initialState, why???
    vect3DEnt v = lastInput.scalarMult(e);
//System.out.println("vvvvvvvvvvvvvvv" +v.toString());
state = state.add(v) ;// new vect3DEnt(state.x+v.x,state.y+v.y);
//System.out.println("stststststst "+ state.toString());
    remainingQuant -= lastInput.scalarMult(e).norm();
   if (remainingQuant < 0)
              System.out.println(getName()+ " ERROR: remainingQuant can't be negative");
}


public void computeIntNextstate(){

      timeAdvance(quantum);
      remainingQuant = quantum;
      nextState = state.add(input.scalarMult(sigma));
      if (input.norm() ==  0)
         System.out.println(getName()+ "ERROR: input can't be zero");

}

public void computeExtNextstate(){
timeAdvance(remainingQuant);
nextState = state.add(input.scalarMult(sigma));
}




public void deltcon(double e,message x)
{

 deltint();

 deltext(0,x);
}

public void  deltext(double e,message   x)
{
for (int i=0; i< x.getLength();i++)
  if (messageOnPort(x,"stop",i)){
  passivate();
  break;
  }
  Continue(e);
  clock = clock + e;
  vect3DEnt sum = new vect3DEnt(0,0,0);

 for (int i=0; i< x.getLength();i++)
  if (messageOnPort(x,"in",i)){
       entity ent = x.getValOnPort("in",i);
       sum.addSelf((vect3DEnt)ent);
    }
     setInp(sum);
     update(e);
     computeExtNextstate();
     phase = state.toString();

}

public void  deltint( )
{
clock = clock + sigma;
state = nextState;
computeIntNextstate();
phase = state.toString();
}

public String getTooltipText(){
   return
  super.getTooltipText()
  +"\n"+" quantum: "+ quantum
   +"\n"+" remainingQuant: "+ remainingQuant
  + "\n"+ "initialState :"+ initialState
     +"\n"+"input :"+ input
   +"\n"+"lastInput :"+ lastInput;
  }


public message    out( )
{
message   m = new message();
if (clock <= 0 && sigma == 0){
m.add(makeContent("out",  state));
m.add(makeContent("outTemp", new doubleEnt(state.y)));
m.add(makeContent("outDraw",new DrawCellEntity(state.x, state.y,
              Color.black, Color.gray)));
              }
else{
m.add(makeContent("out",  nextState));
m.add(makeContent("outTemp", new doubleEnt(nextState.y)));
vect3DEnt diff = nextState.add(state.scalarMult(-1));
//System.out.println();diff.toString();
m.add(makeContent("outPos",new doubleEnt(diff.norm())));
m.add(makeContent("outDraw",new DrawCellEntity(nextState.x, state.y,
              Color.black, Color.gray)));
   }
return m;
}



}

