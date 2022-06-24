package code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.swing.table.DefaultTableModel;

public class Instrucciones {
    public String[][] datosTabla;
    public String[] camposTabla;
    public String[][] datosTablaProyeccion;
    public String[] camposTablaProyecion;
    public String[][] datosTablaSeleccion;
    public String[] camposTablaSeleccion;
    public String[][] datosCartesiano;
    public String[] camposCartesiano;
    
    public boolean datosCorrectos=false;
    public String mensaje="";
    boolean banIgual = false;
    public boolean cartesiano = false;
    boolean imprimeTodo=false, where=false;
    public Instrucciones(){}
    
    public void ejecutar(String entrada, Tabla[] t){
        entrada = entrada.trim();
        
        if(entrada.equals("")){                                                 //validaciones
            mensaje = "No se ha encontrado una consulta";
            datosCorrectos = false;
            return;
        }
        //Recortar ;
        int var = entrada.indexOf(";");
        if(var==-1){           //No encontró ;
            mensaje = "Verifique sus instrucciones";
            datosCorrectos = false;
            return;
        }
        entrada = entrada.substring(0, var);
        var = entrada.indexOf("=");
        if(var!=-1){         //Si tiene un igual lo parte
            String[] cad = entrada.split("=");
            if(cad.length!=2){
                mensaje = "Verifique sus instrucciones";
                datosCorrectos = false;
                return;
            }
            cad[0] = cad[0].toUpperCase();
            entrada = cad[0] + " = " + cad[1];
            banIgual = true;
        }
        else
            entrada = entrada.toUpperCase();
        if(banIgual!=entrada.contains("WHERE")){
            mensaje = "Verifique sus instrucciones";
            datosCorrectos = false;
            return;
        }
        entrada = darFormato(entrada);  //Entrada->"esta    \nes  \n   una cadena" Salida-> "esta es una cadena"
        
        //Saber si es cartesiano
        
        if(entrada.contains(" FROM ")){                                         //Obtiene arrFrom
            int cf = entrada.indexOf(" FROM ");
            int cw = -1;
            String datosFrom = "";
            if(entrada.contains(" WHERE ")){
                cw = entrada.indexOf(" WHERE ");
                where = true;
            }
            else
                cw = entrada.length();
            datosFrom = entrada.substring(cf+6, cw);
            
            String[] arrFrom = datosFrom.split(",");                            //Elementos del from
            switch (arrFrom.length){                                            //Se sabe si es o no producto cartesiano
                case 1: cartesiano = false;
                    break;
                case 2: cartesiano = true;
                    break;
                default:
                    mensaje = "Verifique sus instrucciones";
                    datosCorrectos = false;
                    return;
            }
            String[] arrSelect;
            if(entrada.contains("SELECT ")){                                    //Obtiene el arrSelect
                String datosSelect = "";
                datosSelect = entrada.substring(7, cf);
                arrSelect = datosSelect.split(",");
                for(int i=0; i<arrSelect.length; i++)
                    arrSelect[i] = arrSelect[i].trim();
                
            }else{
                mensaje = "Verifique sus instrucciones";
                datosCorrectos = false;
                return;
            }
            
            String[] arrWhere = new String[2];
            int cl=0;
            if(where){
                String datosWhere = entrada.substring(cw+7, entrada.length());
                arrWhere = datosWhere.split("=");
                if(arrWhere.length!=2){
                    mensaje = "Verifique sus instrucciones";
                    datosCorrectos = false;
                    return;
                }
                arrWhere[0] = arrWhere[0].trim();
                arrWhere[1] = arrWhere[1].trim();
            }
            //Tabla que se va a mandar a imprimir
            if(!cartesiano){
                int nt=0;
                for(nt=0; nt<t.length; nt++)                                   //Obtiene la tabla que
                    if(arrFrom[0].equals(t[nt].nombreTabla))                   //Manda a la función
                        break;
                
                funcionSelecccionNormal(t[nt], arrWhere, arrSelect);
            }else{
                arrFrom[1] = arrFrom[1].toUpperCase();
                funcionSeleccionCartesiano(t[0], t[1], arrWhere, arrSelect);
                
            }
            
            
            
        }else{
            mensaje = "Verifique sus instrucciones";
            datosCorrectos = false;
            return;
        }
        
    }
    
    
    public void funcionSelecccionNormal(Tabla t, String[] arrWhere, String[] arrSelect){
        if(where){
            boolean e1=false, e2=false;
            int var = 0;
            ArrayList<Integer> numSeleccionados = new ArrayList<>();
            for(var=0; var<t.listaCampos.size(); var++)                           //Busca el campo a comparar
                if(arrWhere[0].equals(t.listaCampos.get(var).nombre)){
                    e1 = true;
                    break;
                }
            if(e1){                                                             //Si lo encontró comienza a comparar
                for(int i=0; i<t.listaCampos.get(var).itemList.size(); i++){
                    if(arrWhere[1].equals(t.listaCampos.get(var).itemList.get(i)))
                        numSeleccionados.add(i);                                //Va listando las coincidencias
                }
            }
            
            ArrayList<Campo> listaSeleccion = new ArrayList<>();
            for(Campo c : t.listaCampos){               //Agrega los nombres de los campos
                Campo c2 = new Campo(c.nombre, 0, 0);
                listaSeleccion.add(c2);
            }
            for(int num : numSeleccionados){
                int cont=0;
                for(Campo c : t.listaCampos){
                    listaSeleccion.get(cont).itemList.add(c.itemList.get(num));
                    cont++;
                }
            }
            
            int numColumnas=listaSeleccion.size();
            int numFilas=listaSeleccion.get(0).itemList.size();
            String matriz[][] = new String[numFilas][numColumnas];

            for(int i=0; i<numFilas; i++)          //Llena la matriz de la tabla
                for(int j=0; j<numColumnas; j++)
                    matriz[i][j] = listaSeleccion.get(j).itemList.get(i);
            
            datosTablaSeleccion = new String[numFilas+1][numColumnas+1];
            datosTablaSeleccion = matriz;

            String[] aux = new String[numColumnas];

            int cont3=0;
            for(Campo c : t.listaCampos){
                aux[cont3] = c.nombre;
                cont3++;
            }
            camposTablaSeleccion = new String[aux.length];
            camposTablaSeleccion = aux;
            
            
            
            funcionProyeccionNormal(listaSeleccion, arrSelect);
            //datosCorrectos = true;
        }else{                                                                  //Si no hay where
            ArrayList<Campo> listaSeleccion = new ArrayList<>();
            for(Campo c : t.listaCampos){               //Agrega los nombres de los campos
                Campo c2 = new Campo(c.nombre, 0, 0);
                listaSeleccion.add(c2);
            }
            for(int i=0; i<t.listaCampos.get(0).itemList.size(); i++){
                int cont=0;
                for(Campo c : t.listaCampos){
                    listaSeleccion.get(cont).itemList.add(c.itemList.get(i));
                    cont++;
                }
            }
            
            int numColumnas=listaSeleccion.size();
            int numFilas=listaSeleccion.get(0).itemList.size();
            String matriz[][] = new String[numFilas][numColumnas];

            for(int i=0; i<numFilas; i++)          //Llena la matriz de la tabla
                for(int j=0; j<numColumnas; j++)
                    matriz[i][j] = listaSeleccion.get(j).itemList.get(i);
            
            datosTablaSeleccion = new String[numFilas+1][numColumnas+1];
            datosTablaSeleccion = matriz;

            String[] aux = new String[numColumnas];

            int cont3=0;
            for(Campo c : t.listaCampos){
                aux[cont3] = c.nombre;
                cont3++;
            }
            camposTablaSeleccion = new String[aux.length];
            camposTablaSeleccion = aux;
            
            funcionProyeccionNormal(listaSeleccion, arrSelect);
        }
    }
    
