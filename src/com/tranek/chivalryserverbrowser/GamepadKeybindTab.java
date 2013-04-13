package com.tranek.chivalryserverbrowser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GamepadKeybindTab extends JPanel {
	private final MainWindow mw;
	
	public GamepadKeybindTab(MainWindow MW) {
		mw = MW;
		setLayout(null);
		
		JButton btnLT = new JButton("LT");
		btnLT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GamepadKeybindDialog gkd = new GamepadKeybindDialog(mw, mw.frmChivalryServers, "Left Trigger", Keybinds.XBOXTYPES_LEFT_TRIGGER);
				gkd.showDialog();
			}
		});
		btnLT.setBounds(124, 19, 97, 25);
		add(btnLT);
		
		JButton btnLB = new JButton("LB");
		btnLB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GamepadKeybindDialog gkd = new GamepadKeybindDialog(mw, mw.frmChivalryServers, "Left Shoulder", Keybinds.XBOXTYPES_LEFT_SHOULDER);
				gkd.showDialog();
			}
		});
		btnLB.setBounds(306, 45, 97, 25);
		add(btnLB);
		
		JButton btnRB = new JButton("RB");
		btnRB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GamepadKeybindDialog gkd = new GamepadKeybindDialog(mw, mw.frmChivalryServers, "Right Shoulder", Keybinds.XBOXTYPES_RIGHT_SHOULDER);
				gkd.showDialog();
			}
		});
		btnRB.setBounds(613, 47, 97, 25);
		add(btnRB);
		
		JButton btnRT = new JButton("RT");
		btnRT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GamepadKeybindDialog gkd = new GamepadKeybindDialog(mw, mw.frmChivalryServers, "Right Trigger", Keybinds.XBOXTYPES_RIGHT_TRIGGER);
				gkd.showDialog();
			}
		});
		btnRT.setBounds(757, 22, 97, 25);
		add(btnRT);
		
		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GamepadKeybindDialog gkd = new GamepadKeybindDialog(mw, mw.frmChivalryServers, "Back", Keybinds.XBOXTYPES_BACK);
				gkd.showDialog();
			}
		});
		btnBack.setBounds(400, 80, 97, 25);
		add(btnBack);
		
		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GamepadKeybindDialog gkd = new GamepadKeybindDialog(mw, mw.frmChivalryServers, "Start", Keybinds.XBOXTYPES_START);
				gkd.showDialog();
			}
		});
		btnStart.setBounds(509, 80, 97, 25);
		add(btnStart);
		
		JButton btnY = new JButton("Y");
		btnY.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GamepadKeybindDialog gkd = new GamepadKeybindDialog(mw, mw.frmChivalryServers, "Y", Keybinds.XBOXTYPES_Y);
				gkd.showDialog();
			}
		});
		btnY.setBounds(764, 82, 97, 25);
		add(btnY);
		
		JButton btnX = new JButton("X");
		btnX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GamepadKeybindDialog gkd = new GamepadKeybindDialog(mw, mw.frmChivalryServers, "X", Keybinds.XBOXTYPES_X);
				gkd.showDialog();
			}
		});
		btnX.setBounds(773, 130, 97, 25);
		add(btnX);
		
		JButton btnB = new JButton("B");
		btnB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GamepadKeybindDialog gkd = new GamepadKeybindDialog(mw, mw.frmChivalryServers, "B", Keybinds.XBOXTYPES_B);
				gkd.showDialog();
			}
		});
		btnB.setBounds(739, 185, 97, 25);
		add(btnB);
		
		JButton btnA = new JButton("A");
		btnA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GamepadKeybindDialog gkd = new GamepadKeybindDialog(mw, mw.frmChivalryServers, "A", Keybinds.XBOXTYPES_A);
				gkd.showDialog();
			}
		});
		btnA.setBounds(752, 249, 97, 25);
		add(btnA);
		
		JButton btnRightStick = new JButton("Right Stick");
		btnRightStick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GamepadKeybindDialog gkd = new GamepadKeybindDialog(mw, mw.frmChivalryServers, "Right Stick", Keybinds.XBOXTYPES_RIGHT_THUMBSTICK);
				gkd.showDialog();
			}
		});
		btnRightStick.setBounds(776, 316, 131, 25);
		add(btnRightStick);
		
		JButton btnRightAxis = new JButton("Right Axis (Look Sensitivity)");
		btnRightAxis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GamepadSensitivityDialog gsd = new GamepadSensitivityDialog(mw, mw.frmChivalryServers);
				gsd.showDialog();
			}
		});
		btnRightAxis.setBounds(738, 458, 236, 25);
		add(btnRightAxis);
		
		JButton btnDRight = new JButton("D Right");
		btnDRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GamepadKeybindDialog gkd = new GamepadKeybindDialog(mw, mw.frmChivalryServers, "D Right", Keybinds.XBOXTYPES_DPAD_RIGHT);
				gkd.showDialog();
			}
		});
		btnDRight.setBounds(461, 453, 97, 25);
		add(btnDRight);
		
		JButton btnDDown = new JButton("D Down");
		btnDDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GamepadKeybindDialog gkd = new GamepadKeybindDialog(mw, mw.frmChivalryServers, "D Down", Keybinds.XBOXTYPES_DPAD_DOWN);
				gkd.showDialog();
			}
		});
		btnDDown.setBounds(333, 473, 97, 25);
		add(btnDDown);
		
		JButton btnDLeft = new JButton("D Left");
		btnDLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GamepadKeybindDialog gkd = new GamepadKeybindDialog(mw, mw.frmChivalryServers, "D Left", Keybinds.XBOXTYPES_DPAD_LEFT);
				gkd.showDialog();
			}
		});
		btnDLeft.setBounds(70, 502, 97, 25);
		add(btnDLeft);
		
		JButton btnDUp = new JButton("D Up");
		btnDUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GamepadKeybindDialog gkd = new GamepadKeybindDialog(mw, mw.frmChivalryServers, "D Up", Keybinds.XBOXTYPES_DPAD_UP);
				gkd.showDialog();
			}
		});
		btnDUp.setBounds(73, 328, 97, 25);
		add(btnDUp);
		
		JButton btnLeftStick = new JButton("Left Stick");
		btnLeftStick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GamepadKeybindDialog gkd = new GamepadKeybindDialog(mw, mw.frmChivalryServers, "Left Stick", Keybinds.XBOXTYPES_LEFT_THUMBSTICK);
				gkd.showDialog();
			}
		});
		btnLeftStick.setBounds(88, 138, 97, 25);
		add(btnLeftStick);
		
		JButton btnLeftAxis = new JButton("Left Axis");
		btnLeftAxis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*GamepadKeybindDialog gkd = new GamepadKeybindDialog(mw, mw.frmChivalryServers, "Right Stick", Keybinds.XBOXTYPES_RIGHT_THUMBSTICK);
				gkd.showDialog();*/
			}
		});
		btnLeftAxis.setBounds(90, 223, 97, 25);
		btnLeftAxis.setEnabled(false);
		add(btnLeftAxis);
	}
}
