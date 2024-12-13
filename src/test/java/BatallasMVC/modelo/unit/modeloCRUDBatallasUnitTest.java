package BatallasMVC.modelo.unit;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

import BatallasMVC.modelo.BatallaBean;
import BatallasMVC.modelo.ModeloCRUDBatallas;
import BatallasMVC.modelo.ModeloCRUDBatallasImpl;

public class modeloCRUDBatallasUnitTest {

    private static Logger logger = LoggerFactory.getLogger(modeloCRUDBatallasUnitTest.class);

    @Mock
    private Connection mockConnection;

    @Mock
    private Statement mockStatement;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @Spy
    private ModeloCRUDBatallas spyModeloCRUDBatallas;

    private ModeloCRUDBatallas modeloCRUDBatallas;  

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        mockConnection = mock(Connection.class);
        mockStatement = mock(Statement.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        // crea una instancia de modeloCRUDBatallasImpl sin utilizar el constructor
        Objenesis objenesis = new ObjenesisStd();
        modeloCRUDBatallas = objenesis.newInstance(ModeloCRUDBatallasImpl.class);
    }

    @Test
    void testRecuperarTablaBatallas() throws Exception {
        // Supone que va a utilizar this.consulta en lugar de utilizar la conexion manual
        // Se usa spy para evitar llamar a this.consulta() en el método recuperarTablaBatallas()
        //
        spyModeloCRUDBatallas = Mockito.spy(modeloCRUDBatallas);

        // al llamar a consulta retorna un resultset de 3 batallas
        when(mockResultSet.next())
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(false);
        when(mockResultSet.getString("nombre_batalla"))
            .thenReturn("Batalla1")
            .thenReturn("Batalla2")
            .thenReturn("Batalla3");
        when(mockResultSet.getDate("fecha"))
            .thenReturn(Date.valueOf("2021-01-01"))
            .thenReturn(Date.valueOf("2021-02-02"))
            .thenReturn(Date.valueOf("2021-03-03"));

        doReturn(mockResultSet).when(spyModeloCRUDBatallas).consulta(anyString());

        List<BatallaBean> batallas = spyModeloCRUDBatallas.recuperarTablaBatallas();

        assertEquals(3, batallas.size());

        assertEquals("Batalla1", batallas.get(0).getNombre());
        assertEquals(Date.valueOf("2021-01-01"), batallas.get(0).getFecha());
        assertEquals("Batalla2", batallas.get(1).getNombre());
        assertEquals(Date.valueOf("2021-02-02"), batallas.get(1).getFecha());
        assertEquals("Batalla3", batallas.get(2).getNombre());
        assertEquals(Date.valueOf("2021-03-03"), batallas.get(2).getFecha());
    }


}