    public void funcionSeleccionCartesiano(Tabla t1, Tabla t2, String[] arrWhere, String[] arrSelect){
        String[] aux1 = new String[t1.listaCampos.size()];       //Pasa los nombres de los campos a Arrays de String
        String[] aux2 = new String[t2.listaCampos.size()];
        for(int i=0; i<t1.listaCampos.size(); i++)               //De la tabla 1
            aux1[i] = t1.listaCampos.get(i).nombre;
        for(int i=0; i<t2.listaCampos.size(); i++)               //De la tabla 2
            aux2[i] = t2.listaCampos.get(i).nombre;

        for (int i=0; i<aux1.length; i++) {                         //Si 2 campos de una tabla se llaman igual
            for (int j=0; j<aux2.length; j++) {
                if(aux1[i].equals(aux2[j])){
                    aux1[i] = t1.nombreTabla +"." +aux1[i];      //Renombra a tabla1.campo
                    aux2[j] = t2.nombreTabla +"." +aux2[j];      //Renombra a tabla2.campo
                    break;
                }
            }
        }

        ArrayList<Campo> listaProyeccion = new ArrayList<Campo>();  //Agrega a una nueva lista los campos a proyectar
        for(String c : aux1)
            listaProyeccion.add(new Campo(c, 0, 0));
        for(String c : aux2)
            listaProyeccion.add(new Campo(c, 0, 0));

        int var=0;
        for(int i=0; i<t1.listaCampos.get(0).itemList.size(); i++){  //Recorre las tuplas de t1
            for(int j=0; j<t2.listaCampos.get(0).itemList.size(); j++){  //Recorre las tuplas de t2
                var = 0;
                for(Campo c : t1.listaCampos){                           //Pasa elementos de t1 a la listaProyeccion
                    Campo aux = listaProyeccion.get(var);
                    aux.itemList.add(c.itemList.get(i));
                    var++;
                }
                for(Campo c : t2.listaCampos){                          //Pasa elementos de t2 a la listaProyeccion
                    Campo aux = listaProyeccion.get(var);
                    aux.itemList.add(c.itemList.get(j));
                    var++;
                }
            }         
        }

        int numColumnas=listaProyeccion.size();
        int numFilas=listaProyeccion.get(0).itemList.size();
        String matriz[][] = new String[numFilas][numColumnas];

        for(int i=0; i<numFilas; i++)                               //Llena la matriz de la tabla
            for(int j=0; j<numColumnas; j++)
                matriz[i][j] = listaProyeccion.get(j).itemList.get(i);
        /*
        for(int i=0; i<numFilas; i++){                              //imprime la matriz de la tabla
            for(int j=0; j<numColumnas; j++)
                System.out.print(matriz[i][j] +"\t");
            System.out.println("");
        }*/
        
        datosTablaSeleccion = new String[numFilas+1][numColumnas+1];
        datosTablaSeleccion = matriz;
        datosCartesiano = new String[numFilas+1][numColumnas+1];
        datosCartesiano = matriz;
        
        String[] aux = new String[numColumnas];
        
        int cont=0;
        for(Campo c : listaProyeccion){
            aux[cont] = c.nombre;
            cont++;
        }/*
        for(int i=0; i<aux.length; i++){                            //Imprime los campos de la tabla
            System.out.println(aux[i]);
        }*/
        camposTablaSeleccion = new String[aux.length];
        camposTablaSeleccion = aux;
        camposCartesiano = new String[aux.length];
        camposCartesiano = aux;
        
        ArrayList<Campo> listaCartesiana = new ArrayList<>();
        for(int i=0; i<aux.length; i++){
            listaCartesiana.add(new Campo(aux[i], 0, 0));
            for(int j=0;j<matriz.length; j++){
                Campo c = listaCartesiana.get(i);
                c.itemList.add(matriz[j][i]);
            }
        }
            
        /////////////   Todo lo anterior sirve para sacar el cartesiano/////////////
        if(!where)                                                              //Si no hay where
            funcionProyeccionNormal(listaProyeccion, arrSelect);
        else{                                                                   //Si hay where
            if(!arrWhere[0].contains(".") || !arrWhere[1].contains(".")){                        //En el where no tiene .
                mensaje = "Favor de especificar: \"WHERE Tabla1.Campo = Tabla2.Campo\"";
                datosCorrectos = false;
                return;
            }else{                                                  //En el where hay .
                arrWhere[1] = arrWhere[1].toUpperCase();
                boolean b1=false, b2=false;                                 //Banderas para saber si se encontraron las tablas
                String eq0="", eq1="";                                      //Verifica que las tablas en where existan
                                                       
                eq0 = arrWhere[0].substring(0, arrWhere[0].indexOf("."));
                eq1 = arrWhere[1].substring(0, arrWhere[1].indexOf("."));
                if(eq0.equals(t1.nombreTabla)){     //La tabla izquierda del where existe
                   arrWhere[0] = arrWhere[0].substring(arrWhere[0].indexOf(".")+1, arrWhere[0].length());
                   b1=true;
                }else if(eq0.equals(t2.nombreTabla)){     //La tabla izquierda del where existe en la tabla 2
                    b1=true;
                }else{
                    mensaje = "No se encontró la tabla:"+eq0;
                    datosCorrectos = false;
                    return;
                }

                if(eq1.equals(t1.nombreTabla)){     //La tabla derecha del where existe
                   arrWhere[1] = arrWhere[1].substring(arrWhere[1].indexOf(".")+1, arrWhere[1].length());
                   b2=true;
                }else if(eq1.equals(t2.nombreTabla)){     //La tabla izquierda del where existe en la tabla 2
                    b2=true;
                }else{
                    mensaje = "No se encontró la tabla:"+eq1;
                    datosCorrectos = false;
                    return;
                }
                   
                if(!b1 || !b2){
                    mensaje = "Error al encontrar las tablas\nVerifique: \"WHERE Tabla1.Campo = Tabla2.Campo\"";
                    datosCorrectos = false;
                    return;
                }
                if(!eq0.equals(eq1)){                                       //El where es correcto: where tabla1.x = tabla2.x
                    
                    int encabezado1=0, encabezado2=0;
                    String cad=eq0+"."+arrWhere[0];
                    for(encabezado1=0; encabezado1<camposTablaSeleccion.length; encabezado1++){             //Obtiene el [][] para comparar
                        if(camposTablaSeleccion[encabezado1].equals(arrWhere[0]) || camposTablaSeleccion[encabezado1].equals(cad)){
                            break;
                        }
                    }
                    cad = eq1+"."+arrWhere[1];
                    for(encabezado2=0; encabezado2<camposTablaSeleccion.length; encabezado2++){             //Obtiene el [][] para comparar
                        if(camposTablaSeleccion[encabezado2].equals(arrWhere[1]) || camposTablaSeleccion[encabezado2].equals(cad)){
                            break;
                        }
                    }
                    /*
                    System.out.println("Columnas que se van a comparar");
                    for(int i=0; i<numFilas; i++)             
                        System.out.print(datosTablaSeleccion[i][encabezado1] +" "+datosTablaSeleccion[i][encabezado2]+"\n");
                    */
                    
                    ArrayList<Integer> numSeleccionados = new ArrayList<>();
                    for(int i=0; i<numFilas; i++){                                                          //Compara los datos
                        if(datosTablaSeleccion[i][encabezado1].equals(datosTablaSeleccion[i][encabezado2])){
                            numSeleccionados.add(i);                                                        //Los guarda en una lista
                        }
                    }
                    //System.out.println(numSeleccionados.size());
                    
                    
                    matriz = new String[numSeleccionados.size()][numColumnas];    //Llena la matriz
                    for(int i=0; i<numSeleccionados.size(); i++){                        
                        for(int j=0; j<numColumnas; j++){
                            matriz[i][j] = datosTablaSeleccion[numSeleccionados.get(i)][j];
                        }
                    }
                    /*
                    for(int i=0; i<numSeleccionados.size(); i++){                        //Imprime en consola la tabla del where terminada
                        for(int j=0; j<numColumnas; j++){
                            System.out.print(matriz[i][j]+" ");
                        }System.out.println("");
                    }
                    */
                    datosTablaSeleccion = new String[numFilas+1][numColumnas+1];
                    datosTablaSeleccion = matriz;

                    
                    listaProyeccion = new ArrayList<>();
                    for(int i=0; i<camposTablaSeleccion.length; i++){
                        listaProyeccion.add(new Campo(camposTablaSeleccion[i], 0, 0));
                    }
                    for(int i=0; i<numSeleccionados.size(); i++){                        //Imprime en consola la tabla del where terminada
                        for(int j=0; j<numColumnas; j++){
                            listaProyeccion.get(j).itemList.add(matriz[i][j]);
                        }
                    }
                    
                    funcionProyeccionCartesiano(arrSelect, t1, t2);


                }else{
                    mensaje = "Verifique sus instrucciones: \"WHERE Tabla1.Campo = Tabla2.Campo\"";
                    datosCorrectos = false;
                    return;
                }

                

            }

        }
    }
    
