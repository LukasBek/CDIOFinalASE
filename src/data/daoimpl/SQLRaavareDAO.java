package data.daoimpl;

import java.sql.ResultSet;
import java.sql.SQLException;

import data.daointerface.DALException;
import data.daointerface.RaavareDAO;
import data.database.Connector;
import data.dto.RaavareDTO;

public class SQLRaavareDAO implements RaavareDAO{
	private Connector connector;
	
	public SQLRaavareDAO(){
		try {
			connector = new Connector();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public RaavareDTO getRaavare(int id) throws DALException {
		ResultSet rs = connector.doQuery("SELECT*FROM raavare WHERE raavare_id = " + id);
		try{
			if (!rs.first()) throw new DALException ("Raavarebatch " + id + " findes ikke");
			RaavareDTO rDTO = new RaavareDTO();
			rDTO.setrID(rs.getInt("raavare_id"));
			rDTO.setrName(rs.getString("raavare_navn"));
			rDTO.setDeliverer(rs.getString("leverandoer"));
			RaavareDTO result = rDTO;
			return result;
		}catch(SQLException e){
			throw new DALException(e);
		}
	}
}