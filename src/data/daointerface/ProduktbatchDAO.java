package data.daointerface;

import java.util.List;

import data.dto.ProduktBatchDTO;
import data.daointerface.DALException;
import data.dto.ProduktBatchKomponentDTO;

public interface ProduktbatchDAO {
	
	ProduktBatchDTO getProduktBatch(int pbId) throws DALException;
	void updateProduktBatch(ProduktBatchDTO pb) throws DALException;
	List<Integer> getProduktBatchRaavareList(int id) throws DALException;
	
	// Methods for acces to the produktbatchkomponent table
	List<Integer> getProduktBatchDoneRaavare(int batch) throws DALException;
	void createProduktBatchKomponent(ProduktBatchKomponentDTO produktbatchkomponent) throws DALException;
}
