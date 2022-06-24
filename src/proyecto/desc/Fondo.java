
package proyecto.desc;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.ImageIcon;

public class Fondo extends javax.swing.JPanel{
    public Fondo() {
    this.setSize(1920, 1200); //se selecciona el tamaño del panel
}


    public void paint(Graphics grafico) {
        Dimension height = getSize();


        ImageIcon Img = new ImageIcon(getClass().getResource("/proyecto/imagenes/otra.jpg")); 


        grafico.drawImage(Img.getImage(), 0, 0, height.width, height.height, null);

        setOpaque(false);
        super.paintComponent(grafico);
    }
}

