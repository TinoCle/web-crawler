package ar.edu.ubp.das.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ar.edu.ubp.das.beans.WebsiteBean;
import ar.edu.ubp.das.db.Dao;

public class MSWebsiteDao extends Dao<WebsiteBean, WebsiteBean>{

	@Override
	public void delete(Integer arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(List<WebsiteBean> arg0) throws SQLException {
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
	public void insert(WebsiteBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insert(WebsiteBean arg0, Integer arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public WebsiteBean make(ResultSet arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WebsiteBean> select() throws SQLException {
		// TODO Auto-generated method stub
		return null;
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
	public void update(Integer websiteId) throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.set_website_down(?)");
			this.setParameter(1, websiteId);
			if (this.executeUpdate() == 0) {
				// TODO: Log Error
				System.out.println("ERROR");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 
		finally {
			this.close();
		}
	}

	@Override
	public void update(WebsiteBean website) throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.set_website_indexed(?)");
			this.setParameter(1, website.getWebsiteId());
			if (this.executeUpdate() == 0) {
				// TODO: Log Error
			}
		} finally {
			this.close();
		}
	}
	
	@Override
	public void update(List<WebsiteBean> arg0) throws SQLException {
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
