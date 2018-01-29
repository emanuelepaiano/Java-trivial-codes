/**
 * Copyright 2017 Emanuele Paiano
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * */
package org.telnet.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.telnet.tcpclient.Settings;
import org.telnet.tcpclient.TCPClient;

import javax.swing.JButton;
import java.io.IOException;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;



/**
 * Generated with Eclipse WindowBuilder
 * 
 * */
public class Main extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField hostTxt;
	private JTextField portTxt;
	private JTextField commandTxt;
	private JButton sendBtn;
	private JButton connectBtn;
	
	private volatile TCPClient client=new TCPClient();
	private JScrollPane scrollPane;
	private JTextArea receiveTxt;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		setTitle("Java Telnet ");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 604, 413);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		sendBtn = new JButton(Labels.SendBtn);
		sendBtn.setEnabled(false);
		sendBtn.addActionListener((e)->{
			if(client.isConnected())
				try {
					client.send(commandTxt.getText()+Settings.EndLine);				 
				}catch(IOException e1) {
					System.out.println("Error while sending: "+e1.getMessage());
					pushConnectBtn();
				}
			});
		sendBtn.setBounds(495, 351, 95, 23);
		contentPane.add(sendBtn);
		
		hostTxt = new JTextField();
		hostTxt.setBounds(53, 12, 234, 23);
		contentPane.add(hostTxt);
		hostTxt.setColumns(10);
		
		portTxt = new JTextField();
		portTxt.setColumns(10);
		portTxt.setBounds(341, 12, 78, 23);
		contentPane.add(portTxt);
		
		JLabel lblHost = new JLabel(Labels.HostLbl);
		lblHost.setBounds(12, 16, 70, 15);
		contentPane.add(lblHost);
		
		JLabel lblPort = new JLabel(Labels.PortLbl);
		lblPort.setBounds(305, 16, 44, 15);
		contentPane.add(lblPort);
		
		commandTxt = new JTextField();
		commandTxt.setEditable(false);
		commandTxt.setColumns(10);
		commandTxt.setBounds(12, 351, 471, 23);
		contentPane.add(commandTxt);
		
		connectBtn = new JButton(Labels.ConnectBtn);
		connectBtn.addActionListener((e) -> pushConnectBtn());
		
		connectBtn.setBounds(457, 11, 133, 23);
		contentPane.add(connectBtn);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 47, 578, 292);
		contentPane.add(scrollPane);
		
		receiveTxt = new JTextArea();
		receiveTxt.setEditable(false);
		scrollPane.setViewportView(receiveTxt);
		
		doReceive();
	}
	
	
	/**
	 * Auto-called on pushing connect button
	 * 
	 * */
	private void pushConnectBtn() {
		
		if(!client.isConnected()) 
			openConnection();
		else 
			 closeConnection();
		 
		 if(client.isConnected()) {
			 connectBtn.setText(Labels.DisconnectBtn);
			 sendBtn.setEnabled(true);
			 hostTxt.setEnabled(false);
			 portTxt.setEnabled(false);
			 commandTxt.setEnabled(true);
			 commandTxt.setEditable(true);
		 }
			 
		 else {
			connectBtn.setText(Labels.ConnectBtn);
		 	sendBtn.setEnabled(false);
		 	hostTxt.setEnabled(true);
		 	portTxt.setEnabled(true);
		 	receiveTxt.setEditable(false);
		 	receiveTxt.setText("");
		 	commandTxt.setEnabled(false);
		 	commandTxt.setEditable(false);
		 }
	}
	
	/**
	 * open
	 * */
	private boolean openConnection() {
		String tmp=portTxt.getText();
		int port=tmp.equals("") ? 0: Integer.valueOf(tmp);
		return client.connect(hostTxt.getText(), port);
	}
	
	
	/**
	 * close connections
	 * */
	private boolean closeConnection() {
		return client.close();
	}
	
	
	
	/**
	 * Client incoming data polling thread 
	 * */
	private void doReceive() {
		(new Thread(()->{
					String buffer="";
					while(true) {
						if (client.isConnected()) { 
							if (client.incomingData()) 
								try {
									buffer+=client.receiveByte();
									receiveTxt.setText(buffer);
								} catch (IOException e) {
									this.closeConnection();
									this.pushConnectBtn();
								}
							}
						else
							buffer="";
					}
						
		})).start();
		
	}
}
