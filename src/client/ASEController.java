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
import data.dto.ProduktBatchKomponentDTO;
import data.dto.RaavareBatchDTO;

public class ASEController {

	int portdst = 8000;
	int portnumber;
	int pbBatch;
	int rvBatch;
	int nextRaavare;
	int receptID;
	double tara;
	double netto;
	double brutto;
	double raavareTol;
	double raavareNom;
	String fromWeight;
	String toWeight;
	String fromUser;
	String raavareName;
	String receptName;
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

		try{
			System.out.println("ASE2");
			int id;
			id = (int) mc.sendRM("Indtast ID");
			System.out.println("ASE3");

			toWeight = odao.getOperatoer(id).getOprNavn();

			mc.sendRM(toWeight + " Bekræft identitet ved at trykke ok");

			boolean batchCheck = true;

			while(batchCheck){
				try {
					pbBatch = (int) mc.sendRM("Indtast batchnummer");
				} catch (NumberFormatException nfe){
					continue;
				}

				nextRaavare = rm.getNextRaavare(pbBatch);
				if(nextRaavare == -1){
					System.out.println("Dette produktbatch er allerede færdigt eller eksisterer ikke");
					continue;
				}else{
					receptName = receptdao.getRecept(pbdao.getProduktBatch(pbBatch).getReceptId()).getReceptName();
					toWeight = "Recept: " + receptName + " fortsæt med 'ok";
					mc.sendRM(toWeight);
					batchCheck = false;
				}
			}

			ProduktBatchDTO pb = new ProduktBatchDTO();
			pb = pbdao.getProduktBatch(pbBatch);
			pb.setStatus(1);
			pbdao.updateProduktBatch(pb);

			mc.sendRM("Tjek at vægten er ubelastet, fortsæt med 'ok'");
			mc.tara();

			tara = mc.sendRM("Sæt en tarabeholder på vægten");
			mc.sendWeight(tara);
			mc.tara();

			raavareName = raavaredao.getRaavare(nextRaavare).getrName();
			rvBatch = (int)mc.sendRM("Indtast raavarebatchnummer for " + raavareName + ", med id: " + nextRaavare);

			boolean correctRVBatch = false;
			while(!correctRVBatch){
				if(rbdao.getRaavareBatch(rvBatch).getRaavareId() != nextRaavare){
					rvBatch = (int)mc.sendRM("Forkert raavarebatch, indtast nyt");
				}else {
					correctRVBatch = true;
				}
			}

			receptID = pbdao.getProduktBatch(pbBatch).getReceptId();
			raavareNom = receptdao.getReceptKomp(receptID, nextRaavare).getNom_netto();
			raavareTol = receptdao.getReceptKomp(receptID, nextRaavare).getTolerance();
			netto = mc.sendRM("Sæt "+ raavareNom + "kg " + raavareName + " på vægten. Må kun have en tolerance på " + raavareTol);

			boolean bruttoInput = true;
			boolean bruttoCheck = true;
			while(bruttoInput){
				if(netto < 0){
					netto = mc.sendRM("Ikke et korrekt input. Sæt "+ raavareNom + "kg " + raavareName + " på vægten. Må kun have en tolerance på " + raavareTol);
				}else{
					mc.sendWeight(netto);
					netto = mc.meassure();
					while(bruttoCheck){
						if (netto <= raavareNom+raavareTol && netto >= raavareNom-raavareTol){

							ProduktBatchKomponentDTO pbkDTO = new ProduktBatchKomponentDTO();
							pbkDTO.setPbId(pbBatch);
							pbkDTO.setRbId(rvBatch);
							pbkDTO.setOprId(id);
							pbkDTO.setTara(tara);
							pbkDTO.setNetto(brutto-tara);
							pbdao.createProduktBatchKomponent(pbkDTO);

							System.out.println("Tilføjet pbkomponent");
							double amount;
							RaavareBatchDTO rbDTO = new RaavareBatchDTO();
							rbDTO = rbdao.getRaavareBatch(rvBatch);
							amount = rbDTO.getMaengde();
							rbDTO.setMaengde(amount-brutto);
							rbdao.updateRaavareBatch(rbDTO);
							System.out.println("fratrukket raavarekomponent");
							bruttoInput = false;
							bruttoCheck = false;
							
							mc.sendWeight(0 - (netto + tara));
							mc.tara();
						}else{
							brutto = mc.sendRM("Udenfor tolerance. Sæt "+ raavareNom + "kg " + raavareName + " på vægten. Må kun have en tolerance på " + raavareTol);
						}
					}
				}
			}



		}catch(DALException e){
			e.printStackTrace();
		}


		System.out.println("Tjek at vaegten er ubelastet");
		boolean checked = false;



	}

}
