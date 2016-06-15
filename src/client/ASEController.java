package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import data.daoimpl.SQLOperatoerDAO;
import data.daoimpl.SQLProduktBatchDAO;
import data.daoimpl.SQLRaavareBatchDAO;
import data.daoimpl.SQLRaavareDAO;
import data.daoimpl.SQLReceptDAO;
import data.daointerface.DALException;
import data.dto.ProduktBatchDTO;

public class ASEController {

	int portdst = 8000;
	int portnumber;
	int batchNumber;
	String fromWeight;
	String toWeight;
	String fromUser;
	String error = "Kunne ikke modtage besked fra vaegten";

	private Socket sock;
	private BufferedReader instream;
	private ServerSocket listener;
	private DataOutputStream outstream;

	Socket clientSocket = null;		
	BufferedReader inFromUser = null;		
	PrintWriter outToServer = null;		
	BufferedReader inFromServer = null;

	SQLProduktBatchDAO pbdao = new SQLProduktBatchDAO();
	SQLReceptDAO receptdao = new SQLReceptDAO();
	SQLRaavareDAO raavaredao = new SQLRaavareDAO();
	SQLRaavareBatchDAO rbdao = new SQLRaavareBatchDAO();
	SQLOperatoerDAO odao = new SQLOperatoerDAO();

	RaavareMethod rm = new RaavareMethod();
	MettlerController mc;

	public ASEController(int portnumber){
		mc = new MettlerController(portnumber);
	}

	public void run(){

		int id;
		id = (int) mc.sendRM("Indtast ID");
		try {
			toWeight = odao.getOperatoer(id).getOprNavn();
		} catch (DALException e1) {
			e1.printStackTrace();
		}
		mc.sendRM(toWeight + "Bekræft identitet ved at trykke ok");
		
		
		
		boolean batchCheck = true;
		int nextRaavare;
		while(batchCheck){
			try {
				System.out.print("Batchnummer: ");
				toWeight = inFromUser.readLine();
				batchNumber = Integer.parseInt(toWeight);
			} catch (IOException ioe) {
				System.out.println("Fejl i læsning, 1");
				ioe.printStackTrace();
				continue;
			} catch (NumberFormatException nfe){
				System.out.println("Indtastede er ikke et batchnummer");
				continue;
			}

			nextRaavare = rm.getNextRaavare(batchNumber);
			if(nextRaavare == -1){
				System.out.println("Dette produktbatch er allerede færdigt eller eksisterer ikke");
				continue;
			}else{
				try {
					toWeight = "Recept der skal produceres: " + receptdao.getRecept(pbdao.getProduktBatch(batchNumber).getReceptId()).getReceptName();
				} catch (DALException e) {
					e.printStackTrace();
				}
				mc.displayMessage(toWeight);
			}
		}

		System.out.println("Tjek at vaegten er ubelastet");
		boolean checked = false;



	}

}
