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
	int nextRaavare;
	double tara;
	String fromWeight;
	String toWeight;
	String fromUser;
	String error = "Kunne ikke modtage besked fra vaegten";

	SQLProduktBatchDAO pbdao = new SQLProduktBatchDAO();
	SQLReceptDAO receptdao = new SQLReceptDAO();
	SQLRaavareDAO raavaredao = new SQLRaavareDAO();
	SQLRaavareBatchDAO rbdao = new SQLRaavareBatchDAO();
	SQLOperatoerDAO odao = new SQLOperatoerDAO();

	RaavareMethod rm = new RaavareMethod();
	MettlerController mc;

	public ASEController(int portnumber){
		System.out.println("ASE1");
		mc = new MettlerController(portnumber);
	}

	public void run(){

		System.out.println("ASE2");
		int id;
		id = (int) mc.sendRM("Indtast ID");
		System.out.println("ASE3");
		try {
			toWeight = odao.getOperatoer(id).getOprNavn();
		} catch (DALException e1) {
			e1.printStackTrace();
		}
		mc.sendRM(toWeight + " Bekræft identitet ved at trykke ok");
		
		boolean batchCheck = true;
		
		while(batchCheck){
			try {
				batchNumber = (int) mc.sendRM("Indtast batchnummer");
			} catch (NumberFormatException nfe){
				continue;
			}

			nextRaavare = rm.getNextRaavare(batchNumber);
			if(nextRaavare == -1){
				System.out.println("Dette produktbatch er allerede færdigt eller eksisterer ikke");
				continue;
			}else{
				try {
					toWeight = "Recept: " + receptdao.getRecept(pbdao.getProduktBatch(batchNumber).getReceptId()).getReceptName() + " fortsæt med 'ok";
					mc.sendRM(toWeight);
					batchCheck = false;
				} catch (DALException e) {
					e.printStackTrace();
				}
			}
		}
		
		try {
		ProduktBatchDTO pb = new ProduktBatchDTO();
		pb = pbdao.getProduktBatch(batchNumber);
		pb.setStatus(1);
			pbdao.updateProduktBatch(pb);
		} catch (DALException e) {
			e.printStackTrace();
		}
		
		mc.sendRM("Tjek at vægten er ubelastet, fortsæt med 'ok'");
		mc.tara();
//		
//		tara = 
//		
//		try {
//			 = mc.sendRM("Indtast raavarebatchnummer for " + raavaredao.getRaavare(nextRaavare).getrName() + ", med id: " + nextRaavare);
//		} catch (DALException e) {
//			e.printStackTrace();
//		}
//		mc.sendWeight(tara);
//		mc.tara();
		
//		mc.sendRM("")
		
		

		System.out.println("Tjek at vaegten er ubelastet");
		boolean checked = false;



	}

}
