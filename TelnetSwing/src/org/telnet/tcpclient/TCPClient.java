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

package org.telnet.tcpclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * TCPClient class for telnet swing sample
 * 
 * @author Emanuele Paiano
 * 
 * */
public class TCPClient {
	
	/**
	 * Remote host (hostname or ip address)
	 * */
	private String remoteAddress;
	
	/**
	 * Remote port (1-65535)
	 * */
	private int remotePort;
	
	/**
	 * TCP Socket
	 * */
	private Socket socket;
	
	/**
	 * TCP writing stream
	 * */
	private OutputStream out;
	
	
	/**
	 * TCP reading stream
	 * */
	private InputStream in;
	
	
	/**
	 * Constructor
	 * @param host remote hostname or IP Address
	 * @param port remote port (1-65535)
	 * */
	public TCPClient(String host, int port) {
		setRemoteHost(host);
		setRemotePort(port);
	}
	
	/**
	 * Constructor. Client will be initialized with
	 * default settings
	 * 
	 * */
	public TCPClient() {
		setRemotePort(Settings.DefaultPort);
		setRemoteHost(Settings.DefaultHost);
	}

	/**
	 * @return remote hostname or ip address
	 * */
	public String getRemoteHost() {
		return remoteAddress;
	}

	/**
	 * @return set remote host or ip
	 * */
	public void setRemoteHost(String host) {
		this.remoteAddress = host;
	}

	/**
	 * @return remote port
	 * */
	public int getRemotePort() {
		return remotePort;
	}

	
	/**
	 * set remote port
	 * @param remoteport (1-65535)
	 * */
	public void setRemotePort(int remotePort) {
		if(remotePort>65535)
			this.remotePort=65535;
		else if(this.remotePort<1)
			this.remotePort=1;
		else
			this.remotePort = remotePort;
	}

	
	/**
	 * connect to remote host
	 * @return true if success, false otherwise
	 * */
    public boolean connect() {
    	try {
			socket=new Socket(remoteAddress, remotePort);
			in=socket.getInputStream();
			out=socket.getOutputStream();
			Thread.sleep(500);
		} catch (IOException | InterruptedException e) {
			System.out.println("Error while connecting: "+e.getMessage());
		} 
    	
		return socket.isConnected();
    }
    
    /**
	 * connect to remote host
	 * @param host remote host
	 * @param port remote port
	 * @return true if success, false otherwise
	 * */
    public boolean connect(String host, int port) {
    	this.setRemoteHost(host);
    	this.setRemotePort(port);
    	return this.connect();
    }
    
    
    /**
	 * close connection
	 * @return true if success, false otherwise
	 * */
    public boolean close() {
    	try {
    		out.close();
    		in.close();
			socket.close();
		} catch (IOException e) {
			System.out.println("Error while closing socket: "+e.getMessage());
		}
    	return socket.isClosed();
    }

    
    /**
   	 * connection status
   	 * @return true if connected, false otherwise
   	 * */
	public boolean isConnected() {
		if (socket!=null)
			return !socket.isClosed();
		return false;
	}

	
	/**
   	 * send command string. 
   	 * @return true if success, false otherwise
   	 * */
	public boolean send(String data) throws IOException {
		if (isConnected()) {
			for(int i=0;i<data.length();i++)	
			out.write(data.charAt(i));
			return true;
		}
		return false;
	}
	
	/**
   	 * receive single char from input stream
   	 * @return true if success, false otherwise
   	 * */
	public char receiveByte() throws IOException {
		return (char)in.read();
	}
	
	
	/**
   	 * receive single line from input stream
   	 * @return true if success, false otherwise
   	 * */
	public String readLine() throws IOException {
		String buffer="";
		char ch=' ';
		while(ch!='\n') {
			ch=(char) in.read();
			buffer+=ch;
		}
		return buffer;
	}
	
	
	/**
   	 * @return true if input buffer is not empty, false otherwise
   	 * */
	public boolean incomingData() {
		try {
			if (in!=null) 
				return in.available()>0;
		} catch (IOException e) {
		}
		return false;
	}
	
}
