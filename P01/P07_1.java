/*
 * ¿Se actualiza la tabla si falla la primera, segunda o tercera sentencia?
 * Si, porque la ultima actualizacion se ejecuta aunque las otras hayan fallado.
 *
 * ¿Y si se ejecuta correctamente las tres primeras sentencias que forman parte de la transacción y falla la última qué ocurre?
 * Se actualiza la tabla con todos los cambios de las tres primeras. Los cambios de la ultima no debido a que falla.
 *
 * ¿Qué ocurre si falla la segunda sentencia? ¿Y si falla la tercera?
 * En caso de que falle la segunda sentencia no se van a guardar los cambios, excepto los de la ultima.
 * En caso de que falle la tercera sentencia solo se van a guardar los cambios de la primera y segunda, pero no la tercera.
 */


import java.util.*;
import java.sql.*;
import java.io.*;

public class P07_1 {
    private static Connection conn = null;
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            String db_url = "jdbc:oracle:thin:sys/Oradoc_db1@localhost:1521:ORCLCDB";
            String user = "sys as sysdba";
            String passwd = "Oradoc_db1";

            conn = DriverManager.getConnection(db_url, user, passwd);

            mainMenu(conn);
        } catch (SQLException e) {
            System.out.println("SQLState: " + e.getSQLState() + " | " + e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("SQLState: " + e.getSQLState() + " | " + e.getMessage());
            }
        }

        sc.close();
    }

    public static void mainMenu(Connection conn) throws SQLException, IOException {
        Scanner sc = new Scanner(System.in);
        int opcion = 0;
        while (opcion != 5) {
            System.out.println("==== MENU ====");
            System.out.println("\t1) Consulta.");
            System.out.println("\t2) Actualización.");
            System.out.println("\t3) Inserción.");
            System.out.println("\t4) Transacciones.");
            System.out.println("\t5) Salir.");
            System.out.print("> ");

            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("La opcion debe de ser un numero.");
                continue;
            }

            switch (opcion) {
                case 1:
                    menuConsulta();
                    break;
                case 2:
                    update();
                    break;
                case 3:
                    insert();
                    break;
                case 4:
                    menuTransacciones();
                    break;
                case 5:
                    break;
                default:
                    System.out.println("La opcion " + opcion + " no se reconoce.");
                    break;
            }
        }
    }

    public static void menuConsulta() throws SQLException, IOException {
        Scanner sc = new Scanner(System.in);
        int opcion = 0;
        while (opcion != 3) {
            System.out.println("==== MENU CONSULTA ====");
            System.out.println("\t1) Sin clave primaria.");
            System.out.println("\t2) Clave primaria.");
            System.out.println("\t3) Salir.");
            System.out.print("> ");

            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("La opcion debe de ser un numero.");
                continue;
            }

            switch (opcion) {
                case 1:
                    consultaSinClavePrimaria();
                    break;
                case 2:
                    consultaClavePrimaria();
                    break;
                case 3:
                    break;
                default:
                    System.out.println("La opcion " + opcion + " no se reconoce.");
                    break;
            }
        }
    }

    public static void menuTransacciones() throws SQLException {
        Scanner sc = new Scanner(System.in);
        int opcion = 0;
        while (opcion != 4) {
            System.out.println("==== MENU TRANSACCIONES ====");
            System.out.println("\t1) Actualizacion simple.");
            System.out.println("\t2) Transaccion 1.");
            System.out.println("\t3) Transaccion 2.");
            System.out.println("\t4) Salir.");
            System.out.print("> ");

            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("La opcion debe de ser un numero.");
                continue;
            }

            switch (opcion) {
                case 1:
                    {
                        boolean autocommit = true;
                        try {
                            autocommit = conn.getAutoCommit();
                            conn.setAutoCommit(false); // sin desactivar el autocommit no podremos hacer commit manual
                            // guardamos el estado de la base de datos
                            conn.commit();
                            update();
                            update();
                            conn.commit();
                        } catch (SQLException e) {
                            System.out.println("SQLState: " + e.getSQLState() + " | " + e.getMessage());
                            // algo ha ido mal, volvemos al punto anterior
                            conn.rollback();
                        } finally {
                            conn.setAutoCommit(autocommit);
                        }
                    }
                    break;
                case 2:
                    {
                        boolean autocommit = true;
                        try {
                            autocommit = conn.getAutoCommit();
                            conn.setAutoCommit(false); // sin desactivar el autocommit no podremos hacer commit manual
                            // guardamos el estado de la base de datos
                            conn.commit();
                            update();
                            update();
                            update();
                            conn.commit();
                        } catch (SQLException e) {
                            System.out.println("SQLState: " + e.getSQLState() + " | " + e.getMessage());
                            // algo ha ido mal, volvemos al punto anterior
                            conn.rollback();
                        } finally {
                            conn.setAutoCommit(autocommit);
                            // realizar el update 4 tanto si alguno ha fallado o no
                            update();
                        }
                    }
                    break;
                case 3:
                    {
                        boolean autocommit = true;
                        Savepoint sp = null;
                        try {
                            autocommit = conn.getAutoCommit();
                            conn.setAutoCommit(false); // sin desactivar el autocommit no podremos hacer commit manual
                            // guardamos el estado de la base de datos
                            conn.commit();
                            update();
                            update();
                            sp = conn.setSavepoint("savepoint");
                            update();
                            conn.commit();
                        } catch (SQLException e) {
                            System.out.println("SQLState: " + e.getSQLState() + " | " + e.getMessage());
                            // algo ha ido mal, volvemos al punto anterior
                            if (sp != null) {
                                conn.rollback(sp);
                            } else {
                                conn.rollback();
                            }
                        } finally {
                            conn.setAutoCommit(autocommit);
                            // realizar el update 4 tanto si alguno ha fallado o no
                            update();
                        }
                    }
                    break;
                case 4:
                    break;
                default:
                    System.out.println("La opcion " + opcion + " no se reconoce.");
                    break;
            }
        }
    }

    public static void consultaSinClavePrimaria() throws SQLException, IOException {
        Scanner sc = new Scanner(System.in);
        String query = "select price from serves where beer = ";
        System.out.println("Query: " + query + "...");
        System.out.print("Introduce el campo where: ");
        query += sc.nextLine();

        Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = st.executeQuery(query);
        String[][] tabla = getData(rs);
        String datos = datosTabla(tabla);
        System.out.println(datos);
        String resultado = "Query: " + query + "\n" + datos;
        GestorArchivos.escribir("resultados.txt", resultado, TipoStream.BUFFER, true);
    }

    public static void consultaClavePrimaria() throws SQLException, IOException {
        Scanner sc = new Scanner(System.in);
        String query = "select * from beer where name = ?";
        PreparedStatement pst = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        System.out.println("Query: " + query);
        System.out.print("Introduce un valor: ");
        String val = sc.nextLine();
        pst.setString(1, val);

        ResultSet rs = pst.executeQuery();
        String[][] tabla = getData(rs);
        String datos = datosTabla(tabla);
        System.out.println(datos);
        String resultado = "Query: " + query + "\n" + datos;
        GestorArchivos.escribir("resultados.txt", resultado, TipoStream.BUFFER, true);
    }

    public static void update() throws SQLException {
        Scanner sc = new Scanner(System.in);
        String update = "update beer set brewer = ? where name = ?";
        PreparedStatement pst = conn.prepareStatement(update, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        System.out.println("Update: " + update);
        System.out.print("Introduce el valor de 'name': ");
        String original = sc.nextLine();
        pst.setString(2, original);
        System.out.print("Introduce el valor nuevo: ");
        String nuevo = sc.nextLine();
        pst.setString(1, nuevo);
        pst.executeUpdate();
        pst.close();
    }

    public static void insert() throws SQLException {
        Scanner sc = new Scanner(System.in);
        String insert = "insert into beer values (?, ?)";
        PreparedStatement pst = conn.prepareStatement(insert, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        System.out.println("Insert: " + insert);
        System.out.print("Introduce el primer valor: ");
        String val_1 = sc.nextLine();
        pst.setString(1, val_1);
        System.out.print("Introduce el segundo valor: ");
        String val_2 = sc.nextLine();
        pst.setString(2, val_2);
        pst.executeQuery();
        pst.close();
    }

    public static String datosTabla(String[][] tabla) {
        String ret = "";

        if (tabla != null) {
            for (int i = 0; i < tabla[0].length; i++) {
                ret += "| ";
                for (int j = 0; j < tabla.length; j++) ret += tabla[j][i] + " | ";
                ret += "\n";
            }
        }

        return ret;
    }

    public static String[][] getData(ResultSet rs) throws SQLException {
        String[][] ret = null;
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();

        // obtener el numero de filas
        rs.last(); // nos posicinamos al ultimo
        int rowCount = rs.getRow(); // el ultimo sera el numero total de filas
        ret = new String[colCount][rowCount + 1]; // rowCount + 1 para poner añadir el nombre de la columna

        for (int i = 1; i <= colCount; i ++) {
            ArrayList<String> arr = new ArrayList<String>();

            // el primer elemento del array va a ser el nombre de la columna
            // despues vienen todos los valores
            String colName = rsmd.getColumnName(i);
            rs.beforeFirst(); // reseteamos el indice
            String[] res = getColumn(rs, i);
            for (String val : res) arr.add(val);
            // hay que tener en cuenta que i empieza con el valor 1
            ret[i - 1] = arr.toArray(new String[arr.size()]);
        }

        return ret;
    }

    public static String[] getColumn(ResultSet rs, int col) throws SQLException {
        ArrayList<String> arr = new ArrayList<String>();
        ResultSetMetaData rsmd = rs.getMetaData();
        String colName = rsmd.getColumnName(col);
        arr.add(colName);

        while (rs.next()) arr.add(rs.getString(colName));

        return arr.toArray(new String[arr.size()]);
    }
}
