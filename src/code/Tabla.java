package code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Tabla {
    public String nombreTabla;
    public ArrayList<Campo> listaCampos = new ArrayList<Campo>();
    boolean error=false;
    public String mensajeDeCarga="";
    public Tabla(){     //Constructor
        nombreTabla = "";
        listaCampos.clear();
    }
    
    public void llenarTabla(String nombre){
        String datos = cargarArchivo(nombre);
        if(datos.equals("")){
            mensajeDeCarga = "Archivo vacío";
            return;
        }
        else if(datos.equals("null")){
            mensajeDeCarga = "Archivo no encontrado";
            return;
        }
        else{
            llenarCampos(datos);
            if(error==false)
                llenarDatos(datos);
            else{
                mensajeDeCarga = "No se pudo llenar la tabla";
                nombreTabla = "";
                listaCampos.clear();
            }
            
        }
        
    }
    
    public String cargarArchivo(String ruta){
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        String cad="";

        try {
            archivo = new File(ruta);
            fr = new FileReader (archivo);
            br = new BufferedReader(fr);

            // Lectura del fichero
            String linea="";
            while((linea=br.readLine())!=null)
                cad += linea + "\n";
        }
        catch(Exception e){
           e.printStackTrace();
           return "null";
        }finally{
           try{                    
              if( null != fr ){   
                 fr.close();     
              }                  
           }catch (Exception e2){ 
              e2.printStackTrace();
           }
        }
        
        cad = cad.trim();   //Elimina espacios, ej=___un_mensaje___-->un_mensaje
        return cad;
    }
    
    public void llenarCampos(String datos){
        
        /*Explicación de la función:
        La primera palabra es el nombre de la tabla, después el nombre de un campo
        seguido de 2 carácteres numéricos, hay que verificar que tenga la sintaxis
        correcta*/
        /*Ejemplo:
        departments,department_id,0,3,department_name,4,33,manager_id,34,39*/
        
        String pParrafo="";     /*Esta parte obtiene la cabecera del descriptor de archivos*/
            for (int i=0; i<datos.length(); i++){
                if(datos.charAt(i)!='\n')  //Si el carácter leído es diferente a un salto de línea
                    pParrafo +=datos.charAt(i);    //Se concatena en la cadena del párrafo
                else
                    break;      //Cuando encuentre un salto de línea termina
            }
        
        pParrafo = pParrafo.toUpperCase(); //Convierte a mayúsculas
        
        for(int i=0; i<pParrafo.length(); i++){
            if(pParrafo.charAt(i)!=',')
                nombreTabla += pParrafo.charAt(i);    //Asigna el nombre a la tabla
            else{
                pParrafo = pParrafo.substring(i+1, pParrafo.length());  //Recorta la cadena para quitar el nombre de la tabla
                break;
            }
        }
        
        /*En este punto la sintaxis debe ser: nombre,rangoA,rangoB,nombre2,rangoA,rangoB*/
        pParrafo += ",";    //Se concatena una coma que ayuda a leer mas fácil
        String palabra="";
        int cont=0;
        Campo camp = new Campo();
        for(int i=0; i<pParrafo.length(); i++){
            if (pParrafo.charAt(i)!=','){
                palabra += pParrafo.charAt(i);
            }else{
                palabra = palabra.trim();
                if(palabra.equals("")){     //Si está mal escrito, ejemplo: department_id, ,10
                    System.out.println("Fichero mal escrito");
                    error = true;
                    return;
                }
                /*Ahora sólo se debe validar que después de un nombre haya 2 números de rango*/
                /*campo, a, b*/
                /*en un arreglo campo = 0, a = 1, b = 2*/
                if(cont==0)
                    camp.nombre = palabra;
                else if(cont>0){
                    try{
                        int num = Integer.parseInt(palabra);   //verifica que sea un número
                        if(cont==1)
                            camp.pos1 = num;
                        else
                            camp.pos2 = num;
                    }catch(Exception e){
                        System.out.println("Número no válido");
                        e.printStackTrace();
                        error = true;
                        return;
                    }
                }
                cont++;
                if(cont>2){
                    listaCampos.add(camp);  //Añade a la lista los campos
                    cont=0;         //Resetea contador
                    camp = new Campo(); //Resetea el campo auxiliar
                }
                    
                palabra=""; //Limpiar cadena
            }
            
            
        }
        
    }
    
    public void llenarDatos(String datos){
        /*La estructura de los datos es más o menos*/
        /*-caracteres del campo1- -caracteres del campo2- -caracteres...-*/
        /*Para saber cuántos carácteres ocupa cada campo se acude a los mismos campos*/
        
        /*Primero se recorta la cadena para tener sólo los datos*/
        for(int i=0; i<datos.length(); i++){
            if(datos.charAt(i)=='\n'){
                datos = datos.substring(i+1, datos.length());       //Recorta para tener sólo los datos
                break;
            }
        }
        
        /*Llenar los datos*/
        String[] linea = datos.split("\n");        /*Separa en cada renglón las cadenas*/
        
        for(int i=0; i<linea.length; i++){
            for(Campo c : listaCampos){
                String cad = linea[i].substring(c.pos1, c.pos2+1);
                
                cad = cad.trim();   /*quitar espacios innecesarios*/
                if(cad.equals(""))
                    c.itemList.add("NULL");
                else
                    c.itemList.add(cad);
            }
            
        }
        
        mensajeDeCarga = "Datos cargados correctamente";
        
    }
}