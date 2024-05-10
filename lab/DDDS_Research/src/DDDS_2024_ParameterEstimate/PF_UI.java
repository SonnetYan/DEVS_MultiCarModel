package DDDS_2024_ParameterEstimate;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class PF_UI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("PF UI");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JButton runButton = new JButton("Run PF");
        runButton.setBounds(100, 300, 200, 30);
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PF pf = new PF(0); // replace 0 with the run number you want
                pf.startComputing(0); // replace 0 with the run number you want
            }
        });
        frame.add(runButton);

        JTextField maxElapseTimeWhenBusy_E2W_TextField = new JTextField(String.valueOf(model_configData.pf_maxElapseTimeWhenBusy_E2W));
        maxElapseTimeWhenBusy_E2W_TextField.setBounds(220, 100, 200, 25);
        maxElapseTimeWhenBusy_E2W_TextField.setEnabled(!PF_configData.lightSwitchTime_E2W_Flag&& !PF_configData.oneLightSwitchTimeFlag);
        frame.add(maxElapseTimeWhenBusy_E2W_TextField);

        JTextField maxElapseTimeWhenBusy_W2E_TextField = new JTextField(String.valueOf(model_configData.pf_maxElapseTimeWhenBusy_W2E));
        maxElapseTimeWhenBusy_W2E_TextField.setBounds(220, 140, 200, 25);
        maxElapseTimeWhenBusy_W2E_TextField.setEnabled(!PF_configData.lightSwitchTime_W2E_Flag && !PF_configData.oneLightSwitchTimeFlag);
        frame.add(maxElapseTimeWhenBusy_W2E_TextField);

        JTextField carPassingTime_mean_TextField = new JTextField(String.valueOf(model_configData.pf_carPassingTime_mean));
        carPassingTime_mean_TextField.setBounds(220, 180, 200, 25);
        carPassingTime_mean_TextField.setEnabled(!PF_configData.carPassingTimeFlag);
        frame.add(carPassingTime_mean_TextField);

        JTextField oneTrafficLightTime_TextField = new JTextField(String.valueOf(model_configData.pf_maxElapseTimeWhenBusy_oneDirection));
        oneTrafficLightTime_TextField.setBounds(220, 60, 200, 25);
        oneTrafficLightTime_TextField.setEnabled(!PF_configData.estimate_oneLightSwitchTimeFlag);
        frame.add(oneTrafficLightTime_TextField);

        JLabel lblEastMovingLambda = new JLabel("East Moving Lambda:     1/");
        lblEastMovingLambda.setBounds(10, 220, 200, 25);
        frame.add(lblEastMovingLambda);

        JTextField eastMovingLambdaField = new JTextField(String.valueOf(1/model_configData.generator_eastMoving_lambda));
        eastMovingLambdaField.setBounds(220, 220, 200, 25);
        frame.add(eastMovingLambdaField);

        JLabel lblWestMovingLambda = new JLabel("West Moving Lambda:     1/");
        lblWestMovingLambda.setBounds(10, 260, 200, 25);
        frame.add(lblWestMovingLambda);

        JTextField westMovingLambdaField = new JTextField(String.valueOf(1/model_configData.generator_westMoving_lambda));
        westMovingLambdaField.setBounds(220, 260, 200, 25);
        frame.add(westMovingLambdaField);

        JCheckBox lightSwitchTime_E2W_Flag_CheckBox = new JCheckBox("PE lightSwitchTime_E2W", PF_configData.lightSwitchTime_E2W_Flag);
        lightSwitchTime_E2W_Flag_CheckBox.setBounds(10, 100, 200, 25);
        lightSwitchTime_E2W_Flag_CheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                PF_configData.lightSwitchTime_E2W_Flag = e.getStateChange() == ItemEvent.SELECTED;
                maxElapseTimeWhenBusy_E2W_TextField.setEnabled(!PF_configData.lightSwitchTime_E2W_Flag);
            }
        });
        frame.add(lightSwitchTime_E2W_Flag_CheckBox);

        JCheckBox lightSwitchTime_W2E_Flag_CheckBox = new JCheckBox("PE lightSwitchTime_W2E", PF_configData.lightSwitchTime_W2E_Flag);
        lightSwitchTime_W2E_Flag_CheckBox.setBounds(10, 140, 200, 25);
        lightSwitchTime_W2E_Flag_CheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                PF_configData.lightSwitchTime_W2E_Flag = e.getStateChange() == ItemEvent.SELECTED;
                maxElapseTimeWhenBusy_W2E_TextField.setEnabled(!PF_configData.lightSwitchTime_W2E_Flag);
            }
        });
        frame.add(lightSwitchTime_W2E_Flag_CheckBox);

        JCheckBox oneLightSwitchTime_checkBox = new JCheckBox("Treat as oneLightSwitchTime", PF_configData.oneLightSwitchTimeFlag);
        oneLightSwitchTime_checkBox.setBounds(10, 20, 200, 25);
        oneLightSwitchTime_checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                PF_configData.oneLightSwitchTimeFlag = e.getStateChange() == ItemEvent.SELECTED;
                lightSwitchTime_E2W_Flag_CheckBox.setEnabled(!PF_configData.oneLightSwitchTimeFlag);
                lightSwitchTime_W2E_Flag_CheckBox.setEnabled(!PF_configData.oneLightSwitchTimeFlag);
                maxElapseTimeWhenBusy_E2W_TextField.setEnabled(!PF_configData.oneLightSwitchTimeFlag);
                maxElapseTimeWhenBusy_W2E_TextField.setEnabled(!PF_configData.oneLightSwitchTimeFlag);
            }
        });
        frame.add(oneLightSwitchTime_checkBox);

        JCheckBox carPassingTimeFlag_CheckBox = new JCheckBox("PE carPassingTime", PF_configData.carPassingTimeFlag);
        carPassingTimeFlag_CheckBox.setBounds(10, 180, 200, 25);
        carPassingTimeFlag_CheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                PF_configData.carPassingTimeFlag = e.getStateChange() == ItemEvent.SELECTED;
                carPassingTime_mean_TextField.setEnabled(!PF_configData.carPassingTimeFlag);
            }
        });
        frame.add(carPassingTimeFlag_CheckBox);

        JCheckBox estimateOneLightSwitchTimeFlag_CheckBox = new JCheckBox("PE OneLightSwitchTime", PF_configData.estimate_oneLightSwitchTimeFlag);
        estimateOneLightSwitchTimeFlag_CheckBox.setBounds(10, 60, 200, 25);
        estimateOneLightSwitchTimeFlag_CheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                PF_configData.estimate_oneLightSwitchTimeFlag = e.getStateChange() == ItemEvent.SELECTED;
                oneTrafficLightTime_TextField.setEnabled(!PF_configData.estimate_oneLightSwitchTimeFlag);
                if(PF_configData.estimate_oneLightSwitchTimeFlag) {
                    oneLightSwitchTime_checkBox.setSelected(true);
                }
            }
        });
        frame.add(estimateOneLightSwitchTimeFlag_CheckBox);

        JCheckBox generateParticleStepCheckBox = new JCheckBox("Generate Particle Step", PF_configData.generateParticleStep);
        generateParticleStepCheckBox.setBounds(10, 350, 200, 25);
        generateParticleStepCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                PF_configData.generateParticleStep = e.getStateChange() == ItemEvent.SELECTED;
            }
        });
        frame.add(generateParticleStepCheckBox);
        

        frame.setVisible(true);


    }





}