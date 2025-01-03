#-------------------------------------------------------------------------
# Carga de datos de Prueba

INSERT INTO clases VALUES ("Bismarck", "acorazado", "Germany", 8 ,15 ,42000);
INSERT INTO clases VALUES ("Iowa", "acorazado", "USA", 9, 16, 46000);
INSERT INTO clases VALUES ("Kongo", "crucero", "Japan", 8, 14, 32000);
INSERT INTO clases VALUES ("North Carolina", "acorazado", "USA", 9 ,16, 37000);
INSERT INTO clases VALUES ("Renown", "crucero", "Gt.Britain" ,6, 15, 32000);
INSERT INTO clases VALUES ("Revenge", "acorazado", "Gt.Britain" ,8, 15, 29000);
INSERT INTO clases VALUES ("Tennessee", "acorazado", "USA", 12 ,14 ,32000);
INSERT INTO clases VALUES ("Yamato", "acorazado", "Japan", 9 ,18 ,65000);
INSERT INTO clases VALUES ("Suffolk", "crucero", "Gt.Britain", 8 , 8 , 10000);
INSERT INTO clases VALUES ("Colorado", "acorazado", "USA", 9 , 16, 33000);

INSERT INTO batallas VALUES("Sola Air Station", "1940/04/17");
INSERT INTO batallas VALUES("North Atlantic", "1941/05/24");
INSERT INTO batallas VALUES("Guadalcanal", str_to_date('15/11/1942', '%d/%m/%Y')); 
#INSERT INTO batallas VALUES("Guadalcanal", "1942/11/15");
INSERT INTO batallas VALUES("North Cape", "1943/12/26");
INSERT INTO batallas VALUES("Surigao Strait", "1944/10/25");
INSERT INTO batallas VALUES("Murmansk","1942/05/01");

INSERT INTO barcos(nombre_barco, capitan) VALUES("California","SWIRYD" );
INSERT INTO barcos(nombre_barco, capitan) VALUES("Haruna","STEIJMAN");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Hiei","SIMONE");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Iowa","SILVETTI");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Kirishima", "SEVERINI");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Kongo","KUNISCH");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Missouri","IHITZ");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Musashi","PIETRASANTA");
INSERT INTO barcos(nombre_barco, capitan) VALUES("New Jersey","KWIATKOWSKI");
INSERT INTO barcos(nombre_barco, capitan) VALUES("North Carolina","LANG");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Ramillies", "LEMA");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Renown","PETERSEN");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Repulse", "PODVERSICH CHIAVENNA");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Resolution","ITURRE");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Revenge","LEVIN SACCONE");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Royal Oak","LIRIO");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Royal Sovereign", "SCHROH OLIVERA");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Tennessee","LEVI");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Washington","SERI MEDEI");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Wisconsin", "LASSALLETTE");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Yamato", "LONGONI");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Bismarck", "Ernst Lindeman");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Duke of York", "LANGE");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Fuso", "SEPULVEDA");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Hood", "Lancelot Holland");
INSERT INTO barcos(nombre_barco, capitan) VALUES("HMS King George V", "John Tovey");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Suffolk", "Lancelot Holland");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Prince of Wales", "SMITH");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Rodney", "George Campell Ross");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Scharnhorst", "LATINI");
INSERT INTO barcos(nombre_barco, capitan) VALUES("South Dakota", "PIRO LEW" );
INSERT INTO barcos(nombre_barco, capitan) VALUES("West Virginia", "Thomas J. Senn");
INSERT INTO barcos(nombre_barco, capitan) VALUES("Yamashiro", "ILARRESCAPE");


INSERT INTO barco_clase VALUES("California","Tennessee",1921);
INSERT INTO barco_clase VALUES("Haruna","Kongo",1915);
INSERT INTO barco_clase VALUES("Hiei","Kongo",1914);
INSERT INTO barco_clase VALUES("Iowa","Iowa",1943);
INSERT INTO barco_clase VALUES("Kirishima","Kongo",1915);
INSERT INTO barco_clase VALUES("Kongo","Kongo",1913);
INSERT INTO barco_clase VALUES("Missouri","Iowa",1944);
INSERT INTO barco_clase VALUES("Musashi","Yamato",1942);
INSERT INTO barco_clase VALUES("New Jersey","Iowa",1943);
INSERT INTO barco_clase VALUES("North Carolina","North Carolina",1941);
INSERT INTO barco_clase VALUES("Ramillies","Revenge",1917);
INSERT INTO barco_clase VALUES("Renown","Renown",1916);
INSERT INTO barco_clase VALUES("Repulse","Renown",1916);
INSERT INTO barco_clase VALUES("Resolution","Revenge",1916);
INSERT INTO barco_clase VALUES("Revenge","Revenge",1916);
INSERT INTO barco_clase VALUES("Royal Oak", "Revenge",1916);
INSERT INTO barco_clase VALUES("Royal Sovereign","Revenge",1916);
INSERT INTO barco_clase VALUES("Suffolk","Suffolk",1924);
INSERT INTO barco_clase VALUES("Tennessee","Tennessee",1920);
INSERT INTO barco_clase VALUES("West Virginia","Colorado",1920);
INSERT INTO barco_clase VALUES("Washington","North Carolina",1941);
INSERT INTO barco_clase VALUES("Wisconsin","Iowa",1944);
INSERT INTO barco_clase VALUES("Yamato","Yamato",1941);

INSERT INTO resultados VALUES ("Bismarck","North Atlantic","hundido");
INSERT INTO resultados VALUES ("California","Surigao Strait","ok");
INSERT INTO resultados VALUES ("Duke of York","North Cape","ok");
INSERT INTO resultados VALUES ("Fuso","Surigao Strait","hundido");
INSERT INTO resultados VALUES ("Hood","North Atlantic","hundido");
INSERT INTO resultados VALUES ("Suffolk","Sola Air Station","averiado");
INSERT INTO resultados VALUES ("Suffolk","North Atlantic","ok");
INSERT INTO resultados VALUES ("HMS King George V","Murmansk","averiado");
INSERT INTO resultados VALUES ("HMS King George V","North Atlantic","ok");
INSERT INTO resultados VALUES ("Kirishima","Guadalcanal","hundido");
INSERT INTO resultados VALUES ("Prince of Wales","North Atlantic","averiado");
INSERT INTO resultados VALUES ("Rodney","North Atlantic","ok");
INSERT INTO resultados VALUES ("Scharnhorst","North Cape","hundido");
INSERT INTO resultados VALUES ("South Dakota","Guadalcanal","averiado");
INSERT INTO resultados VALUES ("Tennessee","Surigao Strait","ok");
INSERT INTO resultados VALUES ("Washington","Guadalcanal","ok");
INSERT INTO resultados VALUES ("West Virginia","Surigao Strait","ok");
INSERT INTO resultados VALUES ("Yamashiro","Surigao Strait","hundido");