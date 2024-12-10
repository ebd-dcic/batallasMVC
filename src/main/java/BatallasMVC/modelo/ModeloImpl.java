package BatallasMVC.modelo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.Conexion;

/**
 * Clase gen�rica para implementar un modelo para acceder a una base datos siguiendo el patr�n de dise�o MVC
 */
public class ModeloImpl implements Modelo {
	
	private static Logger logger = LoggerFactory.getLogger(ModeloImpl.class);	

	//Propiedad que mantienen la conexion con el servidor, a partir de la cual el modelo recupera datos de la B.D.    
	protected Connection conexion = null;
 
	//Establece una conexi�n a la base de datos con un usuario y password utilizando el 
    //M�todo est�tico getConection(...) de la clase Conexion
	public boolean conectar(String username, String password) throws Exception {
        this.conexion = Conexion.getConnection(username, password);        
    	return (this.conexion != null);	
	}

	//Cierra la conexi�n con la base de datos
	public void desconectar() {		
		logger.info("Se desconecta de BD.");
		Conexion.closeConnection(this.conexion);		
	}

	//Realiza una consulta de tipo SELECT en el string sql pasado como par�metro a trav�s de 
	//la propiedad conexion y retorna un objeto de tipo ResulSet
	public ResultSet consulta(String sql) throws SQLException 
	{
		logger.info("Se intenta realizar la siguiente consulta {}",sql);
		Statement stmt = conexion.createStatement();			
		ResultSet rs = stmt.executeQuery(sql);
			
		return rs;
	}	
	

	//Realiza la consulta de actualizaci�n (UPDATE, INSERT, DELETE) con el string sql pasado como 
	//par�metro a trav�s de la propiedad conexion 
	public void actualizacion (String sql) throws SQLException 
	{
		Statement stmt = this.conexion.createStatement();
		stmt.executeUpdate(sql);
			
		stmt.close();
	}


	//Setea la conexi�n a la base de datos con una conexi�n ya establecida
	public void setConexion(Connection conexion) {
		this.conexion = conexion;
	}
}
