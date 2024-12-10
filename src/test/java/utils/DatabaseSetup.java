package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.io.FileInputStream;
import java.sql.Connection;

public class DatabaseSetup {

    private static Logger logger = LoggerFactory.getLogger(DatabaseSetup.class);

    private static boolean isDatabaseSetUp = false;
    
    private static Connection connection;

    // propiedades de configuración
    private static final String CONFIG_PROPERTIES = "cfg/config.properties";
    private static final String CONEXION_TESTING = "src/main/resources/conexionBDforTesting.properties";
    private static String cfgUbicacion;
    private static String cfgUser;
    private static String cfgPassword;
    private static String cfgCreate;
    private static String cfgInsert;
    private static String cfgDelete;
    private static String cfgDropDB;
    private static String cfgDropUsers;

    public static void reset() {
        isDatabaseSetUp = false;
    }

    public static void setUp() throws Exception {

        // Si la base de datos no fue inicializada, se inicializa
        if (!isDatabaseSetUp) {
            logger.debug("Inicializando la base de datos...");

            try {
                cargarPropiedades();

                Conexion.inicializar(CONEXION_TESTING);

                // Establece una conexión a la base de datos de mysql para crear la base de datos banco
                connection = Conexion.getConnection("mysql", cfgUser, cfgPassword);

                if (connection == null) {
                    throw new Exception("No se pudo establecer la conexión a la base de datos.");
                }

                // Elimina los usuarios si existen
                SQLUtils.executeSqlScript(connection, cfgDropUsers);

                // Elimina la base de datos si existe
                SQLUtils.executeSqlScript(connection, cfgDropDB);

                // Script de creación de la base de datos
                SQLUtils.executeSqlScript(connection, cfgCreate);

                // Anula la conexión actual y se conecta a la base de datos configurada en el archivo de propiedades 
                // (cfg/conexionBD.properties) por defecto banco
                Conexion.closeConnection(connection);
                connection = Conexion.getConnection(cfgUser, cfgPassword);

                // Script de inserción de datos
                SQLUtils.executeSqlScript(connection, cfgInsert);

                logger.debug("Se inicializó la base de datos correctamente.");

            } catch (Exception e) {
                logger.error("Error al inicializar la base de datos", e);
                throw e;
            }

            isDatabaseSetUp = true;
        }
    }

    public static void cleanDatabase() throws Exception {
        try {
            SQLUtils.executeSqlScript(connection, cfgDelete);
        } catch (Exception e) {
            logger.error("Error al limpiar la base de datos", e);
            throw e;
        }
    }

    public static void loadData() throws Exception {
        try {
            if (isDatabaseSetUp) {
                // Elimina los datos de prueba precargados
                SQLUtils.executeSqlScript(connection, cfgDelete);
            }
            // Carga los datos de prueba
            SQLUtils.executeSqlScript(connection, cfgInsert);
        } catch (Exception e) {
            logger.error("Error al cargar los datos en la base de datos", e);
            throw e;
        }
    }

    public static void tearDown() throws Exception {
        try {
                // Elimina los usuarios si existen
                SQLUtils.executeSqlScript(connection, cfgDropUsers);

                // Elimina la base de datos si existe
                SQLUtils.executeSqlScript(connection, cfgDropDB);

        } catch (Exception e) {
            logger.error("Error al borrar la base de datos", e);
            throw e;
        }
    }

    private static void cargarPropiedades() throws Exception {
        // Obtengo los parametros de configuración
        Properties prop = new Properties();
        try {
            logger.debug("Se intenta leer el archivo de propiedades {}", CONFIG_PROPERTIES);

            FileInputStream file = new FileInputStream(CONFIG_PROPERTIES);
            prop.load(file);

            cfgUser = prop.getProperty("test.script.user", "");
            logger.debug("Usuario de la base de datos: {}", cfgUser);
            if (cfgUser == "") {
                throw new Exception("No está definido el usuario de la base de datos en config.properties.");
            }
            
            cfgPassword = prop.getProperty("test.script.password", "");
            logger.debug("Contraseña de la base de datos {}", cfgPassword);
            if (cfgPassword == "") {
                throw new Exception("No está definida la contraseña de la base de datos en config.properties.");
            }

            cfgUbicacion = prop.getProperty("test.script.folder.ubicacion", "");
            logger.debug("Ubicación de los scripts: {}", cfgUbicacion);

            if (cfgUbicacion == "") {
                throw new Exception("No está definida la ruta de los archivos sql en config.properties.");
            }
    
            cfgCreate = prop.getProperty("test.script.file.create", "");
            if (cfgCreate != "") {
                cfgCreate = cfgUbicacion + "/" + cfgCreate;                
            }
            logger.debug("Script de creación: {}", cfgCreate);

            cfgInsert = prop.getProperty("test.script.file.insert", "");
            if (cfgInsert != "") {
                cfgInsert = cfgUbicacion + "/" + cfgInsert;                
            }
            logger.debug("Script de inserción: {}", cfgInsert);

            cfgDelete = prop.getProperty("test.script.file.delete", "");
            if (cfgDelete != "") {
                cfgDelete = cfgUbicacion + "/" + cfgDelete;                
            }
            logger.debug("Script de eliminación de datos: {}", cfgDelete);

            cfgDropDB = prop.getProperty("test.script.file.drop.database", "");
            if (cfgDropDB != "") {
                cfgDropDB = cfgUbicacion + "/" + cfgDropDB;
            }
            logger.debug("Script de eliminación de tablas: {}", cfgDropDB);

            cfgDropUsers = prop.getProperty("test.script.file.drop.users", "");
            if (cfgDropUsers != "") {
                cfgDropUsers = cfgUbicacion + "/" + cfgDropUsers;
            }
            logger.debug("Script de eliminación de usuarios: {}", cfgDropUsers);

        } catch (Exception ex) {
            logger.error("Se produjo un error al recuperar el archivo de propiedades.");
            throw ex;
        }
    }

}