    public void funcionProyeccionNormal(ArrayList<Campo> listaSeleccion, String[] arrSelect){
        boolean camposCorrectos=false;
        int numColumnas;
        int numFilas;

        String matriz[][];
        for(int i=0; i<arrSelect.length; i++){                          //Buscar los campos
            camposCorrectos = false;                                    //Si alguno no se encuentra manda un aviso
            for(Campo c : listaSeleccion){
                if(arrSelect[i].equals(c.nombre))
                    camposCorrectos = true;
            }
        }
        
        if(camposCorrectos){
            ArrayList<Campo> listaProyeccion = new ArrayList<Campo>();  //Agrega a una nueva lista los campos a proyectar
            for (String s : arrSelect) 
                for(Campo c : listaSeleccion)
                    if (s.equals(c.nombre)) 
                        listaProyeccion.add(c);
            
            numColumnas=listaProyeccion.size();
            numFilas=listaProyeccion.get(0).itemList.size();

            matriz = new String[numFilas][numColumnas];
            
            for(int i=0; i<listaProyeccion.get(0).itemList.size(); i++)          //Llena la matriz de la tabla
                for(int j=0; j<listaProyeccion.size(); j++)
                    matriz[i][j] = listaProyeccion.get(j).itemList.get(i);
            
            /*for(int i=0; i<numFilas; i++){          //Imprime matriz de tabla
                for(int j=0; j<numColumnas; j++)
                    System.out.print(matriz[i][j]+" ");
                System.out.println("");
            }*/
            datosTablaProyeccion = new String[numFilas+1][numColumnas+1];
            datosTablaProyeccion = matriz;

            String[] aux = new String[numColumnas];

            int cont=0;
            for(Campo c : listaProyeccion){
                aux[cont] = c.nombre;
                cont++;
            }

            camposTablaProyecion = new String[aux.length];
            camposTablaProyecion = aux;
            datosCorrectos = true;
        }else{
            mensaje = "Error al encontrar los campos";
            datosCorrectos = false;
            return;
        }
    
    }
    
