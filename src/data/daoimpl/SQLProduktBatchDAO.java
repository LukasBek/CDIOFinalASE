package data.daoimpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import data.daointerface.DALException;
import data.daointerface.ProduktbatchDAO;
import data.database.Connector;
import data.dto.ProduktBatchDTO;
import data.dto.ProduktBatchKomponentDTO;

public class SQLProduktBatchDAO implements ProduktbatchDAO{

	private Connector connector;

	public SQLProduktBatchDAO() {
		try {
			connector = new Connector();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ProduktBatchDTO getProduktBatch(int pbId) throws DALException {
		ResultSet rs = connector.doQuery("SELECT * FROM produktbatch WHERE pb_id = " + pbId);
		try{
			if (!rs.first()) throw new DALException("Produktbatch " + pbId + " findes ikke");
			ProduktBatchDTO pbDTO = new ProduktBatchDTO();
			pbDTO.setPbId(rs.getInt("pb_id"));
			pbDTO.setStatus(rs.getInt("status"));
			pbDTO.setReceptId(rs.getInt("recept_id"));
			ProduktBatchDTO result = pbDTO;
			return result;
		} catch(SQLException e) {
			throw new DALException(e);
		}
	}

	@Override
	public void updateProduktBatch(ProduktBatchDTO pb) throws DALException {
		String query = "UPDATE produktbatch SET status = '" + pb.getStatus() + 
				"', recept_id = '" + pb.getReceptId() + "' WHERE pb_id = '" + pb.getPbId()+"'";
		connector.doUpdate(query);
	}

	@Override
	public void createProduktBatchKomponent(ProduktBatchKomponentDTO produktbatchkomponent) throws DALException {
		connector.doUpdate(
				"INSERT INTO produktbatchkomponent(pb_id, rb_id, tara, netto, opr_id) VALUES " +
						"(" + produktbatchkomponent.getPbId() + ", " + produktbatchkomponent.getRbId() + ", " + 
						produktbatchkomponent.getTara() + ", " + produktbatchkomponent.getNetto() + ", " + produktbatchkomponent.getOprId() + ")"
				);
	}

	@Override
	public List<Integer> getProduktBatchRaavareList(int batch) throws DALException {
		List<Integer> list = new ArrayList<Integer>();
		ResultSet rs = connector.doQuery(
				"SELECT raavare_id from raavare "
						+ "NATURAL JOIN receptkomponent "
						+ "NATURAL JOIN recept "
						+ "NATURAL JOIN produktbatch "
						+ "WHERE pb_id = " + batch
				);
		try{
			while (rs.next()) {
				list.add(rs.getInt("raavare_id"));
			}
			return list;
		}catch (SQLException e){
			throw new DALException(e);
		}
	}

	@Override
	public List<Integer> getProduktBatchDoneRaavare(int batch) throws DALException {
		List<Integer> list = new ArrayList<Integer>();
		ResultSet rs = connector.doQuery(
				"SELECT DISTINCT raavare_id from produktbatchkomponent "
						+ "NATURAL JOIN produktbatch "
						+ "NATURAL JOIN recept "
						+ "NATURAL JOIN receptkomponent "
						+ "NATURAL JOIN raavare "
						+ "NATURAL JOIN raavarebatch "
						+ "WHERE pb_id = " + batch
				);
		try{
			while (rs.next()) {
				list.add(rs.getInt("raavare_id"));
			}
			return list;
		}catch (SQLException e){
			throw new DALException(e);
		}
	}


}