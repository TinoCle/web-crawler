package ar.edu.ubp.das.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ar.edu.ubp.das.beans.UserWebsitesBean;
import ar.edu.ubp.das.db.Dao;


public class MSWebsitesDao extends Dao<UserWebsitesBean, UserWebsitesBean> {
	
	@Override
	public UserWebsitesBean make(ResultSet result) throws SQLException {
		UserWebsitesBean website = new UserWebsitesBean();
		website.setUserId(result.getInt("user_id"));
		website.setWebsitesCSV(result.getString("websites"));
		website.setWebsitesIdCSV(result.getString("websites_id"));
		return website;
	}
	
	@Override
	public List<UserWebsitesBean> select() throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.get_websites");
			return this.executeQuery();
		} catch(SQLException e){
			System.out.println("Websites DAO Error: ");
			System.out.println(e);
		} 
		finally {
			this.close();
		}
		return null;
	}

	@Override
	public void insert(UserWebsitesBean web) throws SQLException {
	}
	
	@Override
	public void delete(Integer arg0) throws SQLException {
	}

	@Override
	public void delete(UserWebsitesBean arg0) throws SQLException {
	}

	@Override
	public void delete(UserWebsitesBean arg0, Integer arg1) throws SQLException {
	}

	@Override
	public UserWebsitesBean find(Integer arg0) throws SQLException {
		return null;
	}

	@Override
	public UserWebsitesBean find(UserWebsitesBean web) throws SQLException {
		return null;
	}

	@Override
	public void insert(UserWebsitesBean arg0, Integer arg1) throws SQLException {
	}

	@Override
	public List<UserWebsitesBean> select(Integer arg0) throws SQLException {
		return null;
	}

	@Override
	public List<UserWebsitesBean> select(UserWebsitesBean arg0) throws SQLException {
		return null;
	}

	@Override
	public void update(UserWebsitesBean arg0) throws SQLException {
	}

	@Override
	public void update(UserWebsitesBean arg0, Integer id) throws SQLException {
	}

	@Override
	public boolean valid(UserWebsitesBean arg0) throws SQLException {
		return false;
	}

	@Override
	public void delete(List<UserWebsitesBean> arg0) throws SQLException {
	}

	@Override
	public void update(Integer websiteId) throws SQLException {
	}

	@Override
	public void update(List<UserWebsitesBean> arg0) throws SQLException {
	}
}
