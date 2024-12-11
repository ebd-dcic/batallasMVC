package BatallasMVC.modelo.unit;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

import BatallasMVC.modelo.BatallaBean;
import BatallasMVC.modelo.BatallaBeanImpl;
import BatallasMVC.modelo.DAOBatallaImpl;

public class DAOBatallaImplUnitTest {

    private static Logger logger = LoggerFactory.getLogger(DAOBatallaImplUnitTest.class);

    @Mock
    private Connection mockConnection;

    @Mock
    private Statement mockStatement;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private DAOBatallaImpl daoBatalla;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockConnection = mock(Connection.class);
        mockStatement = mock(Statement.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        daoBatalla = new DAOBatallaImpl();
        daoBatalla.setConexion(mockConnection);
    }

    @Test
    void testInsertarBatalla() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // retorna la cantidad de filas afectadas

        // Batalla a insertar
        BatallaBean batalla = new BatallaBeanImpl();
        batalla.setNombre("Batalla de Waterloo");
        batalla.setFecha(Date.valueOf("1815-06-18"));

        daoBatalla.insertarBatalla(batalla);

        verify(mockPreparedStatement, times(1)).setString(1, "Batalla de Waterloo");
        verify(mockPreparedStatement, times(1)).setDate(2, Date.valueOf("1815-06-18"));
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testInsertarBatallaException() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Duplicate entry for PRIMARY KEY", "SQLState", 1062));

        // Batalla a insertar
        BatallaBean batalla = new BatallaBeanImpl();
        batalla.setNombre("Batalla de Waterloo");
        batalla.setFecha(Date.valueOf("1815-06-18"));

        Exception exception = assertThrows(Exception.class, () -> {
            daoBatalla.insertarBatalla(batalla);
        });

