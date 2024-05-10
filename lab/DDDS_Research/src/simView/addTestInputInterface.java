/*      Copyright 2002 Arizona Board of regents on behalf of
 *                  The University of Arizona
 *                     All Rights Reserved
 *         (USE & RESTRICTION - Please read COPYRIGHT file)
 *
 *  Version    : DEVSJAVA 2.7
 *  Date       : 08-15-02
 */

package simView;



interface addTestInputInterface {

public void addNameTestInput(String port,String name,double elapsed);

 public void addNameTestInput(String port,String name);

 public void addPortTestInput(String port,double elapsed);

public void addPortTestInput(String port);

 public void addRealTestInput(String port,double value,double elapsed);
 public void addRealTestInput(String port,double value);

  public void addIntTestInput(String port,int value,double elapsed);
 public void addIntTestInput(String port,int value);

}
