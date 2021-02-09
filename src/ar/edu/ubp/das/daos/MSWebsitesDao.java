package ar.edu.ubp.das.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ar.edu.ubp.das.beans.WebsiteBean;
import ar.edu.ubp.das.db.Dao;


public class MSWebsitesDao extends Dao<WebsiteBean, WebsiteBean> {
	@Override
	public void insert(WebsiteBean web) throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.new_website_from_service (?,?,?)");
			this.setParameter(1, web.getUser_id());
			this.setParameter(2, web.getUrl());
			this.setParameter(3, web.getService_id());
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
	public List<WebsiteBean> select() throws SQLException {
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
	public WebsiteBean make(ResultSet result) throws SQLException {
		WebsiteBean website = new WebsiteBean();
		website.setUser_id(result.getInt("user_id"));
		website.setWebsites(result.getString("websites"));
		return website;
	}

	@Override
	public void delete(Integer arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(WebsiteBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(WebsiteBean arg0, Integer arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public WebsiteBean find(Integer arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WebsiteBean find(WebsiteBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insert(WebsiteBean arg0, Integer arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<WebsiteBean> select(Integer arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WebsiteBean> select(WebsiteBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(WebsiteBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(WebsiteBean arg0, Integer arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean valid(WebsiteBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
}
