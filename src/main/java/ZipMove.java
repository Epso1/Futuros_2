import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipMove {
    public static void main(String[] args) {
        // Declarar variables para las rutas
        String path1, path2;

        // Pedir al usuario 2 rutas, una para el archivo a comprimir y otra para mover el archivo comprimido
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduce la ruta del archivo a comprimir:");
        path1 = sc.nextLine();
        System.out.println("Introduce la ruta donde quieres mover el archivo comprimido:");
        path2 = sc.nextLine();


        //Crear el archivo comprimido y moverlo a la ruta indicada
        Future<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                // Crear el archivo comprimido
                String zipName = "MyZipFile.zip";
                FileOutputStream fos = new FileOutputStream(zipName);
                ZipOutputStream zipOut = new ZipOutputStream(fos);

                // Añadir el archivo a comprimir al archivo comprimido
                File fileToZip = new File(path1);
                FileInputStream fis = new FileInputStream(fileToZip);
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);

                // Escribir el archivo a comprimir en el archivo comprimido
                byte[] bytes = new byte[1024];
                int length;

                // Mientras haya bytes que leer, leerlos y escribirlos en el archivo comprimido
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }

                // Cerrar el archivo comprimido y el archivo a comprimir
                zipOut.close();
                fis.close();
                fos.close();
                return zipName;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            // Mover el archivo comprimido a la ruta indicada
        }).whenComplete((response, error) -> {
            try {
                Files.move(Path.of(response), Path.of(path2 + "/" + response), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        // Esperar a que se resuelva la petición
        while (!future.isDone()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}