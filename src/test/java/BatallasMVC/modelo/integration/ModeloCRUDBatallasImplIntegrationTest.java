package BatallasMVC.modelo.integration;

import BatallasMVC.modelo.*;
import utils.Conexion;
import utils.DatabaseSetup;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.*;
import java.util.List;

@Tag("integration")
public class ModeloCRUDBatallasImplIntegrationTest {

    private static Connection connection;

    private static ModeloCRUDBatallasImpl modelo;

    private static Logger logger = LoggerFactory.getLogger(ModeloCRUDBatallasImplIntegrationTest.class);

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

        modelo = new ModeloCRUDBatallasImpl();
        modelo.setConexion(connection);
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Limpia los datos después de cada prueba si es necesario
    }

    @AfterAll
    public static void tearDownOnce() throws Exception {
        if (connection != null && !connection.isClosed()) {
            DatabaseSetup.cleanDatabase();
        }
    }

    @Test
    void testConexion() throws Exception {
        // Prueba que la conexión a la base de datos esté disponible
        assertNotNull(connection);
        assertFalse(connection.isClosed());
    }

    @Test
    void testRecuperarTablaBatallas() throws Exception {
        // Prueba que recupera todas las batallas de la base de datos
        //
        int cantidadBatallasEsperada = 6;

        List<BatallaBean> batallas = modelo.recuperarTablaBatallas();
        assertNotNull(batallas);
        assertEquals(cantidadBatallasEsperada, batallas.size());
    }

}
