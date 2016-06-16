package client;

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
	int id;
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

	ASEHelper rm = new ASEHelper();
	MettlerController mc;

	public ASEController(int portnumber){
		mc = new MettlerController(portnumber);
	}

	public void run(){

		try{
			try{
				id = (int) mc.sendRM("Indtast ID");
				if(id < 1){
					wrongLogin();
				}
			}catch(ClassCastException e){
				wrongLogin();
			}

			toWeight = odao.getOperatoer(id).getOprNavn();
			mc.sendRM(toWeight + " Bekræft identitet ved at trykke ok");

			boolean checkpbBatch = true;
			try{		
				pbBatch = (int) mc.sendRM("Indtast batchnummer");
				if (pbBatch < 1){
					checkpbBatch = false;
				}
			}catch(ClassCastException e){
				checkpbBatch = false;
			}
			boolean batchCheck = true;
			while(batchCheck){
				while(!(checkpbBatch)){
					try {
						pbBatch = (int) mc.sendRM("Produkbatchen er enten færdig, ikke eksisterende, eller du har lavet en forkert indtastning. Prøv igen");
						if(pbBatch < 1){
							continue;
						}else{
							checkpbBatch = true;
						}
					} catch (ClassCastException cce){
						continue;
					}
				}

				nextRaavare = rm.getNextRaavare(pbBatch);
				if(nextRaavare == -1){
					checkpbBatch = false;
					continue;
				}else{
					receptName = receptdao.getRecept(pbdao.getProduktBatch(pbBatch).getReceptId()).getReceptName();
					toWeight = "Recept: " + receptName + " fortsæt med 'ok'";
					mc.sendRM(toWeight);
					batchCheck = false;
				}
			}

			boolean produktbatchLoop = true;
			while(produktbatchLoop){
				nextRaavare = rm.getNextRaavare(pbBatch);
				if(nextRaavare == -1){
					ProduktBatchDTO pb = new ProduktBatchDTO();
					pb = pbdao.getProduktBatch(pbBatch);
					pb.setStatus(2);
					pbdao.updateProduktBatch(pb);
					break;
				}

				ProduktBatchDTO pb = new ProduktBatchDTO();
				pb = pbdao.getProduktBatch(pbBatch);
				pb.setStatus(1);
				pbdao.updateProduktBatch(pb);
				mc.sendRM("Tjek at vægten er ubelastet, fortsæt med 'ok'");
				mc.tara();
				boolean checkTara = false;
				tara = mc.sendRM("Sæt en tarabeholder på vægten");
				if (tara < 0){
					checkTara = true;
				}
				while(checkTara){
					tara = mc.sendRM("Sæt venligst en rigtig tarabeholder på vægten");
					if(tara > 0){
						checkTara = false;
					}
				}
				mc.sendWeight(tara);
				mc.tara();

				raavareName = raavaredao.getRaavare(nextRaavare).getrName();

				boolean checkrvBatch = false;
				try{
					rvBatch = (int)mc.sendRM("Indtast raavarebatchnummer for " + raavareName + ", med id: " + nextRaavare);
					if(rvBatch < 1 ){	
						checkrvBatch = true;
					}else if(rbdao.getRaavareBatch(rvBatch).getRaavareId() != nextRaavare){
						checkrvBatch = true;
					}
				}catch(ClassCastException e){
					checkrvBatch = true;
				}
				while(checkrvBatch){
					try{
						rvBatch = (int)mc.sendRM("Venligst indtast et korrekt raavarebatchnummer for " + raavareName + ", med id: " + nextRaavare);
						if(rvBatch > 0 ){
							try{
								if(rbdao.getRaavareBatch(rvBatch).getRaavareId() == nextRaavare){
									checkrvBatch = false;
								}
							}catch(DALException e){
							}
						}
					}catch(ClassCastException e){

					}
				}

				receptID = pbdao.getProduktBatch(pbBatch).getReceptId();
				raavareNom = receptdao.getReceptKomp(receptID, nextRaavare).getNom_netto();
				raavareTol = receptdao.getReceptKomp(receptID, nextRaavare).getTolerance();

				netto = mc.sendRM("Sæt "+ raavareNom + "kg " + raavareName + " på vægten. Må kun have en tolerance på " + raavareTol);

				boolean bruttoInput = true;
				while(bruttoInput){
					if(netto < 0){
						mc.sendWeight(0 - (netto));
						netto = mc.sendRM("Forkert input. Sæt "+ raavareNom + "kg " + raavareName + " på vægten. Må kun have en tolerance på " + raavareTol);
						continue;
					}else{
						mc.sendWeight(netto);
						netto = mc.meassure();

						if (netto <= raavareNom+raavareTol && netto >= raavareNom-raavareTol){
							ProduktBatchKomponentDTO pbkDTO = new ProduktBatchKomponentDTO();
							pbkDTO.setPbId(pbBatch);
							pbkDTO.setRbId(rvBatch);
							pbkDTO.setOprId(id);
							pbkDTO.setTara(tara);
							pbkDTO.setNetto(netto);
							pbdao.createProduktBatchKomponent(pbkDTO);

							System.out.println("Tilføjet pbkomponent");
							double amount;
							RaavareBatchDTO rbDTO = new RaavareBatchDTO();
							rbDTO = rbdao.getRaavareBatch(rvBatch);
							amount = rbDTO.getMaengde();
							rbDTO.setMaengde(amount-netto);
							rbdao.updateRaavareBatch(rbDTO);
							System.out.println("fratrukket raavarekomponent");
							bruttoInput = false;

							mc.sendWeight(0 - (netto + tara));
							mc.tara();
							mc.sendRM("Produktafvejning registreret!");
						}else{
							mc.sendWeight(0 - (netto));
							netto = mc.sendRM("Forkert input. Sæt "+ raavareNom + "kg " + raavareName + " på vægten. Må kun have en tolerance på " + raavareTol);
						}
					}
				}
			}
		}catch(DALException e){
			e.printStackTrace();
		}
	}

	private void wrongLogin() {
		boolean loggedIn = false;
		while(!loggedIn){
			try{
				id = (int) mc.sendRM("Forkert indtastning, prøv igen");
				if(id < 1){
					continue;
				}
			}catch(ClassCastException e2){
				continue;
			}
			loggedIn = true;
		}
	}
}
