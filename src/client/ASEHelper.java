package client;

import java.util.ArrayList;
import java.util.List;
import data.daoimpl.SQLProduktBatchDAO;
import data.daointerface.DALException;

public class ASEHelper {

	SQLProduktBatchDAO pbdao = new SQLProduktBatchDAO();

	public int getNextRaavare(int batchNumber){

		List<Integer> rvMax = new ArrayList<Integer>();
		List<Integer> rvDone = new ArrayList<Integer>();
		List<Integer> rvNeeded = new ArrayList<Integer>();
		try {		
			rvMax = pbdao.getProduktBatchRaavareList(batchNumber);
			rvDone = pbdao.getProduktBatchDoneRaavare(batchNumber);
		} catch (DALException e) {
			System.out.println("Error getting raavarelist");
		}

		for(int i = 0; i<rvMax.size(); i++){
			rvNeeded.add(0);
		}

		//rv = liste over raavare der skal måles
		for(int i = 0; i<rvDone.size(); i++){
			for(int j = 0; j<rvMax.size(); j++){
				if(rvDone.get(i) == rvMax.get(j)){
					rvNeeded.set(j, 1);
				}
			}
		}
		for(int i = 0; i<rvNeeded.size(); i++){
			if(rvNeeded.get(i) == 0){
				return rvMax.get(i);
			}
		}
		return -1;
	}
}