    public void funcionProyeccionCartesiano(String[] arrSelect, Tabla t1, Tabla t2){
        for(int i=0; i<arrSelect.length; i++){
            if(!arrSelect[i].contains(".")){
                mensaje = "Verifique sus instrucciones: \"SELECT Tabla1.Campo, Tabla2.Campo\"";
                    datosCorrectos = false;
                    return;
            }
        }
        
        String ts0="", ts1="";
        String[] encabezadosTemporales = new String[camposTablaSeleccion.length];
        int var = 0, var2 = 0;
        for(int i=0; i<camposTablaSeleccion.length; i++){
            if(i<t1.listaCampos.size()){
                encabezadosTemporales[i] = t1.nombreTabla + "." + t1.listaCampos.get(var).nombre;
                var++;
            }else{
                encabezadosTemporales[i] = t2.nombreTabla + "." + t2.listaCampos.get(var2).nombre;
                var2++;
            }
        }
        /*
        System.out.println("Encabezados temporales (Todos pero con tabla.x)");
        for(int i=0; i<encabezadosTemporales.length; i++){
            System.out.println(encabezadosTemporales[i]);
        }*/
        int c1, c2;
        boolean b1=false, b2=false;
        ArrayList<Integer> numSeleccionados = new ArrayList<>();
        for(int i=0; i<arrSelect.length; i++){
            b1=false;
            b2=false;
            String prefijoS = arrSelect[i].substring(0, arrSelect[i].indexOf(".")+1);    //Obtiene el prefijo tabla. de arrSelect
            String prefijoT1 = t1.nombreTabla+".";    //Obtiene el prefijo tabla. de t1
            String prefijoT2 = t2.nombreTabla+".";    //Obtiene el prefijo tabla. de t2
            if(prefijoS.equals(prefijoT1)){
                for(c1=0; c1<t1.listaCampos.size(); c1++){
                    if(arrSelect[i].equals(encabezadosTemporales[c1])){
                        numSeleccionados.add(c1);                                            //Si lo encuentra guarda la pos y sale
                        b1 = true;
                        break;
                    }
                }
                if(!b1){
                    mensaje = "No se encontró "+arrSelect[i]+"\nVerifique: SELECT tabla1.x, tabla2.x";
                    datosCorrectos = false;
                    return;
                }
            }else if(prefijoS.equals(prefijoT2)){
                for(c2=t1.listaCampos.size(); c2<encabezadosTemporales.length; c2++){
                    if(arrSelect[i].equals(encabezadosTemporales[c2])){
                        numSeleccionados.add(c2);                                            //Si lo encuentra guarda la pos y sale
                        b2 = true;
                        break;
                    }
                }
                if(!b2){
                    mensaje = "No se encontró "+arrSelect[i]+"\nVerifique: SELECT tabla1.x, tabla2.x";
                    datosCorrectos = false;
                    return;
                }
            }else{
                mensaje = "No se encontró "+arrSelect[i]+"\nVerifique: SELECT tabla1.x, tabla2.x";
                datosCorrectos = false;
                return;
            }
            
        }
        
        
        int numFilas = datosTablaSeleccion.length;
        int numColumnas = numSeleccionados.size();
        datosTablaProyeccion = new String[numFilas][numColumnas];
        camposTablaProyecion = new String[numColumnas];
        
        for(int i=0; i<numSeleccionados.size(); i++){                           //Llena la matriz
            for(int j=0; j<numColumnas; j++)
                datosTablaProyeccion[i][j] = (datosTablaSeleccion[i][numSeleccionados.get(j)]);
        }        
        /*
        for(int i=0; i<numFilas; i++){                                          //Imprime la matriz
            for(int j=0; j<numColumnas; j++){
                System.out.print(datosTablaProyeccion[i][j]+" ");
            }
            System.out.println("");
        }*/
        
        var = 0;
        for(int i=0; i<numSeleccionados.size(); i++){
            camposTablaProyecion[var] = camposTablaSeleccion[numSeleccionados.get(i)];
            var++;
        }
        
        datosCorrectos = true;
        
        
    }
    
