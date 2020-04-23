# Practica 7 
### Compilar
`javac -classpath ../JDBC/ojdbc8.jar:../JDBC/ucp.jar:. P07_1.java`
`java -classpath ../JDBC/ojdbc8.jar:../JDBC/ucp.jar:. P07_1.java`
### SQL Injection
El codigo para obtener el query es el siguiente:
```
String query = "select price from serves where beer = ";
System.out.println("Query: " + query + "...");
System.out.print("Introduce el campo where: ");
query += sc.nextLine();
```
Se pide al usuario que introduzca un valor para filtrar por el nombre de la columna 'beer'. Una forma muy senzilla de hacer SQL Injection seria introducir algo como `'' or bar = 'The Edge'` lo cual convertiria el query completo a `select price from serves where beer = '' or bar = 'The Edge'`

En lugar de filtrar por 'beer' ahora va a filtrar por la columna 'bar' lo cual no era la intención del programa.
