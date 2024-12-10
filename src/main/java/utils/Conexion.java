package utils;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// Clase con métodos estáticos para manejar una conexión con la base de datos
public class Conexion {

	private static Logger logger = LoggerFactory.getLogger(Conexion.class);	
	
    private static String url;    
    private static String driverName;   
    private static Connection con;
    private static String urlstring;
	private static String templateUrlString;  // url template para reemplazar el nombre de la base de datos. Mantiene los mismmos valores que urlstring pero con un placeholder para la base de datos

    /**
     * Inicializa los parámetros de conexión con los valores definidos en el archivo de propiedades pasado como parámetro
     * 
     * @param property Archivo de propiedades con la ruta (ver archivos de propiedades carpeta \BatallasMVC\cfg\) 
     */
	public static void inicializar(String propertyFile)	throws Exception
	{
		logger.debug("Recuperación de los datos para la conexión con la BD");
		
		Properties prop = new Properties();
		try
		{
			logger.debug("Se intenta leer el archivo de propiedades {}", propertyFile);
			
			FileInputStream file=new FileInputStream(propertyFile);			
			prop.load(file);
			
						logger.debug("se cargó exitosamente");

			Conexion.setDriverName(prop.getProperty("driverName"));
			Conexion.setUrl(prop.getProperty("libreria", "jdbc") + ":" +
					  		prop.getProperty("motor", "mysql") + "://" +
					  		prop.getProperty("servidor", "localhost") + ":" +
					  		prop.getProperty("puerto"));
			
			Conexion.setUrlstring(Conexion.getUrl() + "/" + prop.getProperty("base_de_datos") + prop.getProperty("parametro_aux1"));

			Conexion.templateUrlString = Conexion.getUrl() + "/" + "_BASE_DE_DATOS_" + prop.getProperty("parametro_aux1");

			logger.debug("Parámetros de conexión: {}", Conexion.getUrl() + "/" + 
			              prop.getProperty("base_de_datos") + prop.getProperty("parametro_aux1"));
		}
		catch(Exception ex)
		{
        	logger.error("Se produjo un error al recuperar el archivo de propiedades de la BD.");  
        	throw ex;
		}
		return;
	}

	/**
	 * Crea una conexión con la base de datos a través del usuario y password pasados por parámetro y 
	 * utilizando la propiedad url seteada por el método inicializar(...)
	 * @param usuario
	 * @param password
	 * @return
	 */	 
	public static Connection getConnection(String usuario, String password) throws Exception {
		try { 	
				Class.forName(Conexion.getDriverName());
	            try {
	            	
	            	logger.debug("Parametros de conexion: url= {}, user={}, pass={}", Conexion.getUrlstring(), usuario, password);
	            	
	                con = DriverManager.getConnection(Conexion.getUrlstring(), 
	                								  usuario, 
	                								  password);
	                
	            	logger.info("Se establece la conexión con la BD");	            	
	                
	            } catch (SQLException ex) {	            	
	            	logger.error("Error al crear la conexión con la base de datos."); 
	            	logger.debug("SQLException: {}",ex.getMessage());
	            	logger.debug("SQLState: {}", ex.getSQLState());
	            	logger.debug("VendorError: {}", ex.getErrorCode());
	            	throw new Exception("No es posible conectar con el servidor de la Base de Datos");
	            }
		} catch (ClassNotFoundException ex) {
			logger.error("Driver not found.");		
			throw new Exception("No es posible conectar con el servidor de la Base de Datos");			  
		}
	    return con;
    }

	public static Connection getConnection(String database, String usuario, String password) {
		try { 	
				Class.forName(Conexion.getDriverName());
	            try {
	            	
					// reemplada el nombre de la base de datos en la url templateUrlString
					String urlString = templateUrlString.replace("_BASE_DE_DATOS_", database);

	            	logger.debug("Parametros de conexion: url= {}, user={}, pass={}", urlString, usuario, password);
	            	
	                con = DriverManager.getConnection(urlString, 
	                								  usuario, 
	                								  password);
	                
	            	logger.info("Se establece la conexión con la BD");	            	
	                
	            } catch (SQLException ex) {	            	
	            	logger.error("Error al crear la conexión con la base de datos."); 
	            	logger.debug("SQLException: {}",ex.getMessage());
	            	logger.debug("SQLState: {}", ex.getSQLState());
	            	logger.debug("VendorError: {}", ex.getErrorCode());
	            }
		} catch (ClassNotFoundException ex) {
			logger.error("Driver not found."); 
		}
	    return con;
    }

	public static Connection getConnection() throws SQLException {
		// Retorna la con si ya fue creada
		if (con != null) {
			return con;
		}
		throw new SQLException("No se ha establecido la conexión con la base de datos.");
	}

	/**
	 * Cierra una conexión	  
	 * @param conn
	 */
    public static void closeConnection(Connection conn) {
        try {
            if (null != conn) {
                conn.close();
                conn = null;
            }
        } catch (SQLException ex) {
        	logger.error("Error al cerrar la conexión con la base de datos."); 
        	logger.debug("SQLException: {}",ex.getMessage());
        	logger.debug("SQLState: {}", ex.getSQLState());
        	logger.debug("VendorError: {}", ex.getErrorCode());
        }
    }

    /**
     * Cierra un ResulSet (objeto devuelto por una consulta)   
     * @param rs
     */
    public static void closeResultset(ResultSet rs) throws SQLException{
        try {
            if (null != rs) {
                rs.close();
                rs = null;
            }
        } catch (SQLException ex) {
        	logger.error("Error al cerrar el resultSet."); 
        	logger.debug("SQLException: {}",ex.getMessage());
        	logger.debug("SQLState: {}", ex.getSQLState());
        	logger.debug("VendorError: {}", ex.getErrorCode());
        	throw ex;
        }
    }

    /**
     * Cierra una sentencia preparada
     * @param pstmt
     */
    public static void closePreparedStatement(PreparedStatement pstmt) throws SQLException{
        try {
            if (null != pstmt) {
                pstmt.close();
                pstmt = null;
            }
        } catch (SQLException ex) {
        	logger.error("Error al cerrar la consulta preparada."); 
        	logger.debug("SQLException: {}",ex.getMessage());
        	logger.debug("SQLState: {}", ex.getSQLState());
        	logger.debug("VendorError: {}", ex.getErrorCode());
        	throw ex;
        	
        }
    }

     
     /**
      * Cierra una sentencia 
      * @param stmt
      */
    public static void closeStatement(Statement stmt) throws SQLException {
        try {
            if (null != stmt) {
                stmt.close();
                stmt = null;
            }
        } catch (SQLException ex) {
        	logger.error("Error al cerrar la sentencia."); 
        	logger.debug("SQLException: {}",ex.getMessage());
        	logger.debug("SQLState: {}", ex.getSQLState());
        	logger.debug("VendorError: {}", ex.getErrorCode());
        	throw ex;
        }
    }	
	
    
    /*
	 *  Setters y Getters para setear y recuperar las propiedades de la conexión
	 */
	public static String getUrl() {
		return url;
	}

	public static void setUrl(String url) {
		Conexion.url = url;
	}

	public static String getDriverName() {
		return driverName;
	}

	public static void setDriverName(String driverName) {
		Conexion.driverName = driverName;
	}

	public static String getUrlstring() {
		return urlstring;
	}

	public static void setUrlstring(String urlstring) {
		Conexion.urlstring = urlstring;
	}

}
