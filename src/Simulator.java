import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.JFrame;


public class Simulator extends JComponent{

	private static final long serialVersionUID = -6881184014715859484L;
	private City city;
	
	public Simulator(){
		this.city = new City(500,500);
	}
	
	public void paint(Graphics g){
//		super.paint(g);
		this.city.paint(g, this.getWidth(), this.getHeight());
	}
	
	public static void main(String[] args){
		JFrame frame = new JFrame("City Sim");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		frame.setUndecorated(true);
		frame.getContentPane().add(new Simulator());
		frame.validate();
		frame.setVisible(true);
		
	}

}
