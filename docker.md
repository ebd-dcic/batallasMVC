Esta configuración de docker compose permite correr un contenedor mysql guardando de forma persistente los datos de la BD.

La carpeta `bd` contiene toda la información necesaria para mysql

- src: Es una carpeta visible dentro del contenedor en /app, utilizada para poner el codigo fuente de java y sql.

# Inicio


`docker compose up -d`

# Inicializacion

Se debe cambiar los permisos del archivo config-file.cnf para que mysql no produzca una advertencia.

Desde la linea de comando se puede ejecutar
`docker exec -i mysql_batallas_2024 chmod 0400 /etc/mysql/conf.d/config-file.cnf`

o ingresar al contenedor
`docker exec -it mysql_batallas_2024 sh`
y luego dentro del mismo ejecutar
`chmod 0400 /etc/mysql/conf.d/config-file.cnf`

# Procesamiento de Scripts .sql

- docker exec -i mysql_container mysql -uroot -psecret mysql < db.sql

	docker exec -i mysql_batallas_2024 mysql -uroot -p1234 mysql < src/sql/batallas.sql
	docker exec -i mysql_batallas_2024 mysql -uroot -p1234 mysql < src/sql/borrar.sql


### En Linux acceder al contenedor backend

`docker ps`

`docker exec -it <Contenedor ID> sh`
docker exec -it mysql_batallas_2024 sh`



Get-Content src/sql/batallas.sql | docker exec -i mysql_batallas_2024 mysql -uroot -p1234 mysql