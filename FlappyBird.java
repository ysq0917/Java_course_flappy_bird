import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Container;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.*;
public class FlappyBird implements ActionListener, MouseListener, KeyListener
{
	public static FlappyBird FlappyBird;

	public final int WIDTH = 800, HEIGHT = 800;

	public Renderer renderer;

	public Rectangle redBall;

	public ArrayList<Rectangle> columns;

	public int ticks, yMotion, score;

	public boolean gameOver, started;

	public Random rand;
	
	public Image bird;
	public Image bird1;
	public Image bird2;
	public Image bird3;

	public Image tree;
	
	public Image net;
		
	public Image spider;
	
	public Image background;
	
	public String name;
	
	private AudioClip audio;
	public FlappyBird(){
	   String file = "pokemon.wav";
	   try {  
			URL musicURL = new URL("file:" +
            System.getProperty("user.dir") + "/" + file);
            audio = Applet.newAudioClip(musicURL);
       }
      catch ( MalformedURLException e ) { }
	  audio.loop();
	  
		JFrame jframe = new JFrame();
		Timer timer = new Timer(20, this);

		renderer = new Renderer();
		rand = new Random();

		jframe.add(renderer);
		jframe.setTitle("Flappy bird");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setSize(WIDTH, HEIGHT);
		jframe.addMouseListener(this);
		jframe.addKeyListener(this);
		jframe.setResizable(false);
		jframe.setVisible(true);		
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		bird1= toolkit.getImage("first.png");
	    bird2= toolkit.getImage("second.png");
	    bird3= toolkit.getImage("third.png");
		background = toolkit.getImage("ground1.jpg");
		redBall = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
		columns = new ArrayList<Rectangle>();

		addColumn(true);
		addColumn(true);
		addColumn(true);
		addColumn(true);

		timer.start();
	}
	
	public void addColumn(boolean start)
	{
		int space = 300;
		int width = 100;
		int height = 50 + rand.nextInt(300);

		if (start)
		{
			columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT - height - 120, width, height));
			columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space));
		}
		else
		{
			columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 120, width, height));
			columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));
		}
	}

	 public void paintColumn(Graphics g, Rectangle column)
	{
	if(column.y<300){
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	spider = toolkit.getImage("111.png");
	g.drawImage(spider,column.x-(spider.getWidth(null)/2)+20,column.height-spider.getHeight(null)+0, null);
	}
	else{
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	tree = toolkit.getImage("tree.png");
	g.drawImage(tree,column.x-(tree.getWidth(null)/2)+30,column.y+(tree.getHeight(null)/4)-220, null);
	}
	}

	public void jump()
	{
		if (gameOver){
			redBall = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
			columns.clear();
			yMotion = 0;
			score = 0;

			addColumn(true);
			addColumn(true);
			addColumn(true);
			addColumn(true);

			gameOver = false;
		}

		if (!started){
			started = true;
		}
		else if (!gameOver){
			if (yMotion > 0){
				yMotion = 0;
			}
			yMotion -= 10;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		int speed = 10;

		ticks++;

		if (started)
		{
			for (int i = 0; i < columns.size(); i++)
			{
				Rectangle column = columns.get(i);

				column.x -= speed;
			}

			if (ticks % 2 == 0 && yMotion < 15)
			{
				yMotion += 2;
			}

			for (int i = 0; i < columns.size(); i++)
			{
				Rectangle column = columns.get(i);

				if (column.x + column.width < 0)
				{
					columns.remove(column);

					if (column.y == 0)
					{
						addColumn(false);
					}
				}
			}

			redBall.y += yMotion;

			for (Rectangle column : columns)
			{
				if (column.y == 0 && redBall.x + redBall.width / 2 > column.x + column.width / 2 - 10 && redBall.x + redBall.width / 2 < column.x + column.width / 2 + 10)
				{
					score++;
				}

				if (column.intersects(redBall))
				{
					gameOver = true;

					if (redBall.x <= column.x)
					{
						redBall.x = column.x - redBall.width;

					}
					else
					{
						if (column.y != 0)
						{
							redBall.y = column.y - redBall.height;
						}
						else if (redBall.y < column.height)
						{
							redBall.y = column.height;
						}
					}
				}
			}

			if (redBall.y > HEIGHT || redBall.y < 0)
			{
				gameOver = true;
			}

			if (redBall.y + yMotion >= HEIGHT)
			{
				redBall.y = HEIGHT - redBall.height;
				gameOver = true;
			}
		}

		renderer.repaint();
	}

	public void repaint(Graphics g)
	{
		JFrame jframe = new JFrame();
		g.drawImage(background,0,0,WIDTH,HEIGHT,null);

		g.setColor(Color.red);
		g.fillRect(redBall.x, redBall.y, redBall.width, redBall.height);		
		
		if(score<5)
		g.drawImage(bird1,redBall.x-(bird1.getWidth(null)/2)+10,redBall.y-(bird1.getHeight(null)/2)+10, null);
		else if(score<10)
		g.drawImage(bird2,redBall.x-(bird2.getWidth(null)/2)+10,redBall.y-(bird2.getHeight(null)/2)+10, null);
		else g.drawImage(bird3,redBall.x-(bird3.getWidth(null)/2)+10,redBall.y-(bird3.getHeight(null)/2)+10, null);

		for (Rectangle column : columns)
		{
			paintColumn(g, column);
		}

		g.setColor(Color.white);
		g.setFont(new Font("Arial", 1, 100));

		if (!started)
		{
			g.drawString("Click to start!", 75, HEIGHT / 2 - 50);
		}

		if (gameOver)
		{
			g.drawString("Game Over!", 100, HEIGHT / 2 - 50);
		}

		if (!gameOver && started)
		{
			g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
		}
	}

	public static void main(String[] args){
		FlappyBird = new FlappyBird();
	}

	@Override
	public void mouseClicked(MouseEvent e){jump();}
	@Override
	public void keyReleased(KeyEvent e){
		if (e.getKeyCode() == KeyEvent.VK_SPACE){
			jump();
		}
	}
	@Override
	public void mousePressed(MouseEvent e){}
	@Override
	public void mouseReleased(MouseEvent e){}
	@Override
	public void mouseEntered(MouseEvent e){}
	@Override
	public void mouseExited(MouseEvent e){}
	@Override
	public void keyTyped(KeyEvent e){}
	@Override
	public void keyPressed(KeyEvent e){}
}
class Renderer extends JPanel{
	private static final long serialVersionUID = 1L;
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		FlappyBird.FlappyBird.repaint(g);
	}	
}