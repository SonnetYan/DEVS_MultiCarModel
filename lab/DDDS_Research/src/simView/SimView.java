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
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import GenCol.*;
import genDevs.modeling.*;
import genDevs.simulation.*;
import genDevs.simulation.realTime.*;
import util.*;

/**
 * An application that displays the execution of a simulation employing
 * a coupled devs model.
 *
 * @author      Jeff Mather
 */
public class SimView
{
    /**
     * A label that displays whether the viewer is currently executing
     * a step (or running multiple steps) of a simulation, or is ready
     * for the next user command.
     */
    protected JLabel statusLabel;

    /**
     * The name of the settings file for this program.
     */
    final protected String settingsFileName = "SimView_settings";

    /**
     * Whether to always show couplings between components, or just when
     * the mouse is over one of them.
     */
    protected boolean alwaysShowCouplings = false;

    public boolean usePrevModelLayout = false;


    /**
     * The width of the border (in pixels) between the top-level model being
     * viewed and the model-view.
     */
    protected final int modelBorderWidth = 5;

    /**
     * The name given to the default digraph that this sim-view uses to
     * wrap single atomic models that are to be viewed.
     */
    protected String wrapperDigraphName = "wrapper digraph";

    /**
     * The main frame of this application.
     */
    protected JFrame mainFrame;

    /**
     * The list of model package names from which models may be selected
     * for simulation.
     */
    protected List modelPackages;

    protected String className;
    /**
     * The configuration dialog, where program settings are specified.
     */
    protected ConfigureDialog configureDialog;

    /**
     * The help dialog, where the program help text is displayed.
     */
    protected HelpDialog helpDialog;

    /**
     * The model on which the simulation is being executed.
     */
    protected ViewableDigraph model;

    /**
     * The panel which displays the components of the current model, along
     * with the scrollpane containing that panel.
     */
//    protected ModelView modelView;
//    protected JScrollPane modelViewScrollPane;
    public ModelView modelView;
    public JScrollPane modelViewScrollPane;





    /**
     * A label that displays how much time has elapsed in the simulation
     * so far.
     */
    protected JLabel clockLabel;

    /**
     * Formats decimal numbers to three places.
     */
    protected DecimalFormat decimalFormat = new DecimalFormat("0.000");

    /**
     * The coordinator executing the simulation on the current model.
     */
    protected TunableCoordinator coordinator;

    /**
     * This indicates whether to show the blackbox or to view its contents
     */
    protected boolean showLevel = false;

    /**
     * The ratio of real time to simulator time, along with the different
     * choices available for its value, as well as the label that displays
     * its current value, and the slider that adjusts that value.
     */
    protected double[] realTimeFactors = {.0001, .001, .01, .1, .5, 1, 5, 10, 100, 1000, 10000};
    protected RealTimeFactor realTimeFactor = new RealTimeFactor();
    protected JLabel realTimeFactorLabel;
    protected JSlider realTimeFactorSlider;

    /**
     * A combo-box that lists all the packages of models from which the
     * user may select a model to simulate.
     */
    protected JComboBox packagesBox;

    /**
     * A combo-box that lists all the models on which the user may choose
     * to run a simulation.
     */
    protected JComboBox modelsBox;

    /**
     * How much time has elapsed in the simulation so far.
     */
    protected Clock clock = new Clock();

    /**
     * The path (relative to the working directory) at which are located
     * the java model packages, in which model classes may be found.
     */
    protected String modelsPath;

    /**
     * The path (relative to the working directory) at which are located
     * the java source files of the model packages, that this sim-view
     * may modify those files to save model layouts.
     */
    protected String sourcePath;

    /**
     * The current models package selected, from which model classes may
     * be chosen for simulation.
     */
    protected String modelsPackage;

    /**
     * The name of the last model viewed by this program.  This is saved
     * to a file so that the same model may be automatically loaded the
     * next time the program is run.
     */
    protected String lastModelViewed;

    /**
     * Constructs a sim-view.
     */
    public SimView()
    {
        loadSettings();

        constructUI();

        // load the last model viewed from the previous program execution
        if (modelsPackage != null) {
            packagesBox.setSelectedItem(modelsPackage);
        }
        if (lastModelViewed != null) {
            modelsBox.setSelectedItem(lastModelViewed);
        }

        // show the app frame; remember the Swing rule that all further code
        // that manipulates or queries Swing components must run on the
        // event handling thread, otherwise lockups may occur
        mainFrame.setVisible(true);
    }

    /**by saurabh
     * Constructs an exploded window for the blackbox digraph
     */
    public SimView(ViewableDigraph dg, String modelsPackage, String modelClass, boolean level, double prevSimTime)
    {
      showLevel = level;//false for the displaying the child in the network
      constructUI_SM(dg, modelsPackage, modelClass, prevSimTime);
      mainFrame.setVisible(true);
    }


    /**
     * Constructs the UI of the main window of this application.
     */
    protected void constructUI()
    {
        // create the app frame
        final JFrame frame = mainFrame = new JFrame("DEVSJAVA Simulation Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        Container pane = frame.getContentPane();
        pane.setBackground(Color.white);
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pane.add(main);

        // when the app frame is closing
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e)
            {
                // save the current model's layout, and the current program
                // settings, but don't let an exception in the process of
                // doing so halt the shutdown of the program
                try {
                    saveModelLayout();
                    saveSettings();
                } catch (Exception ex) {ex.printStackTrace();}
            }
        });

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        main.add(panel);

        panel.add(Box.createRigidArea(new Dimension(20, 0)));

        // add the configure button
        JButton button = new JButton("configure");
        button.setFont(new Font("SansSerif", Font.PLAIN, 10));
        panel.add(button);

        // when the configure button is clicked
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // if the configure dialog hasn't yet been created
                if (configureDialog == null) {
                    // create it
                    configureDialog = new ConfigureDialog(frame);
                }

                // display the configure dialog
                configureDialog.setVisible(true);
            }
        });

        panel.add(Box.createRigidArea(new Dimension(20, 0)));

        // add the packages combo-box
        JComboBox combo = packagesBox = new JComboBox();
        populatePackagesBox(combo);
        panel.add(combo);

        // this keeps the combo-box from taking up extra height
        // unnecessarily under JRE 1.4
        combo.setMaximumSize(new Dimension(combo.getMaximumSize().width,
            combo.getMinimumSize().height));

        // when the a choice is selected from the packages combo-box
        packagesBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ignore the first choice when it is selected, as it is
                // just an instructions string; also, ignore when no choice
                // is selected
                if (packagesBox.getSelectedIndex() <= 0) return;

                // make the selected item the current model package name
                if(!showLevel) //by saurabh
                modelsPackage = (String)packagesBox.getSelectedItem();

                populateModelsBox(modelsBox);
            }
        });

        panel.add(Box.createRigidArea(new Dimension(20, 0)));

        // add the models combo-box
        combo = modelsBox = new JComboBox();
        panel.add(combo);

        // this keeps the combo-box from taking up extra height
        // unnecessarily under JRE 1.4
        combo.setMaximumSize(new Dimension(combo.getMaximumSize().width,
            combo.getMinimumSize().height));

        // when the a choice is selected from the models combo-box
        modelsBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ignore the first choice when it is selected, as it is
                // just an instructions string; also, ignore when no choice
                // is selected
                if (modelsBox.getSelectedIndex() <= 0) return;

           //     useModelClass((String)modelsBox.getSelectedItem());

                //by saurabh
                if(!showLevel)
                  useModelClass((String)modelsBox.getSelectedItem());
                else{
//                  System.out.println("The data is package 2 <"+modelsPackage+"> class <"+className+"> model<"+model.getName()+">");
                  useModelClass(className);

                }



            }
        });

        panel.add(Box.createRigidArea(new Dimension(20, 0)));

        main.add(Box.createRigidArea(new Dimension(0, 5)));

        // add the model-view panel in a scrollpane
//        modelView = new ModelView();
        modelView = new ModelView(this);
