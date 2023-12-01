/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente_multiple;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author 
 * ALEXANDER EDUARDO CHAVEZ GARCIA 
 * DAVID TADEO ROBLES LARA
 */
public class Cliente_multiple3 {

    /**
     * @param args the command line arguments
     */
     public static String mensaje="";
     public static String duracion="";
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000)) {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);
            String userInput;
            String response;
            // String clientName = "anonymous";
            ClientThread clientThread = new ClientThread(socket, args);
            clientThread.start();
            do {
                response = input.readLine();
                System.out.println(response);
                if (response.contains("(privado):URL")) {
                    String splitted[] = response.split("URL");
                    String url = splitted[1];
                    //procesamos las urls
                    ArrayList<BaseLibros> libros = recibirLibros(url);
                    descargarLibros(libros);
                    //Procesar palabras de los libros
                    //argumentos books  txt  english_sw.txt 
                    //System.out.println(args[0] + " " + args[1] + " " + args[2] );
                    String[] argumentos = {"books3","txt","english_sw.txt "};
                    procesaTextos(argumentos);
                    output.println(mensaje);
                    output.println("privado "+"Cliente3 "+"tiempo "+duracion);
                }
                
                  String message = (" mensaje>:");
                System.out.println(message);
                userInput = scanner.nextLine();
                // output.println(message + " " + userInput);
                output.println(userInput);
                if (userInput.equals("salir")) {
                    break;
                }

            } while (!userInput.equals("salir"));
        } catch (Exception e) {
            System.out.println("Main client. Error:" + e.getMessage());
        }
    }

    public static void procesaTextos(String args[]) {

        Instant inicio = Instant.now();
        HashMap<String, Integer> hasmap = cargaStopPalabras(args[2]);

        List<Texto> lista_textos = cargaTextosSerial(args[0], args[1]);

        procesaListaTextosSerial(lista_textos, hasmap);

        //guardarResultadosSerial(lista_textos, args[3]);
        Instant fin = Instant.now();

        long tiempo = Duration.between(inicio, fin).toMillis();
        System.out.println("Tiempo Serial :" + tiempo);
        //return tiempo;
        duracion=String.valueOf(tiempo);

    }

    //Metodos para procesar textos
    public static HashMap<String, Integer> cargaStopPalabras(String archivo) {
        HashMap<String, Integer> hashwords = new HashMap();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(archivo));
            String linea;
            String texto = "";
            while ((linea = reader.readLine()) != null) {
                hashwords.put(linea, 0);

            }

        } catch (IOException e) {
            System.out.println("Cargando texto :" + e.getMessage());
        }
        return hashwords;
    }

    public static ArrayList<Texto> cargaTextosSerial(String folder, String extension) {
        ArrayList<Texto> lista = new ArrayList<>();
        try {
            List<File> archivos = obtenListaArchivos(folder, extension);
            for (File archivo : archivos) {
                Texto txt = new Texto(archivo.toString());
                txt.cargarTexto();
                lista.add(txt);
            }
        } catch (Exception e) {
            System.out.println("cargaTextosSerial:" + e.getMessage());
        }
        return lista;
    }

    public static void procesaListaTextosSerial(List<Texto> lista, HashMap<String, Integer> stopwords) {

        for (Texto texto : lista) {
            texto.cuentaPalabras();
            texto.display(40);
            mensaje = mensaje +"privado Cliente3 "+texto.totalPalabras() + "\n";
            texto.cuentaPalabrasRepetidas();
            //texto.displayPalabrasRepetidas(texto.diccionario,10);
            texto.cuentaPalabrasStop(stopwords);
            //texto.displayPalabrasRepetidas(texto.diccionario_stopwords, 10);
           // texto.displayPalabrasRepetidas(texto.diccionario_no_stopwords, 10);
            mensaje = mensaje + texto.displayTop(texto.diccionario_no_stopwords, 10);

        }

    }

    public static List<File> obtenListaArchivos(String ruta, String ext) {

        List<File> lista_archivos = new ArrayList<>();
        File directorio = new File(ruta);
        File[] lista = directorio.listFiles();

        for (File archivo : lista) {
            String nombre_archivo = archivo.toString();
            int indice = nombre_archivo.lastIndexOf(".");
            if (indice > 0) {
                String extencion = nombre_archivo.substring(indice + 1);
                if (extencion.equals(ext)) {
                    lista_archivos.add(archivo);
                }
            }

        }

        return lista_archivos;
    }
    //Metodos para descargar los archivos 
    //Metodo solo para el cliente que tiene el archivo de las urls

    public static ArrayList<BaseLibros> cargarULS(String archivo_urls) {
        ArrayList<BaseLibros> base = new ArrayList();
        try {
            System.out.println(archivo_urls);
            base = ReadTextFile(archivo_urls);
        } catch (Exception e) {
            System.out.println("Error Descargar Archivos:" + e);
        }
        return base;
    }

    //metodo para formar los objetos Libros, utilizando las  urls que vienen en forma de string
    public static ArrayList<BaseLibros> recibirLibros(String cadenaUrls) {
        ArrayList<BaseLibros> base = new ArrayList();
        try {
            String Urls[] = cadenaUrls.split(" ");
            for (String Url : Urls) {
                System.out.println(Url);
            }
            String texto[];
            for (String url : Urls) {
                texto = url.split("~");
                BaseLibros libro = new BaseLibros(texto[0], texto[1]);
                base.add(libro);
            }
        } catch (Exception e) {
            System.out.println("Error recibirArchivos " + e);
        }
        return base;
    }
    //metodo que descarga los libros que contiene un ArrayList de Libros y los guarda en la carpeta /books

    public static void descargarLibros(ArrayList<BaseLibros> base) {

        Instant inicio = Instant.now();
        //metodo que descarga y guarda los libros
        processList(base);
        Instant fin = Instant.now();
        System.out.println("archivos descargados  tiepo duracion: 0" + "  " + Duration.between(inicio, fin));

    }

    public static class BaseLibros {

        String titulo;
        String url;

        public BaseLibros(String titulo, String url) {
            this.titulo = titulo;
            this.url = url;
        }

        public void display() {
            System.out.println(this.titulo + "   " + this.url);
        }
    }

    //metodo que recorre la lista para descargar uno por uno
    public static void processList(ArrayList<BaseLibros> lista) {
        for (BaseLibros libro : lista) {
            libro.display();
            String archivo = libro.titulo + ".txt";
            //metodo para  descargar cada libro y guardarlo en la carpeta de /books
            DownloadWebPage(libro.url, archivo);

        }
    }

    public static ArrayList ReadTextFile(String archivo) {
        ArrayList<BaseLibros> array_libros = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(archivo));
            String linea;
            String texto[];
            while ((linea = reader.readLine()) != null) {
                texto = linea.split("~");
                BaseLibros libro = new BaseLibros(texto[0], texto[1]);
                array_libros.add(libro);

            }
            reader.close();
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return array_libros;
    }

    public static void DownloadWebPage(String webpage, String archivo_salida) {
        String ruta = "books3/";
        try {
            URL url = new URL(webpage);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            BufferedWriter writer = new BufferedWriter(new FileWriter(ruta + archivo_salida));
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            reader.close();
            writer.close();
            System.out.println("Successful");
        } catch (MalformedURLException mue) {
            System.out.println("URL malformado: ");
        } catch (IOException ie) {
            System.out.println("Error de entrada/salida: ");
        } catch (Exception e) {
            System.out.println("Ocurrió una excepción: " + e.getMessage());
        }
    }

}

