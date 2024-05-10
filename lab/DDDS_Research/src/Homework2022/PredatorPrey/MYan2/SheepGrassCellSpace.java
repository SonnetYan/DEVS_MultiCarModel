package Homework2022.PredatorPrey.MYan2;

import simView.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import genDevs.simulation.realTime.*;
import GenCol.*;
import genDevs.plots.*;

import java.util.*;
import java.awt.*;

import java.text.*;
import java.io.*;

import twoDCellSpace.*;

public class SheepGrassCellSpace extends TwoDimCellSpace {

    

    public static void main(String args[]) {
//        coordinator r = new coordinator(new SheepGrassCellSpace());
	    TunableCoordinator r = new TunableCoordinator(new SheepGrassCellSpace());
	    r.setTimeScale(0.3);

        r.initialize();
        r.simulate(10000);
        
        System.exit(0);
        
    }

    public SheepGrassCellSpace() {
        this(30, 30);
    }

    public SheepGrassCellSpace(int xDim, int yDim) {
        super("Sheep Grass Cell Space", xDim, yDim);
        this.numCells = xDim * yDim;

        GlobalRef.getInstance().setDim(xDim, yDim);
        for (int i = 0; i < xDim; i++) {
            Arrays.fill(GlobalRef.getInstance().state[i], "empty");
        }
        int showCase = 6;

        switch (showCase) {
            case 1: {       // One grass in the center of the space. No sheep.
                GlobalRef.getInstance().state[15][15] = "grass";
                break;
            }
            case 2: {       // Multiple grass cells at different locations of the space. No sheep.
                GlobalRef.getInstance().state[15][15] = "grass";
                GlobalRef.getInstance().state[18][20] = "grass";
                GlobalRef.getInstance().state[3][5] = "grass";
                GlobalRef.getInstance().state[7][17] = "grass";
                break;
            }
            case 3: {       // One sheep in the center of the space. No grass.
                GlobalRef.getInstance().state[15][15] = "sheep";
                break;
            }
            case 4: {       // Multiple sheep at different locations of the space. No grass.
                GlobalRef.getInstance().state[15][15] = "sheep";
                GlobalRef.getInstance().state[18][20] = "sheep";
                GlobalRef.getInstance().state[3][5] = "sheep";
                GlobalRef.getInstance().state[7][17] = "sheep";
                break;
            }
            /*
            Two neighboring grass cells in the center and one sheep cell adjacent to one of the
            grass cell. The goal is to show that the sheep will eat the neighboring grass but the
            other grass will grow, and from there the dynamics start.
             */
            case 5: {
                GlobalRef.getInstance().state[15][15] = "grass";
                GlobalRef.getInstance().state[15][16] = "grass";
                GlobalRef.getInstance().state[14][15] = "sheep";
                break;
            }
            
            
            case 6: {       // Multiple grass and multiple sheep cells to start a balanced oscillation.
            	GlobalRef.getInstance().state[21][17] = "grass";
                GlobalRef.getInstance().state[21][16] = "grass";
                GlobalRef.getInstance().state[15][16] = "grass";
                GlobalRef.getInstance().state[14][15] = "sheep";
                GlobalRef.getInstance().state[21][15] = "sheep";
                GlobalRef.getInstance().state[4][12] = "sheep";
            	break;
            }
            
        }

        for (int i = 0; i < xDim; i++) {
            for (int j = 0; j < yDim; j++) {
                SheepGrassCell fc = new SheepGrassCell(i, j);
                addCell(fc, 30, 30);
                GlobalRef.getInstance().cell_ref[i][j] = fc;
            }
        }

        hideAll();
        doNeighborToNeighborCoupling();
        DoBoundaryToBoundaryCoupling();

        coupleOneToAll(this, "stop", "stop");
        coupleOneToAll(this, "start", "start");

        CellGridPlot t = new CellGridPlot("SheepGrassCellSpace", 0.1,
                "", 400, "", 400);
        t.setCellSize(10);
        t.setCellGridViewLocation(570, 100);
        add(t);
        coupleAllTo("outDraw", t, "drawCellToScale");
    }

///////////////////////////////////////////////////////////////////////////////////////
// The following are two utility functions that can be useful for you to finish the homework.
// Feel free to modify them, and/or copy them to other places of your code that work for your model

