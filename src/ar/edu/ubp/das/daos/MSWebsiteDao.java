package ar.edu.ubp.das.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ar.edu.ubp.das.beans.WebsiteBean;
import ar.edu.ubp.das.db.Dao;

public class MSWebsiteDao extends Dao<WebsiteBean, WebsiteBean> {

	@Override
	public WebsiteBean make(ResultSet result) throws SQLException {
		WebsiteBean website = new WebsiteBean();
		website.setWebsiteId(result.getInt(""));
		return null;
	}
	
	@Override
	public WebsiteBean find(WebsiteBean web) throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.get_service_website_indexed(?,?)");
			this.setParameter(1, web.getUrl());
			this.setParameter(2, web.getServiceId());
			List<WebsiteBean> user = this.executeQuery();
			if (user.size() > 0) {
				return user.get(0);
			}
			return null;
		} finally {
			this.close();
		}
	}

	@Override
	public void insert(WebsiteBean web) throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.new_website_from_service (?,?,?)");
			this.setParameter(1, web.getUserId());
			this.setParameter(2, web.getUrl());
			this.setParameter(3, web.getServiceId());
			this.executeUpdate();
		} finally {
			this.close();
		}
	}

	// Update status
	@Override
	public void update(Integer websiteId) throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.set_website_indexed(?,?)");
			this.setParameter(1, websiteId);
			this.setOutParameter(2, java.sql.Types.INTEGER);
			this.executeUpdate();
			if (this.getIntParam(2) == 0) {
				// TODO: Log Error
				throw new SQLException("No se pudo actualizar a la pagina");
			}
		} finally {
			this.close();
		}
	}

	@Override
	public void update(WebsiteBean website) throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.set_website_down(?,?)");
			this.setParameter(1, website.getWebsiteId());
			this.setOutParameter(2, java.sql.Types.INTEGER);
			this.executeUpdate();
			if (this.getIntParam(2) == 0) {
				// TODO: Log Error
				throw new SQLException("Error al actualizar pagina");
			}
		} finally {
			this.close();
		}
	}

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
	public void insert(WebsiteBean arg0, Integer arg1) throws SQLException {
		// TODO Auto-generated method stub

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
