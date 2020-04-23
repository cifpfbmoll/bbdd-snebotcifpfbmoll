# Practica 7 
### SQL Injection
El codigo para obtener el query es el siguiente:
```
String query = "select price from serves where beer = ";
System.out.println("Query: " + query + "...");
System.out.print("Introduce el campo where: ");
query += sc.nextLine();
```
Se pide al usuario que introduzca un valor para filtrar por el nombre de la columna 'beer'. Una forma muy senzilla de hacer SQL Injection seria introducir algo como `'' or bar = 'The Edge'` lo cual convertiria el query por nombre de la columna 'beer' por la columna 'bar'.
