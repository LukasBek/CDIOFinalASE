package data.daointerface;

import data.daointerface.DALException;
import data.dto.ReceptKomponentDTO;
import data.dto.ReceptDTO;

public interface ReceptDAO {
	ReceptDTO getRecept(int receptId) throws DALException;
	
	//Methods for "receptkomponent" table access
		ReceptKomponentDTO getReceptKomp(int receptId, int raavareId) throws DALException;
	
}