        assertEquals("Ya existe una batalla con nombre 'Batalla de Waterloo'", exception.getMessage());
    }

    @Test
    void testInsertarBatallaSQLException() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Error inesperado al insertar la batalla en la B.D.", "SQLState", 9999));

        // Batalla a insertar
        BatallaBean batalla = new BatallaBeanImpl();
        batalla.setNombre("Batalla de Waterloo");
        batalla.setFecha(Date.valueOf("1815-06-18"));

        Exception exception = assertThrows(Exception.class, () -> {
            daoBatalla.insertarBatalla(batalla);
        });

        assertEquals("Error inesperado al insertar la batalla en la B.D.", exception.getMessage());
    }

    @Test
    void testEliminarBatalla() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // retorna la cantidad de filas afectadas

        // Batalla a eliminar
        BatallaBean batalla = new BatallaBeanImpl();
        batalla.setNombre("Batalla de Waterloo");

        daoBatalla.eliminarBatalla(batalla);

        verify(mockPreparedStatement, times(1)).setString(1, "Batalla de Waterloo");
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testEliminarBatallaException() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        // Se simula una excepción de clave foránea
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Cannot delete or update a parent row: a foreign key constraint fails (`batallas`.`batallas_personajes`, CONSTRAINT `batallas_personajes_ibfk_1` FOREIGN KEY (`nombre_batalla`) REFERENCES `batallas` (`nombre_batalla`))", "SQLState", 1451));

        // Batalla a eliminar
        BatallaBean batalla = new BatallaBeanImpl();
        batalla.setNombre("Batalla de Waterloo");

        Exception exception = assertThrows(Exception.class, () -> {
            daoBatalla.eliminarBatalla(batalla);
        });

        assertEquals("No se puede eliminar la batalla por que esta referenciada en otra tabla", exception.getMessage());
    }

    @Test
    void testEliminarBatallaSQLException() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Unexpected Error", "SQLState", 9999));

        // Batalla a eliminar
        BatallaBean batalla = new BatallaBeanImpl();
        batalla.setNombre("Batalla de Waterloo");

        Exception exception = assertThrows(Exception.class, () -> {
            daoBatalla.eliminarBatalla(batalla);
        });

        assertEquals("Error inesperado al borrar la batalla en la B.D.", exception.getMessage());
    }

    @Test
    void testRecuperarBatallas() throws Exception {
        // Simula una consulta que retorna una batalla
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getString("nombre_batalla")).thenReturn("Batalla de Waterloo");
        when(mockResultSet.getDate("fecha")).thenReturn(Date.valueOf("1815-06-18"));

        // Batalla a buscar con nombre parcial y fecha
        BatallaBean batalla = new BatallaBeanImpl();
        batalla.setNombre("Waterloo");
        batalla.setFecha(Date.valueOf("1815-06-18"));

        List<BatallaBean> batallas = daoBatalla.recuperarBatallas(batalla);

        // Verifica que se haya recuperado la batalla
        assertEquals(1, batallas.size());
        assertEquals("Batalla de Waterloo", batallas.get(0).getNombre());
        assertEquals(Date.valueOf("1815-06-18"), batallas.get(0).getFecha());

        verify(mockPreparedStatement, times(1)).executeQuery();

        // captura la SQL que se envia al PreparedStatement
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection, times(1)).prepareStatement(sqlCaptor.capture());
        String sql = sqlCaptor.getValue();
        logger.info("SQL: {}", sql);
        assertTrue(sql.contains("nombre_batalla LIKE ?"));
        assertTrue(sql.contains("fecha = ?"));

        // captura los parametros que se envian al PreparedStatement
        ArgumentCaptor<String> paramCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockPreparedStatement, times(2)).setString(anyInt(), paramCaptor.capture());
        List<String> params = paramCaptor.getAllValues();
        assertEquals("%Waterloo%", params.get(0));
        assertEquals("1815-06-18", params.get(1));
    }

    @Test
    void testRecuperarBatallas_FiltroSoloNombre() throws Exception {
        // Simula una consulta que retorna dos batallas
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next())
            .thenReturn(true)   // primera batalla
            .thenReturn(true)   // segunda batalla
            .thenReturn(false);

        when(mockResultSet.getString("nombre_batalla"))
            .thenReturn("Batalla de Waterloo")      // primera batalla
            .thenReturn("Batalla de Watergate");    // segunda batalla

        when(mockResultSet.getDate("fecha"))
            .thenReturn(Date.valueOf("1815-06-18"))     // primera batalla
            .thenReturn(Date.valueOf("1972-06-17"));    // segunda batalla

        // Batalla a buscar con nombre parcial
        BatallaBean batalla = new BatallaBeanImpl();
        batalla.setNombre("Water");

        List<BatallaBean> batallas = daoBatalla.recuperarBatallas(batalla);

        // ordena las batallas por fecha de forma ascendente
        batallas.sort((b1, b2) -> b1.getFecha().compareTo(b2.getFecha()));
        
        // Verifica que se haya recuperado las batallas
        assertEquals(2, batallas.size());
        
        logger.debug("Batallas: {}", String.join(", ", batallas.stream().map(BatallaBean::getNombre).toArray(String[]::new)));
        assertEquals("Batalla de Waterloo", batallas.get(0).getNombre());
        assertEquals(Date.valueOf("1815-06-18"), batallas.get(0).getFecha());
        assertEquals("Batalla de Watergate", batallas.get(1).getNombre());
        assertEquals(Date.valueOf("1972-06-17"), batallas.get(1).getFecha());

        verify(mockPreparedStatement, times(1)).executeQuery();

        // captura la SQL que se envia al PreparedStatement
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection, times(1)).prepareStatement(sqlCaptor.capture());
        String sql = sqlCaptor.getValue();
        logger.info("SQL: {}", sql);
        assertTrue(sql.contains("nombre_batalla LIKE ?"));
        assertFalse(sql.contains("fecha = ?"));

        // captura los parametros que se envian al PreparedStatement
        ArgumentCaptor<String> paramCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockPreparedStatement, times(1)).setString(anyInt(), paramCaptor.capture());
        List<String> params = paramCaptor.getAllValues();
        assertEquals("%Water%", params.get(0));
        // Verifica que solo se envie un parametro
        assertEquals(1, params.size());
    }

    @Test
    void testRecuperarBatallas_FiltroSoloFecha() throws Exception {
        // Simula una consulta que retorna dos batallas que ocurrieron en la misma fecha
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next())
            .thenReturn(true)   // primera batalla
            .thenReturn(true)   // segunda batalla
            .thenReturn(false);

        when(mockResultSet.getString("nombre_batalla"))
            .thenReturn("Batalla de Waterloo")      // primera batalla
            .thenReturn("Batalla de Watergate");    // segunda batalla

        when(mockResultSet.getDate("fecha"))
            .thenReturn(Date.valueOf("1815-06-18"))     // primera batalla
            .thenReturn(Date.valueOf("1815-06-18"));    // segunda batalla

        // Batalla a buscar con fecha
        BatallaBean batalla = new BatallaBeanImpl();
        batalla.setFecha(Date.valueOf("1815-06-18"));

        List<BatallaBean> batallas = daoBatalla.recuperarBatallas(batalla);

        // ordena las batallas por nombre de forma ascendente
        batallas.sort((b1, b2) -> b1.getNombre().compareTo(b2.getNombre()));

        // Verifica que se haya recuperado las batallas
        assertEquals(2, batallas.size());

        assertEquals("Batalla de Watergate", batallas.get(0).getNombre());
        assertEquals(Date.valueOf("1815-06-18"), batallas.get(0).getFecha());
        assertEquals("Batalla de Waterloo", batallas.get(1).getNombre());
        assertEquals(Date.valueOf("1815-06-18"), batallas.get(1).getFecha());

        verify(mockPreparedStatement, times(1)).executeQuery();

        // captura la SQL que se envia al PreparedStatement
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection, times(1)).prepareStatement(sqlCaptor.capture());
        String sql = sqlCaptor.getValue();
        logger.info("SQL: {}", sql);
        assertFalse(sql.contains("nombre_batalla LIKE ?"));
        assertTrue(sql.contains("fecha = ?"));

        // captura los parametros que se envian al PreparedStatement
        ArgumentCaptor<String> paramCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockPreparedStatement, times(1)).setString(anyInt(), paramCaptor.capture());
        List<String> params = paramCaptor.getAllValues();
        assertEquals("1815-06-18", params.get(0));
        // Verifica que solo se envie un parametro
        assertEquals(1, params.size());
    }

    @Test
    void testRecuperarBatallas_SinResultados() throws Exception {
        // Simula una consulta que no retorna batallas
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        BatallaBean batalla = new BatallaBeanImpl();

        List<BatallaBean> batallas = daoBatalla.recuperarBatallas(batalla);

        // Verifica que no se haya recuperado ninguna batalla
        assertEquals(0, batallas.size());

        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testRecuperarBatallasSQLException() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Unexpected Error", "SQLState", 9999));

        // Batalla a buscar
        BatallaBean batalla = new BatallaBeanImpl();

        Exception exception = assertThrows(Exception.class, () -> {
            daoBatalla.recuperarBatallas(batalla);
        });

        assertEquals("Error al recuperar las batallas", exception.getMessage());
    }

    @Test
    void testActualizarBatalla() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // retorna la cantidad de filas afectadas

        // Batalla a seleccionar
        BatallaBean batallaSeleccionada = new BatallaBeanImpl();
        batallaSeleccionada.setNombre("Batalla de Waterloo");

        // Batalla a modificar
        BatallaBean batallaModificada = new BatallaBeanImpl();
        batallaModificada.setNombre("Batalla de Watergate");
        batallaModificada.setFecha(Date.valueOf("1972-06-17"));

        daoBatalla.actualizarBatalla(batallaSeleccionada, batallaModificada);

        verify(mockPreparedStatement, times(1)).setString(1, "Batalla de Watergate");
        verify(mockPreparedStatement, times(1)).setDate(2, Date.valueOf("1972-06-17"));
        verify(mockPreparedStatement, times(1)).setString(3, "Batalla de Waterloo");
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testActualizarBatallaException() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Cannot delete or update a parent row: a foreign key constraint fails", "SQLState", 1451));

        // Batalla a seleccionar
        BatallaBean batallaSeleccionada = new BatallaBeanImpl();
        batallaSeleccionada.setNombre("Batalla de Waterloo");

        // Batalla a modificar
        BatallaBean batallaModificada = new BatallaBeanImpl();
        batallaModificada.setNombre("Batalla de Watergate");
        batallaModificada.setFecha(Date.valueOf("1972-06-17"));

        Exception exception = assertThrows(Exception.class, () -> {
            daoBatalla.actualizarBatalla(batallaSeleccionada, batallaModificada);
        });

        assertEquals("No se puede actualizar la batalla por que esta referenciada en otra tabla", exception.getMessage());
    }

    @Test
    void testActualizarBatallaSQLException() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Unexpected Error", "SQLState", 9999));

        // Batalla a seleccionar
        BatallaBean batallaSeleccionada = new BatallaBeanImpl();
        batallaSeleccionada.setNombre("Batalla de Waterloo");

        // Batalla a modificar
        BatallaBean batallaModificada = new BatallaBeanImpl();
        batallaModificada.setNombre("Batalla de Watergate");
        batallaModificada.setFecha(Date.valueOf("1972-06-17"));

        Exception exception = assertThrows(Exception.class, () -> {
            daoBatalla.actualizarBatalla(batallaSeleccionada, batallaModificada);
        });

        assertEquals("Error inesperado al borrar la batalla en la B.D.", exception.getMessage());
    }
}