    /**
     * Add couplings among boundary cells to make the cell space wrapped
     */
    private void DoBoundaryToBoundaryCoupling() {
        //top and bottom rows
        for (int x = 1; x < xDimCellspace - 1; x++) {
            // (x,0) -- bottom to top
            addCoupling(withId(x, 0), "outS", withId(x, yDimCellspace - 1), "inN");
            addCoupling(withId(x, 0), "outSW", withId(x - 1, yDimCellspace - 1), "inNE");
            addCoupling(withId(x, 0), "outSE", withId(x + 1, yDimCellspace - 1), "inNW");

            // (x,29) -- top to bottom
            addCoupling(withId(x, yDimCellspace - 1), "outN", withId(x, 0), "inS");
            addCoupling(withId(x, yDimCellspace - 1), "outNE", withId(x + 1, 0), "inSW");
            addCoupling(withId(x, yDimCellspace - 1), "outNW", withId(x - 1, 0), "inSE");
        }

        //west and east columns
        for (int y = 1; y < yDimCellspace - 1; y++) {
            // (0,y) -- West - east
            addCoupling(withId(0, y), "outW", withId(xDimCellspace - 1, y), "inE");
            addCoupling(withId(0, y), "outSW", withId(xDimCellspace - 1, y - 1), "inNE");
            addCoupling(withId(0, y), "outNW", withId(xDimCellspace - 1, y + 1), "inSE");

            // (29,y) -- West - east
            addCoupling(withId(xDimCellspace - 1, y), "outE", withId(0, y), "inW");
            addCoupling(withId(xDimCellspace - 1, y), "outNE", withId(0, y + 1), "inSW");
            addCoupling(withId(xDimCellspace - 1, y), "outSE", withId(0, y - 1), "inNW");
        }
        //corners
        //(0, 0)
        addCoupling(withId(0, 0), "outNW", withId(xDimCellspace - 1, 1), "inSE");
        addCoupling(withId(0, 0), "outW", withId(xDimCellspace - 1, 0), "inE");
        addCoupling(withId(0, 0), "outSW", withId(xDimCellspace - 1, yDimCellspace - 1), "inNE");
        addCoupling(withId(0, 0), "outS", withId(0, yDimCellspace - 1), "inN");
        addCoupling(withId(0, 0), "outSE", withId(1, yDimCellspace - 1), "inNW");
        //(29, 0)
        addCoupling(withId(xDimCellspace - 1, 0), "outSW", withId(xDimCellspace - 2, yDimCellspace - 1), "inNE");
        addCoupling(withId(xDimCellspace - 1, 0), "outE", withId(0, 0), "inW");
        addCoupling(withId(xDimCellspace - 1, 0), "outSE", withId(0, yDimCellspace - 1), "inNW");
        addCoupling(withId(xDimCellspace - 1, 0), "outS", withId(xDimCellspace - 1, yDimCellspace - 1), "inN");
        addCoupling(withId(xDimCellspace - 1, 0), "outNE", withId(0, 1), "inSW");
        //(0, 29)
        addCoupling(withId(0, yDimCellspace - 1), "outSW", withId(xDimCellspace - 1, yDimCellspace - 2), "inNE");
        addCoupling(withId(0, yDimCellspace - 1), "outW", withId(xDimCellspace - 1, yDimCellspace - 1), "inE");
        addCoupling(withId(0, yDimCellspace - 1), "outNE", withId(1, 0), "inSW");
        addCoupling(withId(0, yDimCellspace - 1), "outN", withId(0, 0), "inS");
        addCoupling(withId(0, yDimCellspace - 1), "outNW", withId(xDimCellspace - 1, 0), "inSE");
        //(29, 29)
        addCoupling(withId(xDimCellspace - 1, yDimCellspace - 1), "outNW", withId(xDimCellspace - 2, 0), "inSE");
        addCoupling(withId(xDimCellspace - 1, yDimCellspace - 1), "outE", withId(0, yDimCellspace - 1), "inW");
        addCoupling(withId(xDimCellspace - 1, yDimCellspace - 1), "outSE", withId(0, yDimCellspace - 2), "inNW"); // Xiaolin Hu, 10/16/2016
        addCoupling(withId(xDimCellspace - 1, yDimCellspace - 1), "outN", withId(xDimCellspace - 1, 0), "inS");
        addCoupling(withId(xDimCellspace - 1, yDimCellspace - 1), "outNE", withId(0, 0), "inSW");
    }

    /**
     * Get the x and y coordinate (int[2]) of a neighbor cell based on the direction in a wrapped cell space
     *
     * @param myCell:    the center cell
     * @param direction: the direction defines which neighbor cell to get. 0 - N; 1 - NE; 2 - E; ... (clokewise)
     * @return the x and y coordinate
     */


}
// End SheepGrassCellSpace
