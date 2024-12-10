#Archivo batch (batalllas.sql) para la creación de la 
#Base de datos del práctico de SQL

#Lo que esta después del "#" es un comentario

# Creo de la Base de Datos
CREATE DATABASE batallas;

# selecciono la base de datos sobre la cual voy a hacer modificaciones
USE batallas;

#-------------------------------------------------------------------------
# Creación Tablas para las entidades

CREATE TABLE barcos (
 nombre_barco VARCHAR(45) NOT NULL, 
 capitan VARCHAR(45) NOT NULL, 
 id  INT UNSIGNED NOT NULL AUTO_INCREMENT,
 
 CONSTRAINT pk_barcos 
 PRIMARY KEY (nombre_barco),
 
 KEY (id)
) ENGINE=InnoDB;

CREATE TABLE clases (
 clase VARCHAR(45) NOT NULL,
 tipo VARCHAR(20) NOT NULL,
 pais VARCHAR(45) NOT NULL,
 nro_caniones SMALLINT unsigned NOT NULL,
 calibre SMALLINT unsigned NOT NULL,
 desplazamiento SMALLINT unsigned NOT NULL,
 
 CONSTRAINT pk_clases
 PRIMARY KEY (clase)
) ENGINE=InnoDB;

CREATE TABLE batallas (
 nombre_batalla VARCHAR(25) NOT NULL DEFAULT '',
 fecha DATE NOT NULL,
 
 CONSTRAINT pk_batallas
 PRIMARY KEY (nombre_batalla)
) ENGINE=InnoDB;

#-------------------------------------------------------------------------
# Creación Tablas para las relaciones

CREATE TABLE barco_clase (
 nombre_barco VARCHAR(45) NOT NULL,
 clase VARCHAR(45) NOT NULL,
 lanzado SMALLINT unsigned NOT NULL,
 
 CONSTRAINT pk_barco_clase
 PRIMARY KEY (nombre_barco),

 CONSTRAINT FK_barco_clase_barco 
 FOREIGN KEY (nombre_barco) REFERENCES barcos (nombre_barco) 
   ON DELETE RESTRICT ON UPDATE CASCADE,
  
 
 CONSTRAINT FK_barco_clase_clase 
 FOREIGN KEY (clase) REFERENCES clases (clase)
   ON DELETE RESTRICT ON UPDATE RESTRICT
 
) ENGINE=InnoDB;

CREATE TABLE resultados (
 nombre_barco VARCHAR(45) NOT NULL,
 nombre_batalla VARCHAR(45) NOT NULL,
 resultado VARCHAR(45) NOT NULL, 
 
 CONSTRAINT pk_resultados
 PRIMARY KEY (nombre_barco,nombre_batalla),
 
 CONSTRAINT FK_resultados_barco 
 FOREIGN KEY (nombre_barco) REFERENCES barcos (nombre_barco)
    ON DELETE RESTRICT ON UPDATE CASCADE,
 
 CONSTRAINT FK_resultados_batallas 
 FOREIGN KEY (nombre_batalla) REFERENCES batallas (nombre_batalla) 
#    ON DELETE CASCADE ON UPDATE CASCADE
   ON DELETE RESTRICT ON UPDATE RESTRICT 
) ENGINE=InnoDB;

#-------------------------------------------------------------------------
# Creación de vistas 
# acorazados = Datos de todos los barcos que son "acorazados"

   CREATE VIEW acorazados AS 
   SELECT b.nombre_barco, b.capitan, 
          c.clase, c.pais, c.nro_caniones, c.calibre, c. desplazamiento,
          b_c.lanzado
   FROM (barcos as b JOIN  barco_clase as b_c ON b.nombre_barco = b_c.nombre_barco) 
        JOIN clases as c   ON c.clase = b_c.clase
   WHERE c.tipo="acorazado";


#-------------------------------------------------------------------------
# Creación de usuarios y otorgamiento de privilegios

# primero creo un usuario con CREATE USER
 
   CREATE USER 'admin_batallas'@'localhost'  IDENTIFIED BY 'pwadmin';

# el usuario admin_batallas con password 'pwadmin' puede conectarse solo 
# desde la desde la computadora donde se encuentra el servidor de MySQL (localhost)   

# luego le otorgo privilegios utilizando solo la sentencia GRANT

    GRANT ALL PRIVILEGES ON batallas.* TO 'admin_batallas'@'localhost' WITH GRANT OPTION;

# El usuario 'admin_batallas' tiene acceso total a todas las tablas de 
# la B.D. batallas y puede crear nuevos usuarios y otorgar privilegios.


# primero creo un usuario con CREATE USER

    CREATE USER 'barco'@'localhost' IDENTIFIED BY 'pwbarco'; 

# el usuario 'barco' con password 'pwbarco' puede conectarse solo desde localhost

# Luego le otorgo privilegios con GRANT

    GRANT SELECT ON batallas.acorazados TO 'barco'@'localhost';

# el usuario 'barco' solo puede acceder a la tabla (vista) acorazados
# con permiso para seleccionar  

#--------------------------------------------------------------
# Creación del usuario 'prueba' desde % (cualquier host) porque se va a utilizar Docker para la conexión
CREATE USER 'prueba'@'%'  IDENTIFIED BY 'pwprueba';

# Otorgo todos los privilegios al usuario 'prueba' desde cualquier host
GRANT ALL PRIVILEGES ON batallas.* TO 'prueba'@'%' WITH GRANT OPTION;