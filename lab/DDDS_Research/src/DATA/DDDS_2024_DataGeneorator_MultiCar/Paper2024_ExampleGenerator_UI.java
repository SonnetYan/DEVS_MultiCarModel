package DATA.DDDS_2024_DataGeneorator_MultiCar;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.EventQueue;

public class Paper2024_ExampleGenerator_UI {
    private JFrame frame;
    private JTextField runField;
    private JTextField carPassingTimeMeanField; // New field

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Paper2024_ExampleGenerator_UI window = new Paper2024_ExampleGenerator_UI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Paper2024_ExampleGenerator_UI() {
        initialize();
    }

    private void initialize() {
        // Frame setup
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
    
        // Run label and field
        JLabel lblRun = new JLabel("Run:");
        lblRun.setBounds(10, 11, 46, 14);
        frame.getContentPane().add(lblRun);
    
        runField = new JTextField();
        runField.setBounds(66, 11, 86, 20);
        frame.getContentPane().add(runField);
        runField.setColumns(10);
        runField.setText(String.valueOf(model_configData.total_run));
    
        // Car Passing Time Mean label and field
        JLabel lblCarPassingTimeMean = new JLabel("Car Passing Time Mean:");
        lblCarPassingTimeMean.setBounds(10, 41, 150, 14);
        frame.getContentPane().add(lblCarPassingTimeMean);
    
        carPassingTimeMeanField = new JTextField();
        carPassingTimeMeanField.setBounds(170, 41, 86, 20);
        frame.getContentPane().add(carPassingTimeMeanField);
        carPassingTimeMeanField.setColumns(10);
        carPassingTimeMeanField.setText(String.valueOf(model_configData.carPassingTime_mean));
    
        // East Moving Lambda label and field
        JLabel lblEastMovingLambda = new JLabel("East arriving Rate: 1/");
        lblEastMovingLambda.setBounds(10, 71, 150, 14);
        frame.getContentPane().add(lblEastMovingLambda);
        
        JTextField eastMovingLambdaField = new JTextField();
        eastMovingLambdaField.setBounds(170, 71, 86, 20);
        eastMovingLambdaField.setColumns(10);
        eastMovingLambdaField.setText(String.valueOf(1/model_configData.generator_eastMoving_lambda));
        frame.getContentPane().add(eastMovingLambdaField);
    
        // West Moving Lambda label and field
        JLabel lblWestMovingLambda = new JLabel("West Arriving Rate 1/" );
        lblWestMovingLambda.setBounds(10, 101, 150, 14);
        frame.getContentPane().add(lblWestMovingLambda);
        
        JTextField westMovingLambdaField = new JTextField();
        westMovingLambdaField.setBounds(170, 101, 86, 20);
        westMovingLambdaField.setColumns(10);
        westMovingLambdaField.setText(String.valueOf(1/model_configData.generator_westMoving_lambda));
        frame.getContentPane().add(westMovingLambdaField);
    
        // Max Elapse Time When Busy for East to West label and field
        JLabel lblMaxElapseTimeWhenBusyE2W = new JLabel("maxLightSwitchTime E2W:");
        lblMaxElapseTimeWhenBusyE2W.setBounds(10, 131, 150, 14);
        frame.getContentPane().add(lblMaxElapseTimeWhenBusyE2W);
        
        JTextField maxElapseTimeWhenBusyFieldE2W = new JTextField();
        maxElapseTimeWhenBusyFieldE2W.setBounds(170, 131, 86, 20);
        maxElapseTimeWhenBusyFieldE2W.setColumns(10);
        maxElapseTimeWhenBusyFieldE2W.setText(String.valueOf(model_configData.maxElapseTimeWhenBusy_E2W));
        frame.getContentPane().add(maxElapseTimeWhenBusyFieldE2W);

        // Max Elapse Time When Busy for West to East label and field
        JLabel lblMaxElapseTimeWhenBusyW2E = new JLabel("maxLightSwitchTime W2E:");
        lblMaxElapseTimeWhenBusyW2E.setBounds(10, 161, 150, 14);
        frame.getContentPane().add(lblMaxElapseTimeWhenBusyW2E);
        
        JTextField maxElapseTimeWhenBusyFieldW2E = new JTextField();
        maxElapseTimeWhenBusyFieldW2E.setBounds(170, 161, 86, 20);
        maxElapseTimeWhenBusyFieldW2E.setColumns(10);
        maxElapseTimeWhenBusyFieldW2E.setText(String.valueOf(model_configData.maxElapseTimeWhenBusy_W2E));
        frame.getContentPane().add(maxElapseTimeWhenBusyFieldW2E);
        
        // Start button
        JButton btnStart = new JButton("Start Simulation");
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                int run = Integer.parseInt(runField.getText());
                double carPassingTimeMean = Double.parseDouble(carPassingTimeMeanField.getText());
                model_configData.carPassingTime_mean = carPassingTimeMean;
                double eastMovingLambda = Double.parseDouble(eastMovingLambdaField.getText());
                model_configData.generator_eastMoving_lambda = 1/eastMovingLambda;
                double westMovingLambda = Double.parseDouble(westMovingLambdaField.getText());
                model_configData.generator_westMoving_lambda = 1/ westMovingLambda;

                double maxElapseTimeWhenBusyE2W = Double.parseDouble(maxElapseTimeWhenBusyFieldE2W.getText());
                model_configData.maxElapseTimeWhenBusy_E2W = maxElapseTimeWhenBusyE2W;
                double maxElapseTimeWhenBusyW2E = Double.parseDouble(maxElapseTimeWhenBusyFieldW2E.getText());
                model_configData.maxElapseTimeWhenBusy_W2E = maxElapseTimeWhenBusyW2E;
                String[] args = {String.valueOf(run)};
                test.main(args);
            }
        });
        btnStart.setBounds(162, 190, 150, 23);
        frame.getContentPane().add(btnStart);
    }
}