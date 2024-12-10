package BatallasMVC.modelo.integration;

import BatallasMVC.modelo.*;
import utils.Conexion;
import utils.DatabaseSetup;
import utils.Fechas;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.*;
import java.util.List;

@Tag("integration")
public class DAOBatallaImplIntegrationTest {

    private static Connection connection;

    private static DAOBatallaImpl daoBatalla;

    private static Logger logger = LoggerFactory.getLogger(DAOBatallaImplIntegrationTest.class);

    @BeforeAll
    public static void setUpOnce() throws Exception {
        // Configura la conexión a la base de datos MySQL 
        // Lo realiza una única vez para todas las pruebas de integración en este archivo
        //
        if (connection == null || connection.isClosed()) {
            DatabaseSetup.reset();
            DatabaseSetup.setUp();
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        // Para cada test se conecta con el usuario prueba/pwprueba
        //
        connection = Conexion.getConnection("prueba", "pwprueba");

        // Carga los datos de prueba
        DatabaseSetup.loadData();

        // Crea un objeto DAOBatallaImpl
        daoBatalla = new DAOBatallaImpl();
        daoBatalla.setConexion(connection);
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Limpia los datos después de cada prueba si es necesario
        if (connection != null && !connection.isClosed()) {
            DatabaseSetup.cleanDatabase();
        }
    }

    @AfterAll
    public static void tearDownOnce() throws Exception {
    }

    @Test
    void testConexion() throws Exception {
        // Prueba que la conexión a la base de datos esté disponible
        assertNotNull(connection);
        assertFalse(connection.isClosed());
    }

    @Test
    void testInsertarBatalla() throws Exception {
        // Prueba que se inserte una batalla en la base de datos
        //

        BatallaBean batalla = new BatallaBeanImpl();
        // INSERT INTO batallas VALUES("Midway","1942/06/04");
        batalla.setNombre("Midway");
        batalla.setFecha(Date.valueOf("1942-06-04"));

        // Inserta la batalla
        daoBatalla.insertarBatalla(batalla);

        // Verifica que la batalla fue insertada
        //
        String sql = "SELECT * FROM batallas WHERE nombre_batalla = 'Midway'";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        rs.next();
        assertEquals("Midway", rs.getString("nombre_batalla"));
        assertEquals(Date.valueOf("1942-06-04"), rs.getDate("fecha"));
    }

    @Test
    void testInsertarBatalla_Duplicada() throws Exception {
        // Prueba que no se inserte una batalla duplicada en la base de datos
        //

        BatallaBean batalla = new BatallaBeanImpl();
        batalla.setNombre("Guadalcanal");
        batalla.setFecha(Date.valueOf("1942-01-01"));

        // Inserta la batalla
        Exception exception = assertThrows(Exception.class, () -> {
            daoBatalla.insertarBatalla(batalla);
        });

        assertEquals("Ya existe una batalla con nombre '"+ batalla.getNombre() + "'", exception.getMessage());

        // Verifica que la batalla no fue insertada
        //
        String sql = "SELECT * FROM batallas WHERE nombre_batalla = 'Guadalcanal' AND fecha = '1942-01-01'";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        // Verifica que el RS es vacio
        assertFalse(rs.next());
    }

    @Test
    void testEliminarBatalla() throws Exception {
        // Prueba que se elimine una batalla de la base de datos
        //
        // Insertamos una batalla para eliminarla después ya que no deberia tener resultados
		String sql= "INSERT INTO batallas(nombre_batalla, fecha) values (?,?)";
		try
		{  
            PreparedStatement insert = connection.prepareStatement(sql); 
            insert.setString(1, "Midway");
            insert.setDate(2, Date.valueOf("1942-06-04"));
            insert.executeUpdate();
		}
		catch (SQLException ex)
		{
			logger.error("SQLException: {}", ex.getMessage());
			logger.error("SQLState: {}", ex.getSQLState());
			logger.error("VendorError: {}", ex.getErrorCode());
			throw ex;
		}

        BatallaBean batalla = new BatallaBeanImpl();
        batalla.setNombre("Midway");

        // Elimina la batalla
        daoBatalla.eliminarBatalla(batalla);

        // Verifica que la batalla fue eliminada
        //
        String sqlCheck = "SELECT * FROM batallas WHERE nombre_batalla = 'Midway'";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sqlCheck);

        // Verifica que el RS es vacio
        assertFalse(rs.next());
    }

    @Test
    void testEliminarBatalla_IntegridadReferencial_Exception() throws Exception {
        // Prueba que no se elimine una batalla de la base de datos si tiene registros asociados
        //
        BatallaBean batalla = new BatallaBeanImpl();
        batalla.setNombre("Guadalcanal");

        // Elimina la batalla
        Exception exception = assertThrows(Exception.class, () -> {
            daoBatalla.eliminarBatalla(batalla);
        });
        // Importante: Si en la definición de la tabla se establece una clave foránea con ON DELETE CASCADE
        // no se lanzará la excepción de integridad referencial y se eliminarán los registros asociados en la tabla
        // principal y en las tablas secundarias.

        assertEquals("No se puede eliminar la batalla por que esta referenciada en otra tabla", exception.getMessage());

        // Verifica que la batalla no fue eliminada
        //
        String sql = "SELECT * FROM batallas WHERE nombre_batalla = 'Guadalcanal'";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        rs.next();
        
        assertEquals("Guadalcanal", rs.getString("nombre_batalla"));
        assertEquals("1942-11-15", Fechas.convertirDateAStringDB(rs.getDate("fecha")));
    }

    @Test
    void testRecuperarBatallas() throws Exception {
        // Prueba que se recuperen todas las batallas de la base de datos
        //        
        List<BatallaBean> batallas = daoBatalla.recuperarBatallas(new BatallaBeanImpl());

        // Verifica que se recuperen TODAS las batallas (ya que el nombre es vacio y la fecha es null)
        //
        assertEquals(6, batallas.size());
    }

    @Test
    void testRecuperarBatallas_FiltroNombre() throws Exception {
        // Prueba que se recuperen las batallas de la base de datos que contengan en su nombre "North"
        //
        BatallaBean batalla = new BatallaBeanImpl();
        batalla.setNombre("North");

        List<BatallaBean> batallas = daoBatalla.recuperarBatallas(batalla);

        // Verifica que se recuperen las batallas que coincidan con el nombre
        //
        assertEquals(2, batallas.size());

        // ordena el resultado por fecha ascendente
        batallas.sort((b1, b2) -> b1.getFecha().compareTo(b2.getFecha()));

        // Verifica que las batallas recuperadas sean las correctas
        //
        assertEquals("North Atlantic", batallas.get(0).getNombre());
        assertEquals("1941-05-24", Fechas.convertirDateAStringDB(batallas.get(0).getFecha()));

        assertEquals("North Cape", batallas.get(1).getNombre());
        assertEquals("1943-12-26", Fechas.convertirDateAStringDB(batallas.get(1).getFecha()));
    }

    @Test
    void testRecuperarBatallas_FiltroFecha() throws Exception {
        // Prueba que se recuperen las batallas de la base de datos que ocurrieron en la fecha "1942-05-01"
        //
        BatallaBean batalla = new BatallaBeanImpl();
        batalla.setFecha(Date.valueOf("1942-05-01"));

        List<BatallaBean> batallas = daoBatalla.recuperarBatallas(batalla);

        // Verifica que se recuperen las batallas que coincidan con la fecha
        //
        assertEquals(1, batallas.size());

        // Verifica que las batallas recuperadas sean las correctas
        //
        assertEquals("Murmansk", batallas.get(0).getNombre());
        assertEquals("1942-05-01", Fechas.convertirDateAStringDB(batallas.get(0).getFecha()));
    }