/*        JScrollPane scrollPane = modelViewScrollPane =
            new JScrollPane(modelView);
*/
        JScrollPane scrollPane = modelViewScrollPane =
            new JScrollPane(modelView);

        main.add(scrollPane);
        modelViewScrollPane.show();

        panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        main.add(panel);

        // add the status label
        JLabel label = statusLabel = new JLabel("", SwingConstants.CENTER);
        panel.add(label);
        setStatusLabelToReady();

        // force the status label to remain a constant width, so that its
        // updating doesn't cause the other components in its row to
        // be moved around
        Dimension size = new Dimension(
            label.getFontMetrics(label.getFont()).stringWidth("stepping")
                + 10,
            label.getMinimumSize().height);
        label.setMinimumSize(size);
        label.setMaximumSize(size);
        label.setPreferredSize(size);

        panel.add(Box.createRigidArea(new Dimension(20, 0)));

        // add the clock display
        Color labelColor = new Color(102, 102, 153);
        label = new JLabel("clock: ");
        label.setForeground(labelColor);
        panel.add(label);
        label = clockLabel = new JLabel();
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setForeground(labelColor);
        panel.add(label);

        panel.add(Box.createRigidArea(new Dimension(20, 0)));

        // add the real-time-factor label
        label = new JLabel("real time factor: ");
        label.setForeground(labelColor);
        panel.add(label);

        JPanel panel3 = new JPanel();
        panel3.setOpaque(false);
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));
        panel.add(panel3);

        // add the real-time-factor value label
        label = realTimeFactorLabel = new JLabel();
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(labelColor);
        label.setAlignmentX(0.5f);
        panel3.add(label);

        // this will get the initial real-time-factor value shown by the label
        realTimeFactor.set(realTimeFactor.get());

        // add the real-time-factor slider
        JSlider slider = realTimeFactorSlider = new JSlider();
        slider.setMaximumSize(new Dimension(100, 20));
        slider.setAlignmentX(0.5f);
        slider.setMinimum(0);
        slider.setMaximum(realTimeFactors.length - 1);
        panel3.add(slider);

        // set the real-time-factor slider to the notch that corresponds
        // with the factor's value
        for (int i = 0; i < realTimeFactors.length; i++) {
            if (realTimeFactors[i] == realTimeFactor.get()) {
                slider.setValue(i);
                break;
            }
        }

        // when the real-time-factor slider is adjusted
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                // store the new real-time-factor value
                JSlider slider1 = realTimeFactorSlider;
                realTimeFactor.set(realTimeFactors[slider1.getValue()]);

                // if the slider knob isn't still being dragged
                if (!slider1.getValueIsAdjusting() && coordinator != null) {
                    // have the coodinator halt its current sleep period, so
                    // the new value may take effect right away
                    coordinator.interrupt();
                }
            }
        });

        panel.add(Box.createRigidArea(new Dimension(20, 0)));

        // add the always-show-couplings check-box
        JCheckBox checkBox = new JCheckBox("always show couplings",
            alwaysShowCouplings);
        checkBox.setFont(new Font("SansSerif", Font.PLAIN, 10));
        panel.add(checkBox);

        // when the always-show-couplings checkbox is clicked
        checkBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                alwaysShowCouplings =
                    (e.getStateChange() == ItemEvent.SELECTED);
                modelView.repaint();
            }
        });

        panel.add(Box.createRigidArea(new Dimension(20, 0)));

        // add the help button
        button = new JButton("help");
        button.setFont(new Font("SansSerif", Font.PLAIN, 10));
        panel.add(button);

        // when the help button is clicked
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // if the help dialog hasn't yet been created
                if (helpDialog == null) {
                    // create it
                    helpDialog = new HelpDialog(frame);
                }

                // display the help dialog
                helpDialog.setVisible(true);
            }
        });

        main.add(Box.createRigidArea(new Dimension(0, 5)));

        panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        main.add(panel);

        // add the step button
        button = new JButton("step");
        button.setAlignmentX(0.5f);
        panel.add(button);

        // when the step button is clicked
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (coordinator == null) return;

                // inform the model view that a step is to be taken
                modelView.stepToBeTaken();

                // simluate one iteration
                setStatusLabelToStepping();
                coordinator.simulate(1);

                // update this sim-view's clock
                clock.set(coordinator.getTimeOfLastEvent());
            }
        });

        panel.add(Box.createRigidArea(new Dimension(5, 0)));

        // add the run button
        final JToggleButton runButton = new JToggleButton("run");
        runButton.setAlignmentX(0.5f);
        panel.add(runButton);

        // when the run button is clicked
        runButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (coordinator == null) return;

                // if the run button is now selected
                if (runButton.isSelected()) {
                    modelView.runToOccur();

                    // simulate iterations until interrupted
                    setStatusLabelToRunning();
                    coordinator.simulate(Integer.MAX_VALUE);
                }

                // otherwise
                else {
                    // stop the current simulation run
                    coordinator.simulate(0);
                    setStatusLabelToReady();
                }
            }
        });

        panel.add(Box.createRigidArea(new Dimension(5, 0)));

        // add the restart button
        button =  new JButton("restart");
        button.setAlignmentX(0.5f);
        panel.add(button);

        // when the restart button is clicked
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (coordinator == null) return;

                modelView.simulationRestarted();

                coordinator.initialize();

                clock.set(0);
            }
        });

        panel.add(Box.createRigidArea(new Dimension(10, 0)));

        // adjust tooltip delay settings to make tooltips show up immediately and
        // not go away
        ToolTipManager manager = ToolTipManager.sharedInstance();
        manager.setInitialDelay(0);
        manager.setReshowDelay(0);
    }

    /**By Saurabh
     * Constructs the UI of the child window of the blackbox digraph.
     */
    protected void constructUI_SM(ViewableDigraph dg, String modelsPackage, String modelClass, double prevSimTime)
    {
      final JFrame frame;
      // create the app frame
      frame = mainFrame = new JFrame("DEVSJAVA Level Viewer ");
      if(!showLevel)
      {
        //if the child is exploded; false indicates expansion
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      }
      frame.setSize(500, 350);
      Container pane = frame.getContentPane();
      pane.setBackground(Color.white);
      JPanel main = new JPanel();
      main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
      main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      pane.add(main);

      JPanel panel = new JPanel();
      panel.setOpaque(false);
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
      main.add(panel);

      panel.add(Box.createRigidArea(new Dimension(20, 0)));

      // add the configure button
      JButton button = new JButton("configure");

      //added by saurabh
      button = new JButton("");

      //added by saurabh
      useModelClass_SM(dg, modelClass, prevSimTime);

      //to set the digraph back to blackbox as it has been exploded in the
      //new window
      dg.setBlackBox(true);

      panel.add(Box.createRigidArea(new Dimension(20, 0)));
      main.add(Box.createRigidArea(new Dimension(0, 5)));

      JScrollPane scrollPane = modelViewScrollPane =
          new JScrollPane(modelView);
      main.add(scrollPane);

      if(!showLevel){//////////if its a child or an updated parent
        panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        main.add(panel);

        // add the status label
        JLabel label = statusLabel = new JLabel("", SwingConstants.CENTER);
        panel.add(label);
        setStatusLabelToReady();

        // force the status label to remain a constant width, so that its
        // updating doesn't cause the other components in its row to
        // be moved around
        Dimension size = new Dimension(
            label.getFontMetrics(label.getFont()).stringWidth("stepping")
            + 10,
            label.getMinimumSize().height);
        label.setMinimumSize(size);
        label.setMaximumSize(size);
        label.setPreferredSize(size);

        panel.add(Box.createRigidArea(new Dimension(20, 0)));

        // add the clock display
        Color labelColor = new Color(102, 102, 153);
        label = new JLabel("clock: ");
        label.setForeground(labelColor);
        panel.add(label);
        label = clockLabel = new JLabel();
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setForeground(labelColor);
        panel.add(label);

        panel.add(Box.createRigidArea(new Dimension(20, 0)));

        // add the real-time-factor label
        label = new JLabel("real time factor: ");
        label.setForeground(labelColor);
        panel.add(label);

        JPanel panel3 = new JPanel();
        panel3.setOpaque(false);
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));
        panel.add(panel3);

        // add the real-time-factor value label
        label = realTimeFactorLabel = new JLabel();
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(labelColor);
        label.setAlignmentX(0.5f);
        panel3.add(label);

        // this will get the initial real-time-factor value shown by the label
        realTimeFactor.set(realTimeFactor.get());

        // add the real-time-factor slider
        JSlider slider = realTimeFactorSlider = new JSlider();
        slider.setMaximumSize(new Dimension(100, 20));
        slider.setAlignmentX(0.5f);
        slider.setMinimum(0);
        slider.setMaximum(realTimeFactors.length - 1);
        panel3.add(slider);

        // set the real-time-factor slider to the notch that corresponds
        // with the factor's value
        for (int i = 0; i < realTimeFactors.length; i++) {
          if (realTimeFactors[i] == realTimeFactor.get()) {
            slider.setValue(i);
            break;
          }
        }

        // when the real-time-factor slider is adjusted
        slider.addChangeListener(new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            // store the new real-time-factor value
            JSlider slider1 = realTimeFactorSlider;
            realTimeFactor.set(realTimeFactors[slider1.getValue()]);

            // if the slider knob isn't still being dragged
            if (!slider1.getValueIsAdjusting() && coordinator != null) {
              // have the coodinator halt its current sleep period, so
              // the new value may take effect right away
              coordinator.interrupt();
            }
          }
        });

        panel.add(Box.createRigidArea(new Dimension(20, 0)));

        // add the always-show-couplings check-box
        JCheckBox checkBox = new JCheckBox("always show couplings",
            alwaysShowCouplings);
        checkBox.setFont(new Font("SansSerif", Font.PLAIN, 10));
        panel.add(checkBox);

        // when the always-show-couplings checkbox is clicked
        checkBox.addItemListener(new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            alwaysShowCouplings =
                (e.getStateChange() == ItemEvent.SELECTED);
            modelView.repaint();
          }
        });

        panel.add(Box.createRigidArea(new Dimension(20, 0)));

        // add the help button
        button = new JButton("help");
        button.setFont(new Font("SansSerif", Font.PLAIN, 10));
        panel.add(button);

        // when the help button is clicked
        button.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            // if the help dialog hasn't yet been created
            if (helpDialog == null) {
              // create it
              helpDialog = new HelpDialog(frame);
            }

            // display the help dialog
            helpDialog.setVisible(true);
          }
        });

        main.add(Box.createRigidArea(new Dimension(0, 5)));

        panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        main.add(panel);

        // add the step button
        button = new JButton("step");
        button.setAlignmentX(0.5f);
        panel.add(button);

        // when the step button is clicked
        button.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (coordinator == null) return;

            // inform the model view that a step is to be taken
            modelView.stepToBeTaken();

            // simluate one iteration
            setStatusLabelToStepping();
            coordinator.simulate(1);

            // update this sim-view's clock
            clock.set(coordinator.getTimeOfLastEvent());
          }
        });

        panel.add(Box.createRigidArea(new Dimension(5, 0)));

        // add the run button
        final JToggleButton runButton = new JToggleButton("run");
        runButton.setAlignmentX(0.5f);
        panel.add(runButton);

        // when the run button is clicked
        runButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (coordinator == null) return;

            // if the run button is now selected
            if (runButton.isSelected()) {
              modelView.runToOccur();

              // simulate iterations until interrupted
              setStatusLabelToRunning();
              coordinator.simulate(Integer.MAX_VALUE);
            }

            // otherwise
            else {
              // stop the current simulation run
              coordinator.simulate(0);
              setStatusLabelToReady();
            }
          }
        });

        panel.add(Box.createRigidArea(new Dimension(5, 0)));

        // add the restart button
        button =  new JButton("restart");
        button.setAlignmentX(0.5f);
        panel.add(button);

        // when the restart button is clicked
        button.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (coordinator == null) return;

            modelView.simulationRestarted();

            coordinator.initialize();

            clock.set(0);
          }
        });

        panel.add(Box.createRigidArea(new Dimension(10, 0)));

      }
      // adjust tooltip delay settings to make tooltips show up immediately and
      // not go away
      ToolTipManager manager = ToolTipManager.sharedInstance();
      manager.setInitialDelay(0);
      manager.setReshowDelay(0);
    }




    /**
     * Starts this application.
     */
    static public void main(String[] args)
    {
        new SimView();
    }

    /**
     * The panel which displays the components of the current model.
     */
    public class ModelView extends JLayeredPane
        implements TunableCoordinator.Listener,
        TunableCoupledCoordinator.Listener, ViewableAtomicSimulator.Listener,
        ViewableAtomicSimulator.TimeScaleKeeper,
        variableStructureViewer
    {
        /**
         * The thread responsible for moving the currently displayed
         * content views around this model-view.
         */
        protected MoveContentViewThread moveContentViewThread;

        /**
         * A map of the movement objects of the content views currently
         * being moved around this model-view.
         */
        protected Map contentViewMovementMap = new HashMap();

        /**
         * Whether this view should display the couplings between the
         * components in the model.
         */
        protected boolean showCouplings = false;

        /**
         * The panel, which should cover this entire view, on which are drawn
         * the lines meant to represent the couplings between the components
         * in the model.
         */
        protected CouplingsPanel couplingsPanel;

        /**
         * The list of couplings between all the components (and their
         * subcomponents) in the model.
         */
        protected List couplings = new ArrayList();

        /**
         * The views on the components of the model this view
         * is displaying.
         */
        protected List componentViews = new ArrayList();

        /**
         * The content views currently displayed in this view.
         */
        protected List contentViews = new ArrayList();

        /**
         * A map of the paths contents are taking as they travel from
         * their source to their destination.  The key used is based
         * of the content's latest step in its path, meaning it is
         * composed of the content as well as the step's component name.
         */
        protected Map contentPathMap = new HashMap();

        /**
         * Whether or not this model-view should execute just one
         * iteration of the simulation at a time, and wait until the user
         * tells it to execute the next iteration, or if it should just
         * keep on executing iterations without stopping each time.
         */
        protected boolean stepMode = true;

        //added by Saurabh
        public ViewableDigraph model;
        public DigraphView modelDigView;


        protected SimView simview; // added by Xiaolin Hu, Feb 5th, 2003

        /**
         * Constructs a model-view.
         */
        public ModelView(SimView simview_){
            simview = simview_;
            setOpaque(true);
            setBackground(Color.white);
            setLayout(null);

            // add the couplings panel
            JPanel panel = couplingsPanel = new CouplingsPanel();
            add(panel, new Integer(2));

            // make it so that the couplings panel's size will always
            // match this view's size
            addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    couplingsPanel.setSize(getSize());
                }
            });

            // create and start the move-content-view thread
            Thread thread = moveContentViewThread = new MoveContentViewThread();
            thread.start();
        }

        /**
         * Adds the given devs-component view to this model view.
         *
         * @param   view        The view to add.
         */
        public void addView(ComponentView view, JComponent parent)
        {
            componentViews.add(view);

            // add the view's GUI component to the given parent component
            // as a layer-0 component
            JComponent comp = (JComponent)view;
            parent.add(comp, new Integer(0));
            // set the view's location and size to their preferred values
            comp.setLocation(view.getPreferredLocation());
            comp.setSize(view.getPreferredSize());
        }

        public void removeView(ComponentView view, JComponent parent){
            componentViews.remove(view);
            JComponent comp = (JComponent)view;
            ((DigraphView)parent).remove(comp, new Integer(0));
   //         System.out.println("remove view:"+view.getViewableComponent().getName());
        }

        /**
         * Informs this model view that the user has injected an input
         * into one of the components being viewed.
         */
        public void inputInjected()
        {
            // eliminate all previous content views, as they are now old
            removeContentViews();
        }

        /**
         * See genDevs.simulation.realTime.TunableCoordinator.Listener
         * interface method.
         */
        public void iterationsCompleted()
        {
            setStatusLabelToReady();
        }

        /**
         * Returns the time-scale (or, real time factor), currently in
         * use by this sim-view's coordinator for the current model.
         */
        public double getTimeScale() {return coordinator.getTimeScale();}

        /**
         * See ViewableAtomicSimulator.Listener interface method.
         */
        public void clockChanged(double newTime)
        {
            // update the clock only if the given value is greater than the
            // clock's current value, because sometimes lesser values are
            // reported, though I'm not sure why
            if (newTime > clock.get()) clock.set(newTime);
        }

        /**
         * Removes all atomic views from this model-view.
         */
        public void removeAllViews()
        {
            componentViews.clear();
            modelView.removeAll();
            modelView.add(couplingsPanel);
        }

        /**
         * Informs this model-view that a single iteration of the simulation
         * is about to be executed.
         */
        public void stepToBeTaken()
        {
            // get any old content views removed
            removeContentViews();

            stepMode = true;
        }

        /**
         * Informs this model-view that a run of simulation iterations
         * (versus individual iteration steps) is about to occur.
         */
        public void runToOccur()
        {
            // get any old content views removed
            removeContentViews();

            stepMode = false;
        }

        /**
         * See ViewableAtomicSimulator.Listener interface method.
         */
        public void contentOutputted(content content,
            devs source, String sourcePortName)
        {
            // this method requires the source component to be viewable
            if (!(source instanceof ViewableComponent)) return;

            // when step mode isn't on, this model-view shouldn't
            // display the transmittal of contents
            if (!stepMode) return;

            // create a content-path-step to hold this first (source) step
            // in the content's path to its destination
            ContentPathStep step = new ContentPathStep();
            step.view = ((ViewableComponent)source).getView();
            step.portName = sourcePortName;

            // create this content's path object and add to it the source step
            // just created
            List path = new ArrayList();
            path.add(step);

            // store the path in the content-to-path map using a newly created
            // key
            ContentPathKey key = new ContentPathKey(content, source.getName());
            contentPathMap.put(key, path);
        }

        /**
         * See ViewableAtomicSimulator.Listener interface method.
         */
        public void couplingAddedToContentPath(content oldContent,
            devs destComponent, String destPortName,
            content newContent, String sourceComponentName)
        {
            // this method requires the destination component to be viewable
            if (!(destComponent instanceof ViewableComponent)) return;

            // when step mode isn't on, this model-view shouldn't
            // display the transmittal of contents
            if (!stepMode) return;

            // create a content-path-step to hold this step
            // in the content's path to its destination
            ContentPathStep step = new ContentPathStep();
            step.view = ((ViewableComponent)destComponent).getView();
            step.portName = destPortName;

            // use the old-content as part of a key to find the content's
            // path up to this point in the content-path-map
            ContentPathKey key = new ContentPathKey(
                oldContent, sourceComponentName);
            List path = (List)contentPathMap.get(key);

            // if no path was found above, then abort this method
            if (path == null) return;

            // use the content's path to find its movement object (if it
            // has one)
            ContentViewMovement movement =
                (ContentViewMovement)contentViewMovementMap.remove(path);

            // make a copy of the path, so that if this same content
            // branches off along multiple couplings at this point,
            // each of those branches can have its own complete path
            path = new ArrayList(path);

            // add the new step to the path
            path.add(step);

            // put the path back in the content-path-map using the new content
            // as part of a new key
            key = new ContentPathKey(newContent, destComponent.getName());
            contentPathMap.put(key, path);

            // if the content has a movement object associated with it already
            if (movement != null) {
                // update the movement object with the copied path, and remap
                // the movement object into the movement-object-map
                movement.path = path;
                contentViewMovementMap.put(path, movement);
            }

            // otherwise
            else {
                // add a content-view to this model-view for the given content;
                // don't make the view visible right away, because the movement
                // thread has to assign it the location of its first step
                ContentView view = new ContentView(newContent);
                view.setVisible(false);
                add(view, new Integer(3));
                contentViews.add(view);
                view.setSize(view.getPreferredSize());

                // make the content-view draggable, so that overlapped such
                // views may be manually separated by the user
                DragViewListener listener = new DragViewListener(view, this);
                view.addMouseListener(listener);
                view.addMouseMotionListener(listener);

                // have the content view moved along the couplings path;
                // the rest of the steps will be added to the path
                // before its reaches its first stop in the model-view
                movement = new ContentViewMovement();
                movement.view = view;
                movement.path = path;
                moveContentViewThread.addContentViewMovement(movement);

                // put the movement object in the movement-objects map, so
                // that we can find it when further steps need to be added
                // to its path
                contentViewMovementMap.put(path, movement);
            }
        }

        /**
         * Sizes this model-view to be just larger than the size of its
         * top-level model.
         */
        public Dimension getPreferredSize()
        {
            // if the model or its view have not yet been created
            if (model == null || model.getView() == null) {
                return super.getPreferredSize();
            }

            // return the size of the model's view,
            // plus some space for an empty border
            Dimension size = ((JComponent)model.getView()).getSize();
            return new Dimension(size.width + modelBorderWidth * 2,
                size.height + modelBorderWidth * 2);
        }

        /**
         * Together, these force this model-view's size to be its
         * preferred size.
         */
        public Dimension getMinimumSize() {return getPreferredSize();}
        public Dimension getMaximumSize() {return getPreferredSize();}


        public SimView getSim(){
          return this.simview;
        }
        /**
         * Removes from this model-view each of the content-views
         * it is displaying.
         */
        protected void removeContentViews()
        {
            // for each content view this view contains
            for (int i = 0; i < contentViews.size(); i++) {
                ContentView view = (ContentView)contentViews.get(i);

                // remove this content view
                remove(view);
            }

            contentViews.clear();

            repaint();
        }

        /**
         * Informs this model-view that the mouse has entered a port-box
         * on one of the model's components.
         */
        public void mouseEnteredPort()
        {
            showCouplings = true;
            repaint();
        }

        /**
         * Informs this model-view that the mouse has exited a port-box
         * on one of the model's components.
         */
        public void mouseExitedPort()
        {
            showCouplings = false;
            repaint();
        }

        /**
         * An object associating a content-view with various information
         * about its movement along a path within the model-view.
         */
        protected class ContentViewMovement
        {
            /**
             * The content-view to be moved.
             */
            protected ContentView view;

            /**
             * The path of steps along which the content view is to move.
             */
            protected List path;

            /**
             * The index of the current step on the path along which
             * the content view is moving.
             */
            protected int currentStepIndex;

            /**
             * The x and y offsets to take with each step along the current line.
             */
            protected double dx, dy;

            /**
             * The current location of this view along the current line, with more
             * precision than just integer coordinates, so that this view
             * stays centered on the line as it moves.
             */
            protected Point2D.Double location;

            /**
             * How many moves it will take to get to the current destination.
             */
            protected int movesRequired;

            /**
             * How many moves have been made so far towards the current
             * destination.
             */
            protected int movesDone;
        }

        /**
         * Moves each content view in its list along a series of steps
         * (i.e. couplings) to its final destination port.
         */
        protected class MoveContentViewThread extends Thread
        {
            /**
             * The content view movements this thread is currently performing.
             */
            protected List movements = new ArrayList();

            /**
             * Tells this thread to execute the given content-view movement.
             *
             * @param   movement        The content-view movement to execute.
             */
            public void addContentViewMovement(ContentViewMovement movement)
            {
                movements.add(movement);
            }

            /**
             * See parent method.
             */
            public void run()
            {
                // keep doing this
                while (true) {
                    moveContentViews();

                    // pause so the animation doesn't go by too quickly
                    Util.sleep(20);
                }
            }

            /**
             * Performs the actual movements of the content views.
             */
            protected void moveContentViews()
            {
                // for each content view movement currently being performed
                for (int i = 0; i < movements.size(); i++) {
                    ContentViewMovement movement = (ContentViewMovement)
                        movements.get(i);

                    // if, for the current step of this movement, no moves
                    // have yet been performed
                    if (movement.movesDone == 0) {
                        // if the destination component of the current step
                        // is hidden
                        ContentPathStep step = (ContentPathStep)
                            movement.path.get(movement.currentStepIndex);
                        if (step.view.getViewableComponent().isHidden()) {
                            // move on to the next step
                            movement.currentStepIndex++;

                            // if this was the last step in the movement
                            if (movement.currentStepIndex >= movement.path.size()) {
                                discardMovement(movement);
                            }

                            continue;
                        }

                        // if the current step is the first one in this
                        // movement's path, or is a later step but no
                        // previous step could assign the movement a
                        // location
                        JComponent view = (JComponent)movement.view;
                        if (movement.currentStepIndex == 0
                            || movement.location == null) {
                            // use this step as the starting location for the
                            // movement of the content view
                            Point start = modelView.getLocation(
                                (JComponent)step.view);
                            PointUtil.translate(start,
                                step.view.getPortLocation(step.portName));
                            int viewX = start.x - view.getWidth() / 2,
                                viewY = start.y - view.getHeight() / 2;
                            view.setLocation(viewX, viewY);

                            // show the content view
                            view.setVisible(true);

                            // remember the content view's starting location
                            movement.location = new Point2D.Double(
                                viewX, viewY);

                            // go on to the next step in the movement
                            movement.currentStepIndex++;

                            // if this was the last step in the movement
                            if (movement.currentStepIndex >= movement.path.size()) {
                                discardMovement(movement);
                            }

                            continue;
                        }

                        // detm the starting location for this step
                        Point start = new Point(
                            (int)movement.location.x + view.getWidth() / 2,
                            (int)movement.location.y + view.getHeight() / 2);

                        // detm the finish location for this step
                        Point finish = modelView.getLocation((JComponent)step.view);
                        PointUtil.translate(finish,
                            step.view.getPortLocation(step.portName));

                        // compute the x and y offsets to take with each step
                        // along the line
                        movement.location = new Point2D.Double(start.x, start.y);
                        double angle = Math.atan2(finish.y - start.y,
                            finish.x - start.x);
                        final int speed = 2;
                        movement.dx = speed * Math.cos(angle);
                        movement.dy = speed * Math.sin(angle);

                        // detm how many moves it will take to get to the destination
                        Point2D.Double location = movement.location;
                        movement.movesRequired =
                            (int)Math.rint(location.distance(finish) / speed);
                        movement.movesDone = 0;

                        // start the content view centered over the source port location
                        location.x -= view.getWidth() / 2;
                        location.y -= view.getHeight() / 2;
                        view.setLocation((int)Math.rint(location.x),
                            (int)Math.rint(location.y));
                    }

                    // if not all the moves for the current step in the
                    // path have been done
                    if (movement.movesDone < movement.movesRequired) {
                        // move the content view one more step along the line
                        Point2D.Double location = movement.location;
                        location.x += movement.dx;
                        location.y += movement.dy;
                        movement.view.setLocation((int)Math.rint(location.x),
                            (int)Math.rint(location.y));
                        movement.movesDone++;
                    }

                    // otherwise
                    else {
                        // move on to the next step
                        movement.movesDone = 0;
                        movement.currentStepIndex++;

                        // if the content view is at the end of the path
                        if (movement.currentStepIndex >= movement.path.size()) {
                            discardMovement(movement);
                        }
                    }
                }
            }

            /**
             * Removes the given movement and its associated objects from
             * further consideration, so they may be gc'd.
             *
             * @param   movement        The movement object to discard.
             */
            protected void discardMovement(ContentViewMovement movement)
            {
                contentPathMap.remove(movement.view.getContent());
                contentViewMovementMap.remove(movement.path);
                movements.remove(movement);
            }
        }

        /**
         * A panel, which should cover the entire model-view, on which are drawn
         * the lines meant to represent the couplings between the components
         * in the model.
         */
        protected class CouplingsPanel extends JPanel
        {
            public CouplingsPanel()
            {
                setOpaque(false);
            }

            public void paint(Graphics g)
            {
                // if the couplings are currently supposed to be shown
                if (showCouplings || alwaysShowCouplings) {
                    // for each coupling
                    g.setColor(Color.lightGray);
                    int cz = couplings.size();
                    for (int i = 0; i < couplings.size(); i++) {
                        Coupling coupling = (Coupling)couplings.get(i);

                        // if either the source of destination views of the
                        // coupling are hidden
                        if (coupling.sourceView.getViewableComponent().
                            isHidden()
                            || coupling.destView.getViewableComponent().
                            isHidden()) {
                            // don't draw this coupling
                            continue;
                        }

                        // detm the pixel location within this model-view
                        // of the source port of the coupling
                        Point source = ModelView.this.getLocation(
                            (JComponent)coupling.sourceView);
                        PointUtil.translate(source,
                            coupling.sourceView.getPortLocation(
                            coupling.sourcePortName));

                        // detm the pixel location within this model-view
                        // of the destination port of the coupling
                        Point dest = ModelView.this.getLocation(
                            (JComponent)coupling.destView);
                        PointUtil.translate(dest,
                            coupling.destView.getPortLocation(
                            coupling.destPortName));

                        // draw this coupling's line
                        g.drawLine(source.x, source.y, dest.x, dest.y);
                    }
                }
            }
        }

        /**
         * Informs this model-view that the simulation has been restarted.
         */
        protected void simulationRestarted()
        {
            removeContentViews();
        }

        /**
         * Returns the location of the given descendant component within
         * this view.
         *
         * @param   component       The descendent component whose location is
         *                          desired.
         * @return                  The location of the component, relative to
         *                          the upper-left corner of this view.
         */
        public Point getLocation(JComponent component)
        {
            return ComponentUtil.getLocationRelativeToAncestor(component, this);
        }

        /**
         * Adds a coupling to this view's list of couplings.
         *
         * @param   coupling        The coupling to add.
         */
        public void addCoupling(Coupling coupling) {couplings.add(coupling);}

        //To display variable structure DEVS, Xiaolin Hu
        public void couplingAdded(IODevs src, String p1, IODevs dest, String p2){
          if(!(src instanceof ViewableComponent && dest instanceof ViewableComponent)){
             // skip this coupling - it can't be displayed
             System.out.println("Coupling could not be displayed."
                 + "\n\tFrom: " + src.getName()
                 + ", port " + p1
                 + "\n\tTo: " + dest.getName()
                 + ", port " + p2);
             return;
          }
          Coupling coupling = new Coupling();
          coupling.sourceView = ((ViewableComponent)src).getView();
          coupling.sourcePortName = p1;
          coupling.destView = ((ViewableComponent)dest).getView();
          coupling.destPortName = p2;

          addCoupling(coupling);
          repaint();
        }

        public void couplingAdded(Coupling cp){
          cp.printCoupling();
          addCoupling(cp);
          repaint();
        }

        public void savingModelViewCouplingsRemove(ViewableComponent iod, couprel savedCr){
          couprel mc = savedCr;
          Iterator it = mc.iterator();
          ViewableDigraph parent = new ViewableDigraph("none");

          while (it.hasNext()){
            Pair pr = (Pair)it.next();
            Pair cs = (Pair)pr.getKey();
            Pair cd = (Pair)pr.getValue();
            String src =  (String)cs.getKey();
            String dst =  (String)cd.getKey();

            //model = (ViewableDigraph)coordinator.getModel();
            atomicSimulator as = (atomicSimulator)coordinator;
            parent = (ViewableDigraph)as.getModel();
            IODevs source, dest;
            if(src.equals(parent.getName())){
              source = parent;
            }
            else{
              source = parent.withName(src);
            }

            if(dst.equals(parent.getName())){
              dest = parent;
            }
            else{
              dest = parent.withName(dst);
            }
            //s.s("Saving the coupling: "+src+"----"+(String)cs.getValue()+"----"+dst+"----"+(String)cd.getValue());

            this.couplingRemoved(source,(String)cs.getValue(),dest,(String)cd.getValue());

          }
          this.modelRemoved(iod,parent);


          //redraw the compponent
          DigraphView parentView = parent.getDigraphView();
          if (iod instanceof ViewableAtomic) {
            // tell the viewable atomic to create a view for itself
            ViewableAtomic atomic = (ViewableAtomic)iod;
            atomic.createView(modelView);
            // add the atomic view to the model's view
            AtomicView view1 = atomic.getAtomicView();
            //s.s(view1.getViewableComponent().getLayoutName());
            addView(view1, parentView);
          }
          // if this component is a viewable digraph
             if (iod instanceof ViewableDigraph) {
               // call this method recursively to get this digraph's
               // components' views created


                    ViewableDigraph digraph = (ViewableDigraph)iod;
           //         if(digraph.isBlackBox())
                    createViews(digraph, parentView);
             //       digraph.mySimView.detmCouplings((ViewableDigraph)iod);
             //       simview.detmCouplings((ViewableDigraph)iod);
              }

          repaint();
        }
        public void expandDigraph(ViewableDigraph graph, couprel cr){
          ViewableDigraph parent =(ViewableDigraph)graph.getParent();
          DigraphView parentView = parent.getDigraphView();
          s.s("Inside the expand graph in simview");
          this.modelRemoved(graph, parent);

//          graph.createView(this);
//          this.addView(graph.getDigraphView(),parentView);
          graph.printComponents(graph);
          graph.printCouprel(graph.getCouprel());
//          graph.setBlackBox(false);
//          this.createViews(graph,parentView);
//          this.modelAdded(graph,parent);

//          createViews(graph, parentView);
          //       digraph.mySimView.detmCouplings((ViewableDigraph)iod);
//          simview.detmCouplings(graph);

          repaint();
        }

        public void savedModelViewCouplingsAdd(ViewableComponent iod, couprel savedCr){

          couprel mc = savedCr;
          Iterator it = mc.iterator();
          atomicSimulator as = (atomicSimulator)coordinator;
          ViewableDigraph parent = (ViewableDigraph)as.getModel();

          while (it.hasNext()){
            Pair pr = (Pair)it.next();
            Pair cs = (Pair)pr.getKey();
            Pair cd = (Pair)pr.getValue();
            String src =  (String)cs.getKey();
            String dst =  (String)cd.getKey();

            IODevs source, dest;
            if(src.equals(parent.getName())){
              source = parent;
            }
            else{
              source = parent.withName(src);
            }

            if(dst.equals(parent.getName())){
              dest = parent;
            }
            else{
              dest = parent.withName(dst);
            }
       //     s.s("Drawing the coupling: "+src+"----"+(String)cs.getValue()+"----"+dst+"----"+(String)cd.getValue());

            this.couplingAdded(source,(String)cs.getValue(),dest,(String)cd.getValue());

          }
        }

        public void restoreInternalCouplings(ViewableDigraph digraph, couprel cr_){
          couprel mc = cr_;
          Iterator it = mc.iterator();
          ViewableDigraph parent = (ViewableDigraph)digraph;

          while (it.hasNext()){
            Pair pr = (Pair)it.next();
            Pair cs = (Pair)pr.getKey();
            Pair cd = (Pair)pr.getValue();
            String src =  (String)cs.getKey();
            String dst =  (String)cd.getKey();

            //s.s("Drawing the coupling: "+src+"----"+(String)cs.getValue()+"----"+dst+"----"+(String)cd.getValue());
            //s.s(parent.getName());

            IODevs source, dest;
            if(src.equals(parent.getName())){
              source = parent;
            }
            else{
              source = parent.withName(src);
            }

            if(dst.equals(parent.getName())){
              dest = parent;
            }
            else{
              dest = parent.withName(dst);
            }
            //s.s("source: "+source.getName());
            //s.s("dest :"+dest.getName());
            this.couplingAdded(source,(String)cs.getValue(),dest,(String)cd.getValue());

          }




        }





        public void couplingRemoved(IODevs src, String p1, IODevs dest, String p2){
            String srcName, destName;
            ViewableDigraph parent;
            for (int i = 0; i < couplings.size(); i++) {
                Coupling coupling = (Coupling)couplings.get(i);
                srcName = (coupling.sourceView).getViewableComponent().getName();
                ViewableComponent srcView = (coupling.sourceView).getViewableComponent();
                if(srcView instanceof ViewableDigraph){
                  parent = ((ViewableDigraph)srcView).getMyParent();
                }
                else{
                  parent = ((ViewableAtomic)srcView).getMyParent();
                }
                destName = (coupling.destView).getViewableComponent().getName();
                ViewableComponent destView = (coupling.destView).getViewableComponent();
                if(srcName.compareTo(src.getName())==0&&coupling.sourcePortName.compareTo(p1)==0&&
                    destName.compareTo(dest.getName())==0&&coupling.destPortName.compareTo(p2)==0){
                      couplings.remove(i);
                      //modelRemoved(destView,parent);
 //                     removeView(coupling.destView, parent.getDigraphView());

                      break;
                    }
            }

        }

        public void modelAdded(ViewableComponent iod, ViewableDigraph parent){

          DigraphView parentView = parent.getDigraphView();
          if (parent.isBlackBox() || parent.isHidden()) iod.setHidden(true);
          if (iod instanceof ViewableAtomic) {
                // tell the viewable atomic to create a view for itself
                ViewableAtomic atomic = (ViewableAtomic)iod;
                atomic.createView(modelView);
                // add the atomic view to the model's view
                AtomicView view1 = atomic.getAtomicView();
                addView(view1, parentView);
          }
          // if this component is a viewable digraph
          if (iod instanceof ViewableDigraph) {
                // call this method recursively to get this digraph's
                // components' views created
                ViewableDigraph digraph = (ViewableDigraph)iod;
                createViews(digraph, parentView);
                simview.detmCouplings((ViewableDigraph)iod);
          }
          repaint();

        }

        public void modelRemoved(ViewableComponent iod, ViewableDigraph parent){

          DigraphView parentView = parent.getDigraphView();
          if (iod instanceof ViewableAtomic)
            removeView(((ViewableAtomic)iod).getAtomicView(),parentView);
          else if(iod instanceof ViewableDigraph)
            destroyModelView((ViewableDigraph)iod,parentView);
          repaint();


        }

        public void destroyModelView(ViewableDigraph model, ComponentView parentView){
          String srcName, destName, compName="";
          // for each component in the digraph
          Iterator i = model.getComponents().iterator();
          while (i.hasNext()) {
            Object component = i.next();
            // if this component is a viewable atomic
            if (component instanceof ViewableAtomic){
                compName = ((ViewableAtomic)component).getName();
                removeView(((ViewableAtomic)component).getView(),(JComponent)(model.getView()));
            }
            // if this component is a viewable digraph
            if (component instanceof ViewableDigraph){
                compName = ((ViewableDigraph)component).getName();
                destroyModelView((ViewableDigraph)component, model.getView());
            }
            //need to remove the coupling connected to that component from the CouplingsPanel
            int csize = couplings.size();  // the size of the couplings
            for (int j = csize-1; j >=0; j--) { // a special way to check all the elements in the Arraylist while removing element dynamicly
                Coupling coupling = (Coupling)couplings.get(j);
                srcName = (coupling.sourceView).getViewableComponent().getName();
                destName = (coupling.destView).getViewableComponent().getName();
                if(srcName.compareTo(compName)==0||destName.compareTo(compName)==0) couplings.remove(j);
            }
          }
          removeView(model.getView(),(JComponent)parentView);
        }

    /**
     * Creates an atomic-view for each viewable atomic in the given
     * digraph model, as well as for those recursively found contained
     * within in the given model's children models.  Each new
     * atomic-view is added to the model-view.
     *
     * @param   model       The model for whose atomic components views are
     *                      to be created.
     * @param   parent      The parent component to which the given model's
     *                      views are to be added.
     */
    protected void createViews(ViewableDigraph model, JComponent parent)
    {
        // tell the model to create a view for itself
        model.createView(modelView);

        // if the given model isn't to be displayed as a black box
        if (!model.isBlackBox()) {
            // tell the model to lay itself out for the viewer (but don't
            // let any problems in that process cause things to halt, as it
            // is not a critical objective)
          if(!usePrevModelLayout)
          try {
                // if the override layout method doesn't get the layout done
                if (!model.layoutForSimViewOverride()) {
                    // call the automatically-generated layout method
                    model.layoutForSimView();
                }
            } catch (Exception e) {e.printStackTrace();}
        }

        // add the model's view to the given parent
        DigraphView view;
        //       if(!showLevel){
        view = model.getDigraphView();
        modelView.addView(view, parent);

        // add the model's view to the given parent
//        DigraphView view = model.getDigraphView();
//        addView(view, parent);



        // if the given digraph is the top-level digraph in the model being viewed
        if (parent == modelView) {
            // set the location of the digraph's view to the upper-left hand
            // corner of the model-view
          modelView.model = this.model;//by saurabh
          modelView.modelDigView = view;//by saurabh
          view.setLocation(new Point(modelBorderWidth, modelBorderWidth));
        }

        // for each component in the digraph
        Iterator i = model.getComponents().iterator();
        while (i.hasNext()) {
            Object component = i.next();

            // if the given digraph is itself a black box, or is hidden
            if (model.isBlackBox() || model.isHidden()) {
                // if this component is viewable
                if (component instanceof ViewableComponent) {
                    // tell this component it's hidden
                    ViewableComponent comp = (ViewableComponent)component;
                    comp.setHidden(true);
                }
            }

            // if this component is a viewable atomic
            if (component instanceof ViewableAtomic) {
                // tell the viewable atomic to create a view for itself
                ViewableAtomic atomic = (ViewableAtomic)component;
                atomic.createView(modelView);

                // add the atomic view to the model's view
                AtomicView view1 = atomic.getAtomicView();
//                atomic.setView(view1);//added by sm
                modelView.addView(view1, view);
//                addView(view1, view);

                // if this atomic is the whole model being viewed (and thus,
                // is wrapped in a wrapper digraph)
                if (model.getName().equals(wrapperDigraphName)) {
                    // center the atomic in the model-view
                    view1.setLocation(
                        modelViewScrollPane.getWidth() / 2 - view.getWidth() / 2,
                        modelViewScrollPane.getHeight() / 2 - view.getHeight() / 2);
                }
            }

            // if this component is a viewable digraph
            if (component instanceof ViewableDigraph) {
                // call this method recursively to get this digraph's
                // components' views created
                ViewableDigraph digraph = (ViewableDigraph)component;
                createViews(digraph, view);
            }
        }
    }


        /**
         * Clears this view's list of couplings.
         */
        public void clearCouplings() {couplings.clear();}
    }

    /**
     * Adds the names of all java class files (which are assumed to
     * be compiled devs java models) in the models-path to
     * the given combo-box.
     *
     * @param   box     The combo-box to which to add the model names.
     */
    protected void populateModelsBox(JComboBox box)
    {
        // create a filename filter (to be used below) that will
        // match against ".class" files (and ignore inner classes)
        final String extension = ".class";
        FilenameFilter filter =  new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(extension) && name.indexOf('$') == -1;
            }
        };

        // find all java class files (that aren't inner classes) in the
        // models-path
        File path = new File(modelsPath + "/" + modelsPackage.replace('.', '/'));
        File[] files = path.listFiles(filter);

        // if the models-path doesn't exist
        if (files == null) {
            JOptionPane.showMessageDialog(mainFrame,
                "The selected models package does not appear to be available for loading.  Please select another.");
            return;
        }

        // sort the names of the java class files found above
        TreeSet sortedFiles = new TreeSet(Arrays.asList(files));

        box.removeAllItems();

        box.addItem("Select a model");

        // for each java class file in the models-path (we are assuming
        // each such file to be a compiled devs java model)
        Iterator i = sortedFiles.iterator();
        while (i.hasNext()) {
            // add this class file's name (minus its extension) to the box
            String name = ((File)i.next()).getName();
            box.addItem(name.substring(0,
                name.length() - extension.length()));
        }
    }

    /**
     * Creates an instance of the model class of the given name (including
     * the package name).  Also, creates the views and coordinator for the
     * model instance.
     *
     * @param   name        The name of the model class to instantiate.
     */
    protected void useModelClass(String name)
    {
      // if there is a previous model in use
      if (model != null) {
        // stop the previous model's simulation execution
        coordinator.simulate(0);

        // for each frame existing in this application
        Frame[] frames = mainFrame.getFrames();
        for (int i = 0; i < frames.length; i++) {
          // if this frame isn't this sim-view's main frame
          if (frames[i] != mainFrame) {
            // dispose of this frame
            frames[i].setVisible(true);
            frames[i].dispose();
          }
        }

        saveModelLayout();
      }

      // create an instance of the selected class
      Object instance = new Object(); //new object created by saurabh


      if(!showLevel){
        // create an instance of the selected class
        try {
          Class modelClass = Class.forName(modelsPackage + "." + name);
          instance = modelClass.newInstance();
        } catch (Exception e) {
          JOptionPane.showMessageDialog(mainFrame,
                                        "That does not appear to be a valid model class.  Please select another.");
          e.printStackTrace();
          return;
        }


        // if the instance is a viewable-atomic
        if (instance instanceof ViewableAtomic) {
          // wrap the instance in a digraph, and have the digraph be our model
          model = new ViewableDigraph(wrapperDigraphName);
          model.add((atomic)instance);

          // for each of the names of the outports of the atomic
          ViewableAtomic atomic = (ViewableAtomic)instance;
          List names = atomic.getOutportNames();
          for (int i = 0; i < names.size(); i++) {
            String portName = (String)names.get(i);

            // add an outport with this port name to the wrapper digraph,
            // and couple it to the atomic's outport of this name,
            // so that outputs from that outport will be visible
            // when they are emitted
            model.addOutport(portName);
            model.addCoupling(atomic, portName, model, portName);
          }
        }

        // else, if the instance is a viewable-digraph
        else if (instance instanceof ViewableDigraph) {
          // have the instance itself be our model
          ViewableDigraph dig = (ViewableDigraph)instance;
          //dig.setSimView(this);
          model = (ViewableDigraph)instance;

        }

        // otherwise
        else {
          // don't use the model
          JOptionPane.showMessageDialog(mainFrame,
                                        "That does not appear to be a viewable model class.  Please select another.");
          return;
        }



        }//for showLevel
        else{
        }



        // get rid of any old views
        modelView.removeAllViews();

        // create a coordinator
        coordinator = new SimViewCoordinator(model, modelView);
        coordinator.initialize();

        // have the coordinator use the currently selected real time factor
        coordinator.setTimeScale(realTimeFactor.get());

        // create the new views and lay them out
        //createViews(model, modelView);  // changed by Xiaolin Hu
        modelView.createViews(model, modelView);

        // add all the coupings in the model to the model-view
        modelView.clearCouplings();
        detmCouplings(model);

        // get the model-view scrollpane resized to take into account
        // the possibly new model-view size
        ((JComponent)modelViewScrollPane.getParent()).revalidate();

        // if the model is an atomic (being wrapped by a dummy digraph)
        if (instance instanceof ViewableAtomic) {
            // center the atomic within the wrapper digraph
            AtomicView view = ((ViewableAtomic)instance).getAtomicView();
            Dimension viewSize = view.getPreferredSize();
            Dimension wrapperSize = model.getPreferredSize();
            view.setLocation(wrapperSize.width / 2 - viewSize.width / 2,
                wrapperSize.height / 2 - viewSize.height / 2);
        }

        // reset the clock
        clock.set(0);

        // remember the new current model class as the last one
        // successfully viewed
        lastModelViewed = name;
    }

        /*added by Saurabh
    */
    public void useModelClass_SM(ViewableDigraph dg, String className, double prevSimTime)
    //protected void useModelClass_SM()
    {

      Object instance = new Object();
//      System.out.println("ModelName is ...the top part   " + dg.getName());

      // create an instance of the selected class
      if(showLevel){
        model = (ViewableDigraph)dg;
        model.setSimView(this);
      }
      else{
        if(!showLevel){
          try {
            //Class modelClass = Class.forName(className);
            Class modelClass = dg.getClass();
            instance = modelClass.newInstance();
          } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame,
                "That does not appear to be a valid operation.");
            e.printStackTrace();
            return;
          }
          model = (ViewableDigraph)instance;
          model.setSimView(this);
//          System.out.println("ModelName is " + model.getName());
        }
      }

      if(model.isBlackBox()){
        model.setBlackBox(false);
      }

      modelView = new ModelView(this);
      modelView.removeAllViews();

      // create a coordinator
      coordinator ncoordinator = new SimViewCoordinator(model, modelView);
      ncoordinator.initialize(prevSimTime); ///get the prev clock from the earliee simView
      coordinator = (SimViewCoordinator)ncoordinator;

      // have the coordinator use the currently selected real time factor
      coordinator.setTimeScale(realTimeFactor.get());

      model.getSimView().modelView.createViews(model, modelView);
      model.getSimView().detmCouplings(model);

    }

