package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MettlerController {

	String fromWeight;
	String error = "Kunne ikke modtage besked fra vaegten";
	double returnValue;

	Socket clientSocket = null;		
	BufferedReader inFromUser = null;		
	PrintWriter outToServer = null;		
	BufferedReader inFromServer = null;

	public MettlerController(int portnumber){
		try{		
			clientSocket = new Socket("localhost", portnumber);
			inFromUser = new BufferedReader( new InputStreamReader(System.in));
			inFromServer = new BufferedReader(new InputStreamReader (clientSocket.getInputStream()));		
			outToServer = new PrintWriter(clientSocket.getOutputStream());		
		}catch(UnknownHostException e1) {		
			System.out.println("Kunne ikke oprette forbindelse til vaegten");		
		}catch(IOException e1) {		
			System.out.println("Kunne ikke oprette forbindelse til vaegten");		
		}
	}

	public double sendRM(String message){
		System.out.println("MC1");
		returnValue = 0;
		outToServer.println("RM " + message);
		outToServer.flush();
		System.out.println("MC2");
		try {
			fromWeight = inFromServer.readLine();
			System.out.println("MC3");
			System.out.println(fromWeight);
			fromWeight = inFromServer.readLine();
			System.out.println(fromWeight);
			returnValue = Double.parseDouble(fromWeight.substring(6));
		} catch (IOException e1) {
			return -1;
		} catch (NullPointerException | NumberFormatException e2){
			return -2;
		}
		return returnValue;
	}

	public String displayMessage(String message){
		outToServer.println("D " +message);
		try {
			fromWeight = inFromServer.readLine();
		} catch (IOException e) {
			System.out.println("Fejl ved indstilling af display");
		}
		if(!(fromWeight.equals("DB"))){
			return "Kunne ikke opdaterer vægt display";
		}
		return null;
	}

	public double tara(){
		outToServer.println("T");
		try {
			fromWeight = inFromServer.readLine();
			System.out.println(fromWeight);
			returnValue = Double.parseDouble(fromWeight.substring(5));		
		} catch (IOException e) {
			return -1;
		}
		return returnValue;
	}

	public void sendWeight(double weight){
		outToServer.println(weight);
		try {
			fromWeight = inFromServer.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String meassure(){
		outToServer.println("");
		try {
			fromWeight = inFromServer.readLine();
			System.out.println(fromWeight);
		} catch (IOException e) {
			return error;
		}
		return fromWeight;
	}

	public void shutdown(){
		outToServer.println("Q");
	}

}