    @Test
    void testRecuperarBatallas_FiltroNombreFecha() throws Exception {
        // Prueba que se recuperen las batallas de la base de datos que contengan en su nombre "North" y ocurrieron en la fecha "1941-05-24"
        //
        BatallaBean batalla = new BatallaBeanImpl();
        batalla.setNombre("North");
        batalla.setFecha(Date.valueOf("1941-05-24"));

        List<BatallaBean> batallas = daoBatalla.recuperarBatallas(batalla);

        // Verifica que se recuperen las batallas que coincidan con el nombre y la fecha
        //
        assertEquals(1, batallas.size());

        // Verifica que las batallas recuperadas sean las correctas
        //
        assertEquals("North Atlantic", batallas.get(0).getNombre());
        assertEquals("1941-05-24", Fechas.convertirDateAStringDB(batallas.get(0).getFecha()));
    }

    @Test
    void testActualizarBatalla() throws Exception {
        // Prueba que se actualice una batalla en la base de datos
        //
        // Insertamos una batalla para actualizarla después.
        // Se crea una nueva para que no tenga referencias en otras tablas y pueda ser modificada.
        //
        String sql= "INSERT INTO batallas(nombre_batalla, fecha) values (?,?)";
        try
        {  
            PreparedStatement insert = connection.prepareStatement(sql); 
            insert.setString(1, "Midway");
            insert.setDate(2, Date.valueOf("1942-06-04"));
            insert.executeUpdate();
        }
        catch (SQLException ex)
        {
            logger.error("SQLException: {}", ex.getMessage());
            logger.error("SQLState: {}", ex.getSQLState());
            logger.error("VendorError: {}", ex.getErrorCode());
            throw ex;
        }

        BatallaBean batallaSeleccionada = new BatallaBeanImpl();
        batallaSeleccionada.setNombre("Midway");

        BatallaBean batallaModificada = new BatallaBeanImpl();
        batallaModificada.setNombre("Denmark Strait");
        batallaModificada.setFecha(Date.valueOf("1941-05-24"));

        daoBatalla.actualizarBatalla(batallaSeleccionada, batallaModificada);

        // Verifica que la batalla anterior ya no existe
        //
        String sqlinsert = "SELECT * FROM batallas WHERE nombre_batalla = 'Midway'";
        Statement statement = connection.createStatement();
        ResultSet rsinsert = statement.executeQuery(sqlinsert);
        assertFalse(rsinsert.next());
        //
        // Verifica que la batalla anterior conserva sus datos
        //
        String sqlCheck = "SELECT * FROM batallas WHERE nombre_batalla = 'Denmark Strait'";
        Statement statementCheck = connection.createStatement();
        ResultSet rsCheck = statementCheck.executeQuery(sqlCheck);
        rsCheck.next();
        assertEquals("Denmark Strait", rsCheck.getString("nombre_batalla"));
        assertEquals("1941-05-24", Fechas.convertirDateAStringDB(rsCheck.getDate("fecha")));
    }

    @Test
    void testActualizarBatalla_IntegridadReferencial_Exception() throws Exception {
        // Prueba que se actualice una batalla en la base de datos
        //
        BatallaBean batallaSeleccionada = new BatallaBeanImpl();
        batallaSeleccionada.setNombre("Surigao Strait");

        BatallaBean batallaModificada = new BatallaBeanImpl();
        batallaModificada.setNombre("Midway");
        batallaModificada.setFecha(Date.valueOf("1942-06-04"));

        // Actualiza la batalla
        Exception exception = assertThrows(Exception.class, () -> {
            daoBatalla.actualizarBatalla(batallaSeleccionada, batallaModificada);
        });
        
        assertEquals("No se puede actualizar la batalla por que esta referenciada en otra tabla", exception.getMessage());

        // Verifica que la batalla no fue actualizada
        //
        String sql = "SELECT * FROM batallas WHERE nombre_batalla = 'Midway'";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        assertFalse(rs.next());
        //
        // Verifica que la batalla anterior conserva sus datos
        //
        String sqlCheck = "SELECT * FROM batallas WHERE nombre_batalla = 'Surigao Strait'";
        Statement statementCheck = connection.createStatement();
        ResultSet rsCheck = statementCheck.executeQuery(sqlCheck);
        rsCheck.next();
        assertEquals("Surigao Strait", rsCheck.getString("nombre_batalla"));
        assertEquals("1944-10-25", Fechas.convertirDateAStringDB(rsCheck.getDate("fecha")));
    }
}
