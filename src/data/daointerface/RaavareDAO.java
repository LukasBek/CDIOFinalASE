package data.daointerface;

import data.dto.RaavareDTO;

public interface RaavareDAO {
	RaavareDTO getRaavare(int id) throws DALException;
}
