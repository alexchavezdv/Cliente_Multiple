/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente_multiple;

/**
 *
 * @authors ALEXANDER CHAVEZ, DAVID ROBLES 
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 *
 * @author alexander_chavez
 */
public class Texto {

    String filename;//filename con el path
    String texto;
    int palabras_totales;
    int palabras_repetidas;
    int palabras_stop;
    int palabras_significativas;
    HashMap<String, Integer> diccionario ;
    HashMap<String, Integer> diccionario_stopwords ;
    HashMap<String, Integer> diccionario_no_stopwords ;
    String palabras[];
    ArrayList<String> Top10;
    String fn;//filename sin el path

    public Texto(String filename) {
        this.filename = filename;
        this.texto = "";
        this.palabras_totales = 0;
        this.palabras_repetidas = 0;
        this.palabras_stop = 0;
        this.palabras_significativas = 0;
        this.Top10= new ArrayList();
        File arc=new File(this.filename);
        this.fn=arc.getName();
    }
    public void guardaResultado(String folder){
        try {
            String archivo = folder+"\\"+this.fn;
            System.out.println("archivo salida:"+archivo);
            BufferedWriter writer = new BufferedWriter(
            new FileWriter(archivo));
            
            for (int i = 0; i < 10; i++) {
                String linea = this.Top10.get(i)+'\n';
                writer.write(linea);
            }
            writer.close();
        }  catch (IOException e) {
            System.out.println("guardaResultado:"+e.getMessage());
        }
    
    }
    public void cargarTexto() {
        try {
            
            BufferedReader reader = new BufferedReader(new FileReader(this.filename));
            String linea;
            String texto = "";
            while ((linea = reader.readLine()) != null) {
                texto += linea;
            }
            this.texto = texto;
        } catch (IOException e) {
            System.out.println("Cargando texto :" + e.getMessage());
        }
    }

    public void display(int n) {
      //  System.out.println(this.texto.substring(n, n + n));
        System.out.println("Total de palabras:" + this.palabras_totales);
    }
    public String totalPalabras() {
      //  System.out.println(this.texto.substring(n, n + n));
        
        return "Total de palabras:" + String.valueOf(this.palabras_totales);
    }

    public void cuentaPalabras() {
        this.texto=this.texto.toLowerCase();
        String[] cadenas = this.texto.split("[^a-zA-Z]+");
        this.palabras_totales = cadenas.length;
        this.palabras = cadenas;

    }

    public void cuentaPalabrasRepetidas() {
        HashMap<String, Integer> diccionario = new HashMap();
        for (String palabra : this.palabras) {
            if (diccionario.containsKey(palabra)) {
                int conteo = diccionario.get(palabra);
                diccionario.put(palabra, conteo+1);
            } else {
                diccionario.put(palabra, 1);
            }
        }
        this.palabras_repetidas = diccionario.size();
        
        this.diccionario = diccionario;
        //entrySet().stream().sorted(Map.Entry.comparingByValue())
    }
    public void cuentaPalabrasStop(HashMap<String, Integer> stopwords){
      HashMap<String, Integer> dstop = new HashMap();
      HashMap<String, Integer> dnostop = new HashMap();
      
      for (String palabra : this.palabras) {
            if (stopwords.containsKey(palabra)) {
                if (dstop.containsKey(palabra)) {  //contamos stop words
                    int conteo=dstop.get(palabra);
                    dstop.put(palabra, conteo+1);
                }
                else{
                 dstop.put(palabra,1);
                }
                //int conteo = diccionario.get(palabra);
                //diccionario.put(palabra, conteo+1);
            } else {// contamos no stop words
                
                if (dnostop.containsKey(palabra)) {
                    int conteo=dnostop.get(palabra);
                    dnostop.put(palabra, conteo+1);
                }
                else{
                 dnostop.put(palabra,1);
                }
                //int conteo = diccionario.get(palabra);
                //diccionario.put(palabra, conteo+1);
           
            }
            this.diccionario_stopwords=dstop;
            this.diccionario_no_stopwords=dnostop;
        }
    
    }
    public void displayPalabrasRepetidas(HashMap<String, Integer> diccionario ,int n_palabras) {
       LinkedHashMap<String,Integer> sort_dicc =
                new LinkedHashMap<>();
        //ArrayList<String> palabras = 
        //        new ArrayList<>(this.diccionario.keySet());
        //ArrayList<Integer> conteo =
        //        new ArrayList<>(this.diccionario.values());
        ArrayList<Integer> lista = new ArrayList<>();
        for(Map.Entry<String,Integer> registro: diccionario.entrySet()) {
            lista.add(registro.getValue());          
        }
        Collections.sort(lista, Collections.reverseOrder());
        for(int num : lista) {
            for(Map.Entry<String, Integer> registro : this.diccionario.entrySet() ) {
                if (registro.getValue().equals(num)) {
                    sort_dicc.put(registro.getKey(),num);
                }
            }
        }
        System.out.println(">>>>>>>"+this.filename);
        int cuenta_pal =0;
        ArrayList<String> palabras = 
                new ArrayList<>(sort_dicc.keySet());
        ArrayList<Integer> conteo =
                new ArrayList<>(sort_dicc.values());
        
        for (int i = 0; i < palabras.size(); i++) {
            if (cuenta_pal<n_palabras) {
                if(conteo.get(i)>1) {
                    
                    String palabra=palabras.get(i)+ ":" +
                        conteo.get(i);
                    System.out.println("  "+palabra);
                    cuenta_pal++;
                    this.Top10.add(palabra);
                }
            }
            
        }
    }
    public String displayTop(HashMap<String, Integer> diccionario ,int n_palabras) {
        String mensaje="";
       LinkedHashMap<String,Integer> sort_dicc =
                new LinkedHashMap<>();
        //ArrayList<String> palabras = 
        //        new ArrayList<>(this.diccionario.keySet());
        //ArrayList<Integer> conteo =
        //        new ArrayList<>(this.diccionario.values());
        ArrayList<Integer> lista = new ArrayList<>();
        for(Map.Entry<String,Integer> registro: diccionario.entrySet()) {
            lista.add(registro.getValue());          
        }
        Collections.sort(lista, Collections.reverseOrder());
        for(int num : lista) {
            for(Map.Entry<String, Integer> registro : this.diccionario.entrySet() ) {
                if (registro.getValue().equals(num)) {
                    sort_dicc.put(registro.getKey(),num);
                }
            }
        }
        System.out.println(">>>>>>>"+this.filename);
        mensaje=mensaje+">>>>>>>"+this.filename+"\n";
        int cuenta_pal =0;
        ArrayList<String> palabras = 
                new ArrayList<>(sort_dicc.keySet());
        ArrayList<Integer> conteo =
                new ArrayList<>(sort_dicc.values());
        
        for (int i = 0; i < palabras.size(); i++) {
            if (cuenta_pal<n_palabras) {
                if(conteo.get(i)>1) {
                    
                    String palabra=palabras.get(i)+ ":" +
                        conteo.get(i);
                    System.out.println("  "+palabra);
                    mensaje=mensaje+palabra+"\n";
                    cuenta_pal++;
                   
                   // this.Top10.add(palabra);
                }
            }
            
        }
        return mensaje;
    }
    

}