package BatallasMVC.modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.Fechas;


public class DAOBatallaImpl implements DAOBatalla{
	
private static Logger logger = LoggerFactory.getLogger(DAOBatallaImpl.class);
	
	private Connection conexion;
	
	//El constructor setea la conexión a través de la cual DAOBatallas interactua con la base de datos
	//public DAOBatallaImpl(Connection c) {
	//	this.conexion = c;
	//}

	public DAOBatallaImpl() {
	}


	@Override
	public void setConexion(Connection conexion) {
		this.conexion = conexion;
	}

	@Override
	public void insertarBatalla(BatallaBean batalla) throws Exception
	{
		// crea el string para la sentencia preparada
		String sql= "INSERT INTO batallas(nombre_batalla, fecha) values (?,?)";

		logger.debug("SQL: INSERT INTO batallas(nombre_batalla, fecha) values ({},{})", batalla.getNombre(), Fechas.convertirDateADateSQL(batalla.getFecha()));
		
		try 
		{  PreparedStatement insert = conexion.prepareStatement(sql); 
		   insert.setString(1, batalla.getNombre());
		   insert.setDate(2, Fechas.convertirDateADateSQL(batalla.getFecha()));
		   insert.executeUpdate();
		}
		catch (SQLException ex)
		{
			logger.error("SQLException: {}", ex.getMessage());
			logger.error("SQLState: {}", ex.getSQLState());
			logger.error("VendorError: {}", ex.getErrorCode());
			//System.out.println(ex.getSQLState());
			if (ex.getErrorCode()==1062) // Duplicate entry for PRIMARY KEY
				throw new Exception("Ya existe una batalla con nombre '"+ batalla.getNombre() + "'");
			else 
				throw new Exception("Error inesperado al insertar la batalla en la B.D.");
		}		
			
	}
	
	@Override
	public void eliminarBatalla(BatallaBean batalla) throws Exception
	{
		// crea el string para la sentencia preparada, para borrar solo es necesario conocer el nombre_batalla que es la llave  
		String sql= "DELETE FROM batallas WHERE nombre_batalla=?";
		
		logger.debug("SQL: DELETE FROM batallas WHERE nombre_batalla={}", batalla.getNombre());
		
		try 
		{  PreparedStatement delete = conexion.prepareStatement(sql); 
		   delete.setString(1, batalla.getNombre());
		   delete.executeUpdate();
		   logger.debug("Se eliminó la batalla con nombre {}", batalla.getNombre());
		}
		catch (SQLException ex)
		{		
			logger.error("SQLException: {}", ex.getMessage());
			logger.error("SQLState: {}", ex.getSQLState());
			logger.error("VendorError: {}", ex.getErrorCode());
		    
			if (ex.getErrorCode()==1451) // Cannot delete or update a parent row: a foreign key constraint fails
				// Ojo: Esta excepcion no va a producirse si en la definicion de la tabla resultados  
				// está  FOREIGN KEY (nombre_batalla) REFERENCES batallas (nombre_batalla) ON DELETE CASCADE 
				throw new Exception("No se puede eliminar la batalla por que esta referenciada en otra tabla");
			else 
				throw new Exception("Error inesperado al borrar la batalla en la B.D.");			
		}
	}

	@Override
	public  ArrayList<BatallaBean> recuperarBatallas(BatallaBean batalla) throws Exception{
		// Crea el string para la sentencia, para buscar las batallas. 
		// Nota: por seguridad debería utilizarse siempre sentencias preparadas para evitar 
		// ataques del tipo "SQL injection". 
		
		String sql= "SELECT nombre_batalla, fecha FROM batallas WHERE 1=1";

		if ((batalla.getNombre() != null) && !batalla.getNombre().isEmpty()) {
			sql = sql + " AND nombre_batalla LIKE ?";
		}
	
		if (batalla.getFecha() != null) {
			sql = sql + " AND fecha = ?";
		}

		logger.debug("SQL: {}", sql);
		
		ArrayList<BatallaBean> lista = new ArrayList<BatallaBean>() ;
		try{
			PreparedStatement select = conexion.prepareStatement(sql);

			int paramIndex = 1; // Se usa una variable porque no se conoce de antemano cuantos parametros se van a agregar
			if ((batalla.getNombre() != null) && !batalla.getNombre().isEmpty()) {
				String patron = "%" + batalla.getNombre() + "%";
				logger.debug("patron: {}", patron);
				select.setString(paramIndex++, patron);
			}
	
			if (batalla.getFecha() != null) {
				select.setString(paramIndex++, Fechas.convertirDateAStringDB(batalla.getFecha()));
			}
	
			 ResultSet rs= select.executeQuery();
			
			 while (rs.next()) {
				logger.debug("Se recuperó el item con nombre {} y fecha {}", rs.getString("nombre_batalla"), rs.getDate("fecha"));

				BatallaBean b = new BatallaBeanImpl();
				b.setNombre(rs.getString("nombre_batalla"));
				b.setFecha(rs.getDate("fecha"));

				lista.add(b);
			}
			return lista;
		}
		catch (SQLException ex)
		{			
			logger.error("SQLException: {}", ex.getMessage());
			logger.error("SQLState: {}", ex.getSQLState());
			logger.error("VendorError: {}", ex.getErrorCode());

			throw new Exception("Error al recuperar las batallas", ex);
		}
	}

	@Override
	public void actualizarBatalla(BatallaBean batallaSeleccionada, BatallaBean batallaModificada) throws Exception {	 
		// crea el string para la sentencia preparada, para borrar solo es necesario conocer el nombre_batalla que es la llave  
		 String sql= "UPDATE batallas SET nombre_batalla= ?, fecha=? WHERE nombre_batalla=?";
		 
		 logger.debug("SQL: UPDATE batallas SET nombre_batalla= {}, fecha={} WHERE nombre_batalla={}", batallaModificada.getNombre(), Fechas.convertirDateADateSQL(batallaModificada.getFecha()), batallaSeleccionada.getNombre());
		 
		 try 
		 {  PreparedStatement update = conexion.prepareStatement(sql);
		    update.setString(1, batallaModificada.getNombre());
		    update.setDate(2, Fechas.convertirDateADateSQL(batallaModificada.getFecha()));
		 	update.setString(3, batallaSeleccionada.getNombre());
		 	update.executeUpdate();
		 }
		 catch (SQLException ex)
		 {
			logger.error("SQLException: {}", ex.getMessage());
			logger.error("SQLState: {}", ex.getSQLState());
			logger.error("VendorError: {}", ex.getErrorCode());

			if (ex.getErrorCode()==1451) // Cannot delete or update a parent row: a foreign key constraint fails
				throw new Exception("No se puede actualizar la batalla por que esta referenciada en otra tabla");
			else 
				throw new Exception("Error inesperado al borrar la batalla en la B.D.");
		 }
	 }
}