////////////////////////////////////////////////



    /**
     * A wrapper for a clock value, changes to which must be accompanied
     * by other actions.
     */
    protected class Clock
    {
        /**
         * The value being wrapped.
         */
        private double clock;

        public double get() {return clock;}

        /**
         * Updates the wrapped variable, and performs resulting side effects.
         */
        public void set(double clock_)
        {
            clock = clock_;

            // update the clock label
            clockLabel.setText(decimalFormat.format(clock));
        }
    }

    /**
     * A wrapper for a real-time-factor value, changes to which must be
     * accompanied by other actions.
     */
    protected class RealTimeFactor
    {
        /**
         * The value being wrapped.
         */
        private double realTimeFactor =
            realTimeFactors[realTimeFactors.length / 2];

        public double get() {return realTimeFactor;}

        /**
         * Updates the wrapped variable, and performs resulting side effects.
         */
        public void set(double realTimeFactor_)
        {
            realTimeFactor = realTimeFactor_;

            // update the speed label
            if (realTimeFactorLabel != null) {
                realTimeFactorLabel.setText("" + realTimeFactor);
            }

            // tell the coordinator to use the new speed
            if (coordinator != null) {
                coordinator.setTimeScale(realTimeFactor);
            }
        }
    }

    /**
     * Adds the names of all the user-specified java packages containing
     * models to the given combo-box.
     *
     * @param   box     The combo-box to which to add the model names.
     */
    protected void populatePackagesBox(JComboBox box)
    {
        box.removeAllItems();

        box.addItem("Select a package");

        // if the user has specified a list of model packages
        if (modelPackages != null) {
            // for each model package in the list
            for (int i = 0; i < modelPackages.size(); i++) {
                // add this package's name to the given combo-box
                box.addItem((String)modelPackages.get(i));
            }
        }
    }

    /**
     * A dialog in which the user may specify various program-wide settings.
     */
    protected class ConfigureDialog extends JDialog
    {
        /**
         * Constructs a configure-dialog.
         *
         * @param   owner       The parent frame of this dialog.
         */
        public ConfigureDialog(Frame owner)
        {
            super(owner, "Configure", true);

            setSize(400, 300);
            WindowUtil.centerWindow(this);

            constructUI();
        }

        /**
         * Constructs the UI of this dialog.
         */
        protected void constructUI()
        {
            // have the close-window button do nothing for this dialog
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            // add the main panel
            Container pane = getContentPane();
            JPanel main = new JPanel();
            main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
            main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            pane.add(main);

            // add the class path label
            JLabel label = new JLabel("Path to packages of model classes (from current folder)");
            label.setAlignmentX(0.0f);
            main.add(label);

            // add the class path field
            final JTextField classPathField = new JTextField(modelsPath);
            JTextField field = classPathField;
            field.setAlignmentX(0.0f);
            main.add(field);

            // limit the height of the class path field
            field.setMaximumSize(new Dimension(1000,
                (int)(1.5 * getFontMetrics(field.getFont()).getHeight())));

            main.add(Box.createRigidArea(new Dimension(0, 10)));

            // add the source path label
            label = new JLabel("Path to packages of model source files (from current folder)");
            label.setAlignmentX(0.0f);
            main.add(label);

            // add the source path field
            final JTextField sourcePathField = new JTextField(sourcePath);
            field = sourcePathField;
            field.setAlignmentX(0.0f);
            main.add(field);

            // limit the height of the source path field
            field.setMaximumSize(new Dimension(1000,
                (int)(1.5 * getFontMetrics(field.getFont()).getHeight())));

            main.add(Box.createRigidArea(new Dimension(0, 10)));

            // add the package names label
            label = new JLabel("Model package names (one per line)");
            label.setAlignmentX(0.0f);
            main.add(label);

            // add the package names text area
            final JTextArea packagesArea = new JTextArea(modelsPath);
            JTextArea area = packagesArea;
            JScrollPane scrollPane = new JScrollPane(area);
            scrollPane.setAlignmentX(0.0f);
            main.add(scrollPane);

            // for each entry in our model-packages list
            String text = "";
            for (int i = 0; i < modelPackages.size(); i++) {
                // add this entry as a line to the text area's text
                text += ((String)modelPackages.get(i)) + "\n";
            }
            area.setText(text);

            // have ctrl-insert do a copy action in the text area
            Keymap keymap = area.addKeymap(null, area.getKeymap());
            KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT,
                Event.CTRL_MASK);
            keymap.addActionForKeyStroke(key, new DefaultEditorKit.CopyAction());

            // have shift-insert do a paste action in the text area
            key = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, Event.SHIFT_MASK);
            keymap.addActionForKeyStroke(key,
                new DefaultEditorKit.PasteAction());
            area.setKeymap(keymap);

            main.add(Box.createRigidArea(new Dimension(0, 10)));

            JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            panel.setAlignmentX(0.0f);
            main.add(panel);

            panel.add(Box.createHorizontalGlue());

            // add the ok button
            JButton button = new JButton("Ok");
            button.setAlignmentX(0.0f);
            panel.add(button);

            // when the ok button is clicked
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // store the class path field entry
                    modelsPath = classPathField.getText();
                    if (modelsPath.equals("")) {
                        modelsPath = ".";
                    }

                    // if there is a trailing slash on the class path entry
                    if (modelsPath.endsWith("/")) {
                        // remove it
                        modelsPath = modelsPath.substring(0,
                            modelsPath.length() - 1);
                    }

                    // store the source path field entry
                    sourcePath = sourcePathField.getText();
                    if (sourcePath.equals("")) {
                        sourcePath = ".";
                    }

                    // if there is a trailing slash on the source path entry
                    if (sourcePath.endsWith("/")) {
                        // remove it
                        sourcePath = sourcePath.substring(0,
                            sourcePath.length() - 1);
                    }

                    // keep doing this
                    modelPackages = new ArrayList();
                    StringReader stringReader =
                        new StringReader(packagesArea.getText());
                    BufferedReader reader = new BufferedReader(stringReader);
                    while (true) {
                        // read the next line specified in the packages text
                        // area
                        String line = null;
                        try {
                            line = reader.readLine();
                        } catch (IOException ex) {ex.printStackTrace(); continue;}

                        // if there are no more lines
                        if (line == null) break;

                        // if this line is blank, skip it
                        line = line.trim();
                        if (line.equals("")) continue;

                        // add this line's package name to our list of package
                        // names
                        modelPackages.add(line);
                    }

                    populatePackagesBox(packagesBox);

                    saveSettings();

                    setVisible(false);
                }
            });
        }
    }

    /**
     * A dialog in which displays the program's help text.
     */
    protected class HelpDialog extends JDialog
    {
        /**
         * Constructs an object of this class.
         *
         * @param   owner       The parent frame of this dialog.
         */
        public HelpDialog(Frame owner)
        {
            super(owner, "Help", true);

            setSize(550, 400);
            WindowUtil.centerWindow(this);

            constructUI();
        }

        /**
         * Constructs the UI of this dialog.
         */
        protected void constructUI()
        {
            // add the main panel
            Container pane = getContentPane();
            JPanel main = new JPanel();
            main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
            main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            pane.add(main);

            // add the help text area
            JTextPane textPane = new JTextPane();
            try {
                textPane.setPage(new URL("file:SimView.html"));
            } catch (Exception e) {e.printStackTrace();}
            textPane.setEditable(false);
            textPane.setFont(new Font("monospaced", Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(textPane);
            main.add(scrollPane);

            main.add(Box.createRigidArea(new Dimension(0, 5)));

            JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            main.add(panel);

            // add the ok button
            JButton button = new JButton("Ok");
            panel.add(button);

            // when the ok button is clicked
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // close this dialog
                    setVisible(false);
                }
            });
        }
    }

    /**
     * Loads this program's settings from its settings files.
     */
    protected void loadSettings()
    {
        try {
            // read in the settings from the settings file
            InputStream in = new FileInputStream(settingsFileName);
            ObjectInputStream s = new ObjectInputStream(in);
            modelsPath = (String)s.readObject();
            modelPackages = (List)s.readObject();
            alwaysShowCouplings = s.readBoolean();
            realTimeFactor.set(s.readDouble());
            modelsPackage = (String)s.readObject();
            lastModelViewed = (String)s.readObject();
            sourcePath = (String)s.readObject();
        } catch (Exception e) {
            System.out.println("Couldn't read settings from file.");
            if (modelsPath == null) modelsPath = ".";
            if (sourcePath == null) sourcePath = ".";
            if (modelPackages == null) modelPackages = new ArrayList();
        }
    }

    /**
     * Saves this program's settings to its settings files.
     */
    protected void saveSettings()
    {
        try {
            // write out the current settings to the settings file
            FileOutputStream out = new FileOutputStream(settingsFileName);
            ObjectOutputStream s = new ObjectOutputStream(out);
            s.writeObject(modelsPath);
            s.writeObject(modelPackages);
            s.writeBoolean(alwaysShowCouplings);
            s.writeDouble(realTimeFactor.get());
            s.writeObject(modelsPackage);
            s.writeObject(lastModelViewed);
            s.writeObject(sourcePath);
            s.flush();
        } catch (IOException e) {e.printStackTrace();}
    }

    /**
     * A coupling between a source port on one devs component view, and
     * a destination port on another.
     */
    protected class Coupling
    {
        /**
         * The source and destination component views.
         */
        public ComponentView sourceView, destView;

        /**
         * The source and destination port names.
         */
        public String sourcePortName, destPortName;

        public void printCoupling(){
          System.out.println("Inside coupling: source:<"+sourceView.getViewableComponent().getLayoutName()+"> <sourcePort:"+sourcePortName+"> destination:<"+destView.getViewableComponent().getLayoutName()+"> <destPort:"+destPortName);
        }
    }

    /**
     * Determines all the couplings present within the given model, and adds
     * them to the model-view for display.
     *
     * @param   model       The model for which to find all the couplings.
     */
    protected void detmCouplings(ViewableDigraph model)
    {
        detmCouplings((ViewableComponent)model);

        // for each component in the model
        Iterator i = model.getComponents().iterator();
        while (i.hasNext()) {
            Object component = i.next();

            // if this component is a viewable digraph
            if (component instanceof ViewableDigraph) {
                // call this method recursively to get this digraph's
                // components' couplings detm'd
                ViewableDigraph digraph = (ViewableDigraph)component;
                detmCouplings(digraph);
            }

            // else, if this component is a viewable component
            else if (component instanceof ViewableComponent) {
                detmCouplings((ViewableComponent)component);
            }
        }
    }

    /**
     * Determines all the couplings for which a port on the given component
     * is the source, and adds those couplings to the model-view for display.
     *
     * @param   comp       The devs component whose couplings are to be found.
     */
    protected void detmCouplings(ViewableComponent comp)
    {
        // find all the couplings for which an outport of the component
        // is the source
        detmCouplings(comp, comp.getOutportNames());

        // if the component is a digraph
        if (comp instanceof ViewableDigraph) {
            // find all the couplings for which an inport of the component
            // is the source
            detmCouplings(comp, comp.getInportNames());
        }
    }

    /**
     * Determines all the couplings for which a port from the given list
     * (on the given component) is the source, and adds those couplings
     * to the model-view for display.
     *
     * @param   comp        The devs component to which the ports belong.
     * @param   portNames   The list of port names on the above component
     *                      to look for couplings.
     */
    protected void detmCouplings(ViewableComponent comp, List portNames)
    {
        // for each port in the given list
        for (int i = 0; i < portNames.size(); i++) {
            String portName = (String)portNames.get(i);

            // ask the given component's simulator or coordinator for the
            // couplings for which this port is a source
            List couplings = null;
            if (comp instanceof ViewableAtomic) {
                couplings = ((coupledSimulator)((atomic)comp).getSim()).
                    getCouplingsToSourcePort(portName);
            }
            else if (comp instanceof ViewableDigraph) {
                couplings = ((digraph)comp).getCoordinator().
                    getCouplingsToSourcePort(portName);
            }

            // for each coupling to this port
            for (int j = 0; j < couplings.size(); j++) {
                Pair pair = (Pair)couplings.get(j);

                // create a coupling object and store this coupling's
                // source component-view and port
                Coupling coupling = new Coupling();
                coupling.sourceView = comp.getView();
                coupling.sourcePortName = portName;

                // if the component at the destination end of this coupling
                // is not a viewable-component
                entity destEntity = (entity)pair.getKey();
                String destPortName = (String)pair.getValue();
                if (!(destEntity instanceof ViewableComponent)) {
                    // skip this coupling - it can't be displayed
                    System.out.println("Coupling could not be displayed."
                        + "\n\tFrom: " + comp.getName()
                        + ", port " + portName
                        + "\n\tTo: " + destEntity.getName()
                        + ", port " + destPortName);
                    continue;
                }

                // store this coupling's destination component-view and port
                coupling.destView = ((ViewableComponent)destEntity).getView();
                coupling.destPortName = destPortName;

                // add the coupling to the model-view
                modelView.addCoupling(coupling);
            }
        }
    }

    /**
     * A step in a content-path list of such steps.
     */
    protected class ContentPathStep
    {
        /**
         * At which view the content should be displayed for this step.
         */
        public ComponentView view;

        /**
         * At which port on the above view the content should be
         * displayed for this step.
         */
        public String portName;
    }

    /**
     * Returns this sim-view's model-view.
     */
    public ModelView getModelView() {return modelView;}

    /**
     * A key used to find a particular content-path in the content-path-map.
     */
    protected class ContentPathKey
    {
        /**
         * The latest incarnation of the content traversing the path.
         */
        ContentInterface content;

        /**
         * The current component the above content has reached in its path.
         */
        String componentName;

        /**
         * Constructs a key.
         */
        public ContentPathKey(ContentInterface content_, String componentName_)
        {
            content = content_;
            componentName = componentName_;
        }

        /**
         * Equates the given object to this key if it is of the same class,
         * if its content port and values match this key's,
         * and if its component name matches this key's.
         */
        public boolean equals(Object o)
        {
            if (o instanceof ContentPathKey) {
                ContentPathKey pair = (ContentPathKey)o;
                return content.equals(pair.content) &&
                    componentName.equals(pair.componentName);
            }

            return false;
        }

        /**
         * Returns a hash-code for this key that is based on a combination
         * of its component-name and its content's port-name and value.
         */
        public int hashCode()
        {
            return (componentName + content.getPort() + content.getValue())
                .hashCode();
        }
    }

    /**
     * Puts this sim-view's status label in "stepping" mode.
     */
    protected void setStatusLabelToStepping()
    {
        statusLabel.setForeground(Color.red.darker());
        statusLabel.setText("stepping");
    }

    /**
     * Puts this sim-view's status label in "running" mode.
     */
    protected void setStatusLabelToRunning()
    {
        statusLabel.setForeground(Color.blue.darker());
        statusLabel.setText("running");
    }

    /**
     * Puts this sim-view's status label in "ready" mode.
     */
    protected void setStatusLabelToReady()
    {
        statusLabel.setForeground(Color.green.darker());
        statusLabel.setText("ready");
    }

    /**
     * Saves the current top-level model's layout (within this sim-view)
     * to its source code file.
     */
    protected void saveModelLayout() {saveModelLayout(model);}

    /**
     * Saves the given model's layout (within this sim-view)
     * to its source code file.
     */
    protected void saveModelLayout(ViewableDigraph model)
    {
        // if no model is currently being viewed, or the given model
        // is being viewed as black box (or is hidden), then abort this method
        if (model == null || model.isBlackBox() || model.isHidden()) return;

        // if the model's layout hasn't changed since its last save
        if (!model.getLayoutChanged()) {
            // just look into whether the children's layouts need to be
            // saved
            saveLayoutsOfChildren(model);
            return;
        }

        // detm the file name of the java source code file for the current
        // model
        String className = model.getClass().getName().replace('.', '/');

        // if we can't load the model's source code into memory as a
        // big string
        File file = new File(sourcePath + "/" + className + ".java");
        String code = FileUtil.getContentsAsString(file);

        // if we can find a previously generated layoutForSimView method
        int index = code.indexOf("void layoutForSimView()");
        if (index != -1) {
            // find the index of the first line of the method's
            // autogenerated comment
            int startIndex = code.lastIndexOf("/**", index);
            startIndex = code.lastIndexOf("\n", startIndex);

            // find the index of the end of the last line of the
            // method's body
            int endIndex = code.indexOf("}", index);
            endIndex = code.indexOf("\n", endIndex);

            // remove the method and its comment
            code = code.substring(0, startIndex)
                + code.substring(endIndex, code.length());
        }

        // find the ending brace in the file, which is assumed to be the
        // closing bracket of the only class in the file; the method will
        // be inserted on the line just before this bracket
        index = code.lastIndexOf("}");
        index = code.lastIndexOf("\n", index);

        // build the comment and declaration of the method
        StringBuffer method = new StringBuffer("\n");
        method.append("    /**\n");
        method.append("     * Automatically generated by the SimView program.\n");
        method.append("     * Do not edit this manually, as such changes will get overwritten.\n");
        method.append("     */\n");
        method.append("    public void layoutForSimView()\n");
        method.append("    {\n");

        // if the model isn't currently displayed as a black box
        if (!model.isBlackBox()) {
            // add the call to set the model's preferred size
            method.append("        preferredSize = new Dimension(");
            Dimension size = ((JComponent)model.getView()).getSize();
            method.append(size.width);
            method.append(", ");
            method.append(size.height);
            method.append(");\n");
        }

        // for each child component of the model
        Iterator i = model.getComponents().iterator();
        while (i.hasNext()) {
            // if this component isn't a viewable-component, skip it
            Object next = i.next();
            if (!(next instanceof ViewableComponent)) continue;
            ViewableComponent component = (ViewableComponent)next;

            // if this component is hidden, skip it
            if (component.isHidden()) continue;

            // add the call to set this component's location
            method.append("        ");
            method.append("if((ViewableComponent)withName("
                + component.getLayoutName());
            method.append(")!=null)\n");   // Xiaolin Hu, Sept. 2007
            method.append("             ");
            method.append("((ViewableComponent)withName("
                + component.getLayoutName());
            method.append(")).setPreferredLocation(new Point(");
            Point location = component.getPreferredLocation();
            method.append(location.x);
            method.append(", ");
            method.append(location.y);
            method.append("));\n");
        }

        // add the closing brace of the method
        method.append("    }\n");

        // insert the method into the code
        code = code.substring(0, index) + method
            + code.substring(index + 1, code.length());

        // write the updated source code back to the java file
        file = new File(file.getPath());
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(code, 0, code.length());
            out.flush();
        } catch (IOException e) {e.printStackTrace();}

        // tell the model its layout now hasn't changed since its last save
        model.setLayoutChanged(false);

        saveLayoutsOfChildren(model);
    }

    /**
     * Saves the layouts (within this sim-view) of the children components
     * of the given model to their associated source code files.
     */
    protected void saveLayoutsOfChildren(ViewableDigraph model)
    {
        // for each child component of the model
        Iterator i = model.getComponents().iterator();
        while (i.hasNext()) {
            // if this component isn't a viewable-component, skip it
            Object next = i.next();
            if (!(next instanceof ViewableComponent)) continue;
            ViewableComponent component = (ViewableComponent)next;

            // if this component is itself a viewable digraph
            if (component instanceof ViewableDigraph) {
                ViewableDigraph digraph = (ViewableDigraph)component;

                // call this method recursively to get this digraph's
                // layout method created/updated
                saveModelLayout(digraph);
            }
        }
    }
}




