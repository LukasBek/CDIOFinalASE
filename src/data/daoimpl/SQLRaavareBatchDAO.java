package data.daoimpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import data.daointerface.DALException;
import data.daointerface.RaavareBatchDAO;
import data.database.Connector;
import data.dto.RaavareBatchDTO;

public class SQLRaavareBatchDAO implements RaavareBatchDAO{
	private Connector connector;

	public SQLRaavareBatchDAO(){
		try {
			connector = new Connector();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public RaavareBatchDTO getRaavareBatch(int rbId) throws DALException {
		ResultSet rs = connector.doQuery("SELECT * FROM raavarebatch WHERE rb_Id = " + rbId);
		try{
			if (!rs.first()) throw new DALException("RaavareBatch " +rbId+ " findes ikke");
			RaavareBatchDTO rbDTO = new RaavareBatchDTO();
			rbDTO.setRbId(rbId);
			rbDTO.setRaavareId(rs.getInt("raavare_Id"));
			rbDTO.setMaengde(rs.getDouble("maengde"));
			return rbDTO;
		} catch(SQLException e) {
			throw new DALException(e);
		}
	}

	@Override
	public void updateRaavareBatch(RaavareBatchDTO rb) throws DALException {
		String query = "UPDATE raavarebatch SET maengde = '" + rb.getMaengde() + 
				"', raavare_Id = '" + rb.getRaavareId() + "' WHERE rb_id = '"+ rb.getRbId()+"'";
		connector.doUpdate(query);
	}
}