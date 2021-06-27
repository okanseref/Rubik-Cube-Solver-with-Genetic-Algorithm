package yapayzekaproje;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class LineDrawing extends JFrame {
	static int[][][] c;

    public LineDrawing(int[][][] cube,String s) {
        super(s);
        c=cube;
        setSize(360, 480);
        setBackground(Color.black);
        setForeground(Color.black);
        getContentPane().setBackground( Color.black );
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    private Color getColor(int x,int y,int z) {
    	if(c[x][y][z]==0) {
    		return Color.white;
    	}
    	if(c[x][y][z]==1) {
    		return Color.red;
    	}
    	if(c[x][y][z]==2) {
    		return Color.yellow;
    	}
    	if(c[x][y][z]==3) {
    		return Color.blue;
    	}
    	if(c[x][y][z]==4) {
    		return Color.orange;
    	}
    	if(c[x][y][z]==5) {
    		return Color.green;
    	}
    	return null;
    }
    void drawLines(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        //Yüzey 2
        g2d.setColor(getColor(2,2,0));
        g2d.fillRect(120, 50, 30, 30);
        
        g2d.setColor(getColor(2,2,1));
        g2d.fillRect(152, 50, 30, 30);
        
        g2d.setColor(getColor(2,2,2));
        g2d.fillRect(184, 50, 30, 30);

        g2d.setColor(getColor(2,1,0));
        g2d.fillRect(120, 82, 30, 30);
        
        g2d.setColor(getColor(2,1,1));
        g2d.fillRect(152, 82, 30, 30);
        
        g2d.setColor(getColor(2,1,2));
        g2d.fillRect(184, 82, 30, 30);
        
        g2d.setColor(getColor(2,0,0));
        g2d.fillRect(120, 114, 30, 30);
        
        g2d.setColor(getColor(2,0,1));
        g2d.fillRect(152, 114, 30, 30);
        
        g2d.setColor(getColor(2,0,2));
        g2d.fillRect(184, 114, 30, 30);
        
        //4 
        g2d.setColor(getColor(4,0,0));
        g2d.fillRect(120, 150, 30, 30);
        
        g2d.setColor(getColor(4,0,1));
        g2d.fillRect(152, 150, 30, 30);
        
        g2d.setColor(getColor(4,0,2));
        g2d.fillRect(184, 150, 30, 30);

        g2d.setColor(getColor(4,1,0));
        g2d.fillRect(120, 182, 30, 30);
        
        g2d.setColor(getColor(4,1,1));
        g2d.fillRect(152, 182, 30, 30);
        
        g2d.setColor(getColor(4,1,2));
        g2d.fillRect(184, 182, 30, 30);
        
        g2d.setColor(getColor(4,2,0));
        g2d.fillRect(120, 214, 30, 30);
        
        g2d.setColor(getColor(4,2,1));
        g2d.fillRect(152, 214, 30, 30);
        
        g2d.setColor(getColor(4,2,2));
        g2d.fillRect(184, 214, 30, 30);
        
        //0
        
        g2d.setColor(getColor(0,0,0));
        g2d.fillRect(120, 250, 30, 30);
        
        g2d.setColor(getColor(0,0,1));
        g2d.fillRect(152, 250, 30, 30);
        
        g2d.setColor(getColor(0,0,2));
        g2d.fillRect(184, 250, 30, 30);

        g2d.setColor(getColor(0,1,0));
        g2d.fillRect(120, 282, 30, 30);
        
        g2d.setColor(getColor(0,1,1));
        g2d.fillRect(152, 282, 30, 30);
        
        g2d.setColor(getColor(0,1,2));
        g2d.fillRect(184, 282, 30, 30);
        
        g2d.setColor(getColor(0,2,0));
        g2d.fillRect(120, 314, 30, 30);
        
        g2d.setColor(getColor(0,2,1));
        g2d.fillRect(152, 314, 30, 30);
        
        g2d.setColor(getColor(0,2,2));
        g2d.fillRect(184, 314, 30, 30);
        
        //5
        g2d.setColor(getColor(5,0,0));
        g2d.fillRect(120, 350, 30, 30);
        
        g2d.setColor(getColor(5,0,1));
        g2d.fillRect(152, 350, 30, 30);
        
        g2d.setColor(getColor(5,0,2));
        g2d.fillRect(184, 350, 30, 30);

        g2d.setColor(getColor(5,1,0));
        g2d.fillRect(120, 382, 30, 30);
        
        g2d.setColor(getColor(5,1,1));
        g2d.fillRect(152, 382, 30, 30);
        
        g2d.setColor(getColor(5,1,2));
        g2d.fillRect(184, 382, 30, 30);
        
        g2d.setColor(getColor(5,2,0));
        g2d.fillRect(120, 414, 30, 30);
        
        g2d.setColor(getColor(5,2,1));
        g2d.fillRect(152, 414, 30, 30);
        
        g2d.setColor(getColor(5,2,2));
        g2d.fillRect(184, 414, 30, 30);
     	
        //1
        g2d.setColor(getColor(1,0,0));
        g2d.fillRect(220, 250, 30, 30);
        
        g2d.setColor(getColor(1,0,1));
        g2d.fillRect(252, 250, 30, 30);
        
        g2d.setColor(getColor(1,0,2));
        g2d.fillRect(284, 250, 30, 30);

        g2d.setColor(getColor(1,1,0));
        g2d.fillRect(220, 282, 30, 30);
        
        g2d.setColor(getColor(1,1,1));
        g2d.fillRect(252, 282, 30, 30);
        
        g2d.setColor(getColor(1,1,2));
        g2d.fillRect(284, 282, 30, 30);
        
        g2d.setColor(getColor(1,2,0));
        g2d.fillRect(220, 314, 30, 30);
        
        g2d.setColor(getColor(1,2,1));
        g2d.fillRect(252, 314, 30, 30);
        
        g2d.setColor(getColor(1,2,2));
        g2d.fillRect(284, 314, 30, 30);
        

        //3
        g2d.setColor(getColor(3,0,0));
        g2d.fillRect(20, 250, 30, 30);
        
        g2d.setColor(getColor(3,0,1));
        g2d.fillRect(52, 250, 30, 30);
        
        g2d.setColor(getColor(3,0,2));
        g2d.fillRect(84, 250, 30, 30);

        g2d.setColor(getColor(3,1,0));
        g2d.fillRect(20, 282, 30, 30);
        
        g2d.setColor(getColor(3,1,1));
        g2d.fillRect(52, 282, 30, 30);
        
        g2d.setColor(getColor(3,1,2));
        g2d.fillRect(84, 282, 30, 30);
        
        g2d.setColor(getColor(3,2,0));
        g2d.fillRect(20, 314, 30, 30);
        
        g2d.setColor(getColor(3,2,1));
        g2d.fillRect(52, 314, 30, 30);
        
        g2d.setColor(getColor(3,2,2));
        g2d.fillRect(84, 314, 30, 30);
    }
 
    public void paint(Graphics g) {
        super.paint(g);
        drawLines(g);
    }
 
    public static void main(String[] args) {

    }
}