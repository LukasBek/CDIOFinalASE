package data.daointerface;

import data.dto.OperatoerDTO;

public interface OperatoerDAO {
	OperatoerDTO getOperatoer(int oprId) throws DALException;
}
