package partie3;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.util.Scanner;
public class InterfaceErgonomique  extends JFrame{
	public JTextArea txtInput;
	public String msg1 = "";
	public int i1 =0;
	public boolean bb=false ;
	public InterfaceErgonomique(){
	    MyKeyListener myKeyListener = new MyKeyListener();
	    txtInput = new JTextArea(10,40);
	    txtInput.addKeyListener(myKeyListener);
	    add(new JScrollPane(txtInput), BorderLayout.NORTH);
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    pack();
	    setVisible(true);
	    txtInput.requestFocusInWindow();
		try {
		Connection connection = Connexion.factory.newConnection();
	    Channel channel = connection.createChannel();
	    channel.exchangeDeclare(EXCHANGE_NAME, "direct");
	    String queueName = channel.queueDeclare().getQueue();
	    channel.queueBind(queueName, EXCHANGE_NAME, "msg");
	    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
	    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
	    	String message = new String(delivery.getBody(), "UTF-8");
	        System.out.println(" [x2] Received '" + message + "'");
	        msg1=message;
	    };
	    channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
	    channel.exchangeDeclare(EXCHANGE_NAME, "direct");
	    String queueNamei = channel.queueDeclare().getQueue();
	    channel.queueBind(queueNamei, EXCHANGE_NAME, "i");
	    System.out.println(" [*] Waiting for indexes. To exit press CTRL+C");

	    DeliverCallback deliverCallbacki = (consumerTag, delivery) -> {
	    	String message = new String(delivery.getBody(), "UTF-8");
	    	
	    	System.out.println(" [x1] recieved index '" + message + "'");
	    	i1 = Integer.parseInt(message);
	    	String text1 = this.txtInput.getText();
	    	if (this.bb == true)
	    		this.bb =false;
	    	else if (this.bb == false)
	    		this.txtInput.setText(addChar(text1,msg1,i1));
	    };
	    channel.basicConsume(queueNamei, true, deliverCallbacki, consumerTag -> { });
	   
		}
		catch(Exception e ) {}
	}	
	private static class Connexion {
		private static ConnectionFactory factory ;
		public Connexion() throws Exception {
			 factory = new ConnectionFactory();
		     factory.setHost("localhost");    
		}
	}
	public static String addChar(String str, String ch, int position) {
		if(ch.equals(""))
			return str.substring(0, position) + str.substring(position+1);
	    return str.substring(0, position) + ch + str.substring(position);
	}
	private class MyKeyListener extends KeyAdapter {
	    @Override
	    public void keyTyped(KeyEvent e) {
	    	String key = e.getKeyChar()+"";
	    	int ind= txtInput.getCaretPosition();
	    	try (Connection connection = Connexion.factory.newConnection();
					Channel channel =connection.createChannel())
				{
	    			channel.exchangeDeclare(EXCHANGE_NAME,"direct");
					if(e.getKeyChar() == KeyEvent.VK_BACK_SPACE)
						{
							key="";
							channel.basicPublish(EXCHANGE_NAME, "msg", null, key.getBytes());
						}
			    	if(e.getKeyChar() == KeyEvent.VK_ENTER) {
						channel.basicPublish(EXCHANGE_NAME, "msg", null, "\n".getBytes());
			    		ind--;
			    	}
			    	else
					{
						channel.basicPublish(EXCHANGE_NAME, "msg", null, key.getBytes());						
					}
					System.out.println("[x] sent '"+key +"'");
				}
	        catch(Exception e1) {System.out.print(e1.getStackTrace());}
	    	try (Connection connection = Connexion.factory.newConnection();
					Channel channel =connection.createChannel())
				{
	    			channel.exchangeDeclare(EXCHANGE_NAME, "direct");
					
			    	String inds = ind+"";
					channel.basicPublish(EXCHANGE_NAME, "i", null, inds.getBytes());
					InterfaceErgonomique.this.bb = true;
					System.out.println("[x] sent '"+ind +"'");
					System.out.println("[x] sent '"+bb +"'");
				}
	        catch(Exception e1) {System.out.print(e1.getStackTrace());}
	    }
	}
	private final static String EXCHANGE_NAME= "t" ;
	public static void main(String [] args) throws Exception {
		Scanner keyboard = new Scanner(System.in);
		System.out.println("donner le nombre des interfaces à utiliser");
		int n = keyboard.nextInt();
		new Connexion();
		for (int i=0;i<n;i++) {
		new InterfaceErgonomique();
		}

	}
	}
