package BatallasMVC.modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

/**
 * Clase genérica para implementar un modelo para acceder a una base datos siguiendo el patrón de dsiseño MVC
 */
public interface Modelo {

	/**
	 * Intenta realizar la conexion a la BD con el par (username, password)
	 * 
	 * @param username
	 * @param password
	 * @return verdadero si pudo conectar.
	 */
	public boolean conectar(String username, String password) throws Exception;
	
	public void desconectar();
	
	/**
	  * Método encargado de realizar una consulta SQL del tipo SELECT y 
	  * devolver el resultado en un objeto ResultSet
	  * 
	  * @param sql
	  * @throws SQLException 
	  */
	public ResultSet consulta(String sql) throws SQLException;
	
	/**
	  * Método encargado de ejecutar una actualizacion en la B.D. con una 
	  * sentencia SQL del tipo UPDATE, INSERT, DELETE, ETC.
	  * 
	  * @param sql 
	  * @throws SQLException 
	  */
	public void actualizacion (String sql) throws SQLException;

	/**
	 * Permite setear la conexión a la base de datos con una conexión ya establecida
	 */
	public void setConexion(Connection conexion);
}
