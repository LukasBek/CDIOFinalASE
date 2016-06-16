package data.daointerface;

import data.dto.RaavareBatchDTO;

public interface RaavareBatchDAO {
	
	RaavareBatchDTO getRaavareBatch(int rbId) throws DALException;
	void updateRaavareBatch(RaavareBatchDTO rb) throws DALException;

}
