import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class FileSearcher {
    private static final int NUM_THREADS = 10; // cantidad de hilos fija

    public static void main(String[] args) {
        String directorioRaiz = "C:\\Users\\Cande\\OneDrive\\Escritorio\\AUS\\"; // directorio donde inicia la búsqueda
        String terminoBusqueda = "alkf"; // el termino que va a buscar en los nombres de los archivos

        // Crea un executor de hilos con un pool fijo de 10 hilos
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        try {
            // iniciar la búsqueda y obtener los resultados
            List<String> archivosEncontrados = buscarArchivos(executor, new File(directorioRaiz), terminoBusqueda);

            for (String archivo : archivosEncontrados) {
                System.out.println(archivo);
            }
        } finally {
            // Asegura que todos los hilos terminen su ejecución
            executor.shutdown();
        }
    }

    // Método que envía una tarea al executor para buscar archivos en el directorio
    private static List<String> buscarArchivos(ExecutorService executor, File directorio, String terminoBusqueda) {
        List<String> archivosEncontrados = new ArrayList<>();

        // Crea una tarea para buscar en el directorio actual
        Callable<List<String>> tarea = () -> buscarEnDirectorio(directorio, terminoBusqueda);

        try {
            // Enviar la tarea al executor y obtener el resultado
            Future<List<String>> resultado = executor.submit(tarea);

            // Agregar los resultados al listado final
            archivosEncontrados.addAll(resultado.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // Si no se encontraron archivos con el termino, imprime:
        if (archivosEncontrados.isEmpty()) {
            System.out.println("No se encontraron archivos que coincidan con el término de búsqueda: " + terminoBusqueda);
        }

        return archivosEncontrados;
    }

    // busca recursivamente archivos en el directorio y sus subdirectorios
    private static List<String> buscarEnDirectorio(File directorio, String terminoBusqueda) {
        List<String> archivosEncontrados = new ArrayList<>();

        // Verifica si el archivo actual es un directorio
        if (directorio.isDirectory()) {
            // obtiene todos los archivos y subdirectorios del directorio actual
            File[] archivos = directorio.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    // Si el archivo es un directorio, llama recursivamente
                    if (archivo.isDirectory()) {
                        archivosEncontrados.addAll(buscarEnDirectorio(archivo, terminoBusqueda));
                    } 
                    // Si el archivo no es un directorio, verifica si tiene el término de búsqueda
                    else if (archivo.getName().toLowerCase().contains(terminoBusqueda.toLowerCase())) {
                        archivosEncontrados.add(archivo.getAbsolutePath());
                    }
                }
            }
        }

        return archivosEncontrados;
    }
}
