package ar.edu.ubp.das.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ar.edu.ubp.das.beans.UserWebsitesBean;
import ar.edu.ubp.das.db.Dao;


public class MSWebsitesDao extends Dao<UserWebsitesBean, UserWebsitesBean> {
	@Override
	public void insert(UserWebsitesBean web) throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.new_website_from_service (?,?,?)");
			this.setParameter(1, web.getUserId());
			this.setParameter(2, web.getUrl());
			this.setParameter(3, web.getServiceId());
			this.executeQuery();
		} catch(Exception e){
			System.out.println("Websites DAO Error (insert from service): ");
			System.out.println(e);
		} 
		finally {
			this.close();
		}
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
	public UserWebsitesBean make(ResultSet result) throws SQLException {
		UserWebsitesBean website = new UserWebsitesBean();
		website.setUserId(result.getInt("user_id"));
		website.setWebsitesCSV(result.getString("websites"));
		website.setWebsitesIdCSV(result.getString("websites_id"));
		return website;
	}

	@Override
	public void delete(Integer arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(UserWebsitesBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(UserWebsitesBean arg0, Integer arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UserWebsitesBean find(Integer arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserWebsitesBean find(UserWebsitesBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insert(UserWebsitesBean arg0, Integer arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<UserWebsitesBean> select(Integer arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserWebsitesBean> select(UserWebsitesBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(UserWebsitesBean arg0) throws SQLException {
		
	}

	@Override
	public void update(UserWebsitesBean arg0, Integer id) throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.set_website_indexed(?)");
			this.setParameter(1, id);
			if (this.executeUpdate() == 0) {
				// TODO: Log error
			}
		} finally {
			this.close();
		}
		
	}

	@Override
	public boolean valid(UserWebsitesBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void delete(List<UserWebsitesBean> arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Set down website
	 */
	@Override
	public void update(Integer websiteId) throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.set_website_down(?)");
			this.setParameter(1, websiteId);
			if (this.executeUpdate() == 0) {
				// TODO: Log
			}
		} finally {
			this.close();
		}
	}

	@Override
	public void update(List<UserWebsitesBean> arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}
