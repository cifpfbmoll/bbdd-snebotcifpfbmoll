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

            int opcion = 0;
            while (opcion != 4) {
                System.out.println("==== MENU ====");
                System.out.println("\t1) Consulta.");
                System.out.println("\t2) Actualización.");
                System.out.println("\t3) Inserción.");
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
                            int opcion_2 = 0;
                            while (opcion_2 != 3) {
                                System.out.println("==== MENU CONSULTA ====");
                                System.out.println("\t1) Sin clave primaria.");
                                System.out.println("\t2) Clave primaria.");
                                System.out.println("\t3) Salir.");
                                System.out.print("> ");

                                try {
                                    opcion_2 = Integer.parseInt(sc.nextLine());
                                } catch (NumberFormatException e) {
                                    System.out.println("La opcion debe de ser un numero.");
                                    continue;
                                }

                                switch (opcion_2) {
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
                        break;
                    case 2:
                        update();
                        break;
                    case 3:
                        insert();
                        break;
                    case 4:
                        break;
                    default:
                        System.out.println("La opcion " + opcion + " no se reconoce.");
                        break;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage() + " | SQLState: " + e.getSQLState());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage() + " | SQLState: " + e.getSQLState());
            }
        }

        sc.close();
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

    public static void update() throws SQLException, IOException {
        Scanner sc = new Scanner(System.in);
        String update = "update beer set brewer = ? where name = ?";
        PreparedStatement pst = conn.prepareStatement(update, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        System.out.println("Update: " + update);
        System.out.print("Introduce el valor original: ");
        String original = sc.nextLine();
        pst.setString(2, original);
        System.out.print("Introduce el valor nuevo: ");
        String nuevo = sc.nextLine();
        pst.setString(1, nuevo);
        pst.executeQuery();
    }

    public static void insert() throws SQLException, IOException {
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
