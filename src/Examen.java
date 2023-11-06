import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Examen {
    public static void main(String[] args) {
        try (RandomAccessFile r = new RandomAccessFile("","rw")){
            String linea= "";
            String hiddenText = "";

            while ((linea = r.readLine()) != null){
                r.seek(r.getFilePointer() - 10);
                int inicio = r.readInt();
                int fin = r.readInt();
                hiddenText+= linea.substring(inicio, fin);
                r.readChar();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void printDirectoryTree (Path path){
        if (Files.isDirectory(path)){
            try (Stream<Path> pathStream = Files.walk(path,Integer.MAX_VALUE)){
                pathStream.forEach(path1 -> {
                    String tabulacion ="";
                    int indentation = path1.getNameCount();
                    for (int i = 0; i < indentation; i++) {
                        tabulacion="\t";
                    }
                    System.out.println(tabulacion + path1.getFileName().toString());
                });

            } catch (IOException e) {
                System.out.println("HA habido algun problema");
            }
        }else {
            System.out.println("La ruta no es un directorio");
        }
    }

    public static void lecturaEscritura(Path path, Path pathEscribir){
        try {
            List<String> lineas = Files.readAllLines(path);
            List<String> lineasACopiar = new ArrayList<>();
            for (String linea: lineas){
                if (linea.contains("LOG")){
                    int startIndex = linea.indexOf(":");
                    lineasACopiar.add(linea.substring(startIndex+1));
                }
            }
            Files.write(pathEscribir, lineasACopiar, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static  int CopyErrorLogLines(Path origen, Path destino){
        try {
            List <String> lineas = Files.readAllLines(origen);
            List <String> lineadACppiar = new ArrayList<>();
            for (String linea: lineas){
                if (linea.contains("Fatal") || linea.contains("ERROR")){
                    int posAnterior =lineas.indexOf(linea) - 1;
                    lineadACppiar.add(lineas.get(posAnterior));
                    lineadACppiar.add(linea);
                }
            }
            Files.write(destino, lineadACppiar , StandardOpenOption.CREATE,StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int deleteByExtension(Path path, String extension) {
        if (path == null || extension.isEmpty() || Files.isDirectory(path)) {
            return -1;
        }
        AtomicInteger contador = new AtomicInteger();
        try (Stream<Path> pathStream = Files.find(path , Integer.MAX_VALUE,(path1 , attrb) ->
            attrb.isRegularFile() && path1.getFileName().toString().endsWith(extension))){
        pathStream.forEach( path1 -> {
            try {
                Files.delete(path1);
                contador.getAndIncrement();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
