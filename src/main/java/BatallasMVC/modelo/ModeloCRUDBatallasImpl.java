package BatallasMVC.modelo;

import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import utils.Conexion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ModeloCRUDBatallasImpl extends ModeloImpl implements ModeloCRUDBatallas {

	private static Logger logger = LoggerFactory.getLogger(ModeloCRUDBatallasImpl.class);
	
	public ModeloCRUDBatallasImpl() throws Exception {
		// inicializa la conexi�n con el servidor utilizando el m�todo est�tico de la clase Conexion
		// que setea el driver (jdbc), la ubicaci�n del servidor y puerto (url) y la bases de datos, 
		// recuperando estos datos de un archivo de propiedades  
		try
		{
			Conexion.inicializar("cfg/conexionBD.properties");	
		}
		catch (Exception ex) {
			logger.error("Error al recuperar el archivo de propiedades de la BD y no se pudo establecer la conexion");
			throw new Exception("No se pudo conectar al servidor. Error al recuperar el archivo de configuraci�n de la B.D.");
		}
		
		//recupera del archivo de propiedades el usuario y password que se utilizar� para acceder al servidor 
		String usuario;
		String password;
		Properties prop = new Properties();
		try
		{
			logger.debug("Se intenta leer el archivo de propiedades con los datos del usuario cfg/usuarios.properties");
			
			FileInputStream file=new FileInputStream("cfg/usuarios.properties");			
			prop.load(file);

			logger.debug("se carg� exitosamente");

			usuario= prop.getProperty("username");
			password= prop.getProperty("password");

			logger.debug("se recupero el usuario: " + usuario + " y password: "+ password);			
		
			//Establece una conexi�n al servidor con el usuario y password recuperado del archivo de propiedades
			//utilizando el m�todo conectar heredado de ModeloImpl
			this.conectar(usuario, password);
			logger.debug("se conect� exitosamente al servidor");
		}
		catch(Exception ex)
		{
			logger.error("Se produjo un error al recuperar el archivo de propiedades con los datos del usuario y no se puedo establecer la conexi�n");
			throw new Exception("No se pudo conectar al servidor. Error al recuperar el usuario y password del archivo de configuraci�n de usuarios");
		}
	}
	
	  // Utilizando la conexi�n ya establecida con la base de datos recupera todas las tuplas 
	  // de la tabla batallas y las devuelve como una lista de objetos BatallaBean  
	  public ArrayList<BatallaBean> recuperarTablaBatallas() throws Exception{
	   try{
		    ArrayList<BatallaBean> lista = new ArrayList<BatallaBean>() ;
			ResultSet rs = this.consulta("SELECT nombre_batalla, fecha FROM batallas");
			while (rs.next()) {
				BatallaBean batalla= new BatallaBeanImpl(); 	
				batalla.setNombre(rs.getString("nombre_batalla"));
				batalla.setFecha(rs.getDate("fecha"));
				lista.add(batalla);
			}
			return lista; 
	   }
	   catch (SQLException ex) {
		   logger.error("SQLException: " + ex.getMessage());
		   logger.error("SQLState: " + ex.getSQLState());
		   logger.error("VendorError: " + ex.getErrorCode());		   
		   throw new Exception("Error en la conexi�n con la BD.");
	   }
	}
	
	  // Inserta una nueva Batalla en la tabla Batallas	  
	  public void crearBatalla(BatallaBean batalla) throws Exception{		
		// crea un objeto de acceso a datos (Data Access Object o DAO) de tipo DaoBatallas para
		// insertar una nueva Batallas en la base de datos. Le pasa como parametro la conexi�n
		// con la B.D. ya establecida por el modelo. 
		DAOBatalla daoBatalla = new DAOBatallaImpl();
		daoBatalla.setConexion(this.conexion);
		
		// El metodo insertarBatalla(...) de la clase DAOBatallas se encarga de insertar la batalla 
		// en la tabla Batallas de la B.D.  
		daoBatalla.insertarBatalla(batalla);
	  }
	  
	 // Elimina una batalla en la tabla Batallas  
	  public void eliminarBatalla(BatallaBean batalla) throws Exception{
		DAOBatalla daoBatalla = new DAOBatallaImpl();
		daoBatalla.setConexion(this.conexion);
		daoBatalla.eliminarBatalla(batalla);
	  }

	  // Recupera la/s batalla/s que coinciden con el nombre y la fecha del par�metro batalla 
	  public  ArrayList<BatallaBean> buscarBatallas(BatallaBean batalla) throws Exception{
		DAOBatalla daoBatalla = new DAOBatallaImpl();
		daoBatalla.setConexion(this.conexion);
		return daoBatalla.recuperarBatallas(batalla);		  
	  }

	  // Actualiza los atributos de una batalla 
	  public void actualizarBatalla(BatallaBean batallaSeleccionada, BatallaBean batallaModificada)throws Exception{
		DAOBatalla daoBatalla = new DAOBatallaImpl();
		daoBatalla.setConexion(this.conexion);
		daoBatalla.actualizarBatalla(batallaSeleccionada, batallaModificada);		  
	  }
	  
}