    public void imprimirTabla(Tabla t){
        int numColumnas=t.listaCampos.size();
        int numFilas=t.listaCampos.get(0).itemList.size();
        //matriz = new String[numFilas][numColumnas];
        datosTabla = new String[numFilas][numColumnas];
        
        for(int i=0; i<numFilas; i++)          //Llena la matriz de la tabla
            for(int j=0; j<numColumnas; j++)
                datosTabla[i][j] = t.listaCampos.get(j).itemList.get(i);

        camposTabla = new String[numColumnas];

        int cont=0;
        for(Campo c : t.listaCampos){
            camposTabla[cont] = c.nombre;
            cont++;
        }
    }
    
    
    public String darFormato(String cad){   //Entrada->"esta    \nes  \n   una    cadena"
        String cadFormateada = "";          //Salida-> "esta es una cadena"
        String cadAux = "";
        String[] temp = cad.split("\n");            //Primero se eliminan saltos de línea '\n'
        for (int i = 0; i < temp.length; i++){
            temp[i] = temp[i].trim();
            cadAux += " " +temp[i];
        }
        for(int i=0; i<cadAux.length(); i++){       //Elimina espacios y tabuladores innecesarios
            if(cadAux.charAt(i)!=' ')                   
                cadFormateada += cadAux.charAt(i);      
            else{
                cadFormateada += ' ';
                if(i+1 < cadAux.length()){
                    while(cadAux.charAt(i+1)==' ' || cadAux.charAt(i+1)=='\t')
                        i++;
                }
            }
        }
        cadFormateada = cadFormateada.trim();
        return cadFormateada;
    }
    
    
    
}
