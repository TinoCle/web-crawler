package ar.edu.ubp.das.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ar.edu.ubp.das.beans.ServiceBean;
import ar.edu.ubp.das.db.Dao;

public class MSServicesDao extends Dao<ServiceBean, ServiceBean> {
	@Override
	public List<ServiceBean> select() throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.get_services_to_crawl");
			return this.executeQuery();
		} catch(SQLException e){
			System.out.println("Services DAO Error: ");
			System.out.println(e);
		} 
		finally {
			this.close();
		}
		return null;
	}

	@Override
	public ServiceBean make(ResultSet result) throws SQLException {
		ServiceBean service = new ServiceBean();
		service.setUser_id(result.getInt("user_id"));
		service.setService_id(result.getInt("service_id"));
		service.setProtocol(result.getString("protocol"));
		service.setURLResource(result.getString("url_resource"));
		service.setURLPing(result.getString("url_ping"));
		return service;
	}
	
	@Override
	public void update(Integer id) throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.reindex_service(?,?)");
			this.setParameter(1, id);
			this.setParameter(2, false);
			if (this.executeUpdate() == 0) {
				// TODO: Log
			}
		} finally {
			this.close();
		}
		
	}
	
	@Override
	//utilizado para setear servicios como caídos
	public void update(ServiceBean service) throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.set_service_down(?)");
			this.setParameter(1, service.getService_id());
			int affectedRows = this.executeUpdate();
			if (affectedRows == 0) {
				// Medio imposible, pero no está de más el chequeo
				throw new SQLException("El servicio a actualizar no existe");
			}
		} finally {
			this.close();
		}
	}
	
	@Override
	public void delete(ServiceBean service) throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.clean_service_pages(?,?)");
			this.setParameter(1, service.getService_id());
			this.setParameter(2, service.getUser_id());
			this.executeQuery();
		} finally {
			this.close();
		}
		
	}

	@Override
	public void delete(Integer arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(ServiceBean arg0, Integer arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServiceBean find(Integer arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceBean find(ServiceBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insert(ServiceBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insert(ServiceBean arg0, Integer arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ServiceBean> select(Integer arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ServiceBean> select(ServiceBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(ServiceBean arg0, Integer arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean valid(ServiceBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void delete(List<ServiceBean> arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(List<ServiceBean> arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

}
