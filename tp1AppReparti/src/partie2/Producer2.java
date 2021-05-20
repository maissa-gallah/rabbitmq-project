package partie2;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer2  extends JFrame{
private String msg;
private JTextArea txtInput;
public Producer2(){
    
    MyKeyListener myKeyListener = new MyKeyListener();
    txtInput = new JTextArea(10,40);
    txtInput.addKeyListener(myKeyListener);
    add(new JScrollPane(txtInput), BorderLayout.NORTH);
   
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    pack();
    setVisible(true);
    txtInput.requestFocusInWindow();
}	
private static class Connexion {
	private static ConnectionFactory factory ;
	public Connexion() throws Exception {
		 factory = new ConnectionFactory();
	     factory.setHost("localhost");
	   
	}
}

public String getMsg() {return msg;}
private class MyKeyListener extends KeyAdapter {
    @Override
    public void keyTyped(KeyEvent e) {
    	String key = e.getKeyChar()+"";
    	int ind= txtInput.getCaretPosition();
    	try (Connection connection = Connexion.factory.newConnection();
				Channel channel =connection.createChannel())
			{
				channel.queueDeclare(QUEUE_NAME, false,false,false,null);
				channel.queueDeclare(QUEUE_NAMEI, false,false,false,null);
				//Suppression
				if(e.getKeyChar() == KeyEvent.VK_BACK_SPACE)
					{
					key="";
					channel.basicPublish("", QUEUE_NAME, null, key.getBytes());
					}
		    	if(e.getKeyChar() == KeyEvent.VK_ENTER) {
		    		channel.basicPublish("", QUEUE_NAME, null, "\n".getBytes());
		    		ind--;
		    	}
		    	else
				{channel.basicPublish("", QUEUE_NAME, null, key.getBytes());
				}
				System.out.println("[x] sent '"+key +"'");
			}
        catch(Exception e1) {System.out.print(e1.getStackTrace());}
    	try (Connection connection = Connexion.factory.newConnection();
				Channel channel =connection.createChannel())
			{
				channel.queueDeclare(QUEUE_NAMEI, false,false,false,null);
		    	String inds = ind+"";
				channel.basicPublish("", QUEUE_NAMEI, null, inds.getBytes());
				System.out.println("[x] sent '"+ind +"'");
			}
        catch(Exception e1) {System.out.print(e1.getStackTrace());}
    }
}
private final static String QUEUE_NAME= "t2" ;
private final static String QUEUE_NAMEI= "i2" ;
public static void main(String [] args) throws Exception {
	new Producer2();
	new Connexion();
	
}

}
