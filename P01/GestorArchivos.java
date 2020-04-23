import java.util.*;
import java.io.*;
import java.nio.charset.UnsupportedCharsetException;

// No quiero que se hagan instancias de esta clase
// Solo se van a usar metodos static
public abstract class GestorArchivos {
    public static void escribir(String ruta, String str, TipoStream tipo, boolean anadir) throws IOException {
        switch (tipo) {
            case BYTE:
                {
                    FileOutputStream fout = null;
                    try {
                        fout = new FileOutputStream(ruta, anadir);
                        for (char c : str.toCharArray()) {
                            fout.write(c);
                        }
                    } finally {
                        if (fout != null) fout.close();
                    }
                    break;
                }
            case CHAR:
                {
                    OutputStreamWriter fout = null;
                    try {
                        fout = new OutputStreamWriter(new FileOutputStream(ruta, anadir), "UTF-8");
                        for (char c : str.toCharArray()) {
                            fout.write(c);
                        }
                    } catch (FileNotFoundException e) {
                        System.out.println("No se encuentra el archivo.");
                    } catch (UnsupportedCharsetException uce) {
                        System.out.println("No se soporta el charset del archivo.");
                    } finally {
                        if (fout != null) fout.close();
                    }
                    break;
                }
            case BUFFER:
                {
                    BufferedOutputStream fout = null;
                    try {
                        fout = new BufferedOutputStream(new FileOutputStream(ruta, anadir));
                        for (char c : str.toCharArray()) {
                            fout.write(c);
                        }
                    } finally {
                        if (fout != null) fout.close();
                    }
                    break;
                }
            default:
                System.out.println("No se reconoce el tipo de stream.");
                break;
        }
    }
}
