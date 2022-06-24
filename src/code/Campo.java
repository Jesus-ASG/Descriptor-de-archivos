package code;

import java.util.ArrayList;

public class Campo {
    String nombre;
    int pos1;
    int pos2;
    ArrayList<String> itemList = new ArrayList<String>();
    
    public Campo (){
        nombre="";
        pos1 = pos2 = 0;
        itemList.clear();
    }
    
    public Campo (String nombre, int pos1, int pos2){
        this.nombre = nombre;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }
    
    @Override
    public String toString(){
        String cad = "Campo: "+this.nombre+"\nPos1: "+pos1+"\tPos2: "+pos2;
        String cad2 = "\n";
        for(String s : itemList){
            cad2 += s + "\n";
        }
        return cad + cad2;
    }
}
