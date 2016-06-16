package data.daoimpl;

import java.sql.ResultSet;
import java.sql.SQLException;

import data.daointerface.DALException;
import data.daointerface.ReceptDAO;
import data.database.Connector;
import data.dto.ReceptDTO;
import data.dto.ReceptKomponentDTO;


public class SQLReceptDAO implements ReceptDAO{
	private Connector connector;
	
	public SQLReceptDAO() {
		try {
			connector = new Connector();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public ReceptDTO getRecept(int receptId) throws DALException {
		ResultSet rs = connector.doQuery("SELECT * FROM recept WHERE recept_id = " + receptId);
		try{
			if (!rs.first()) throw new DALException("Recepten " + receptId + " findes ikke");
			ReceptDTO recDTO = new ReceptDTO();
			recDTO.setReceptId(rs.getInt("recept_id"));
			recDTO.setReceptName(rs.getString("recept_navn"));
			ReceptDTO result = recDTO;
			return result;
		} catch(SQLException e) {
			throw new DALException(e);
		}
	}

	@Override
	public ReceptKomponentDTO getReceptKomp(int receptId, int raavareId) throws DALException {
		ResultSet rs = connector.doQuery("SELECT * FROM receptkomponent WHERE recept_id = " + receptId + " AND raavare_id = " + raavareId);
	    try {
	    	if (!rs.first()) throw new DALException("Receptkomponent " + receptId + " findes ikke");
	    	return new ReceptKomponentDTO (rs.getInt("recept_id"), rs.getInt("raavare_id"), rs.getDouble("nom_netto"), rs.getDouble("tolerance"));
	    }
	    catch (SQLException e) {throw new DALException(e); }
	}
}