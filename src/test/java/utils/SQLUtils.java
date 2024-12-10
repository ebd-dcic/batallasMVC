package utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

public class SQLUtils {

    private static Logger logger = LoggerFactory.getLogger(SQLUtils.class);

    private static String filterComments(String sql) {
        return sql.replaceAll("(?:--|#).*|(?s)/\\*.*?\\*/", "");
    }

    /*
     * Ejecuta un script SQL en la conexión pasada como parámetro
     */
    public static void executeSqlScript(Connection connection, String filePath) throws Exception {

        connection.setAutoCommit(false); // Desactiva el autocommit

        String sql = new String(Files.readAllBytes(Paths.get(filePath)));

        // Eliminar comentarios del script
        sql = removeComments(sql);

        // JDBC no procesa sentencias DELIMITER porque es algo específico de MySQL
        String delimiter = ";"; // Delimitador de sentencias SQL por defecto
        String currentDelimiter = delimiter; // Delimitador actual

        // Dividir el script en múltiples sentencias
        List<String> statements = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        for (String line : sql.split("\n")) {

            line = line.trim();

            if (line.toLowerCase().startsWith("delimiter")) {
                // Cambiar el delimitador
                currentDelimiter = line.substring(10).trim();
                logger.debug("Nuevo delimiter: {}", currentDelimiter);
                continue; // No proceses esta línea como parte de una sentencia SQL
            }

            sb.append(line).append(" ");

            // Cuando encontramos el delimitador actual, añadimos la sentencia completa
            if (line.endsWith(currentDelimiter)) {
                // Eliminar el delimitador de la sentencia acumulada
                String statement = sb.toString().replace(currentDelimiter, "").trim();
                logger.debug("Se agrega la sentencia: {}", statement);
                statements.add(statement);
                sb.setLength(0); // Limpiar el StringBuilder para la siguiente sentencia
            }
        }

        try (Statement stmt = connection.createStatement()) {

            // Agregar las sentencias al batch
            for (String statement : statements) {
                stmt.addBatch(statement);
            }

            // Ejecutar el batch
            int[] updateCounts = stmt.executeBatch();

            // Procesar los resultados del batch
            for (int i = 0; i < updateCounts.length; i++) {
                if (updateCounts[i] == Statement.EXECUTE_FAILED) {
                    throw new SQLException("Error ejecutando el script SQL: " + filePath);
                }
            }

            connection.commit(); // Confirmar la transacción
            logger.debug("Transacción confirmada - Se ejecutó el script SQL: {}", filePath);

        } catch (SQLException e) {
            connection.rollback(); // Revertir la transacción en caso de error
            System.err.println("Error ejecutando el script SQL: " + filePath);
            System.err.println("Código de error SQL: " + e.getErrorCode());
            System.err.println("Estado SQL: " + e.getSQLState());
            System.err.println("Mensaje de error: " + e.getMessage());
            //e.printStackTrace();
            throw e; // Re-lanza la excepción para que el fallo sea visible en las pruebas
        }
    }

    /*
     * Calcula el hash MD5 de una cadena
     */
    public static String md5(String cadena) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(cadena.getBytes());
        BigInteger number = new BigInteger(1, messageDigest);
        return number.toString(16);
    }


    private static String removeComments(String sql) {
        // Expresión regular para eliminar comentarios de línea y de bloque
        String regex = "(--.*?$)|(/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/)|(#.*?$)";
        
        // Compilar la expresión regular con la opción MULTILINE para procesar línea por línea
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sql);

        // Reemplazar los comentarios por cadenas vacías
        return matcher.replaceAll("").trim();
    }    

    
}
