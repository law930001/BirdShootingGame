import java.awt.*;  
import java.awt.event.*;
import java.util.Random;
import javax.imageio.*;
import java.io.*;

public class finalProject
{
	public static class BirdShoot extends Frame
	{
        // constant values
        private static final int rect_width  = 100; // width of rectangle
        private static final int rect_height = 90;  // height of rectangle
        private static final int main_pos_x  = 20;  // controling all position x 
        private static final int main_pos_y  = 90;  // controling all position y
        private static final int main_game_time = 30; // set the constant of game time
        // componect items
        private static MenuBar mb = new MenuBar();
        private static Menu menu1 = new Menu("Game");
        private static MenuItem mi1 = new MenuItem("Reset");
        // setting elements
        private Rectangle[][] rects = new Rectangle[6][4];
		private int[][] numFill = new int[6][4]; // default:0, level 1 ~ 5
        private boolean iniPaint = false;   // for initialize paint()
        private boolean DelayLock = true;   // lock for press wrong to delay
        private boolean reset = false;
        private LeftBar lb = new LeftBar();
        private Point token_pos = new Point(main_pos_x+5, main_pos_y+(6*rect_height)-5+20);
        private Image final_image, shoot_image;
        private Image[] birds_image = new Image[6];
        // double buffer
        private  Image iBuffer;  
        private  Graphics gBuffer; 
        // game elements
        private int start_index = -1;       // -1 or 0 ~ 3
        private int game_score = 0;         // game score
        private int game_exp = 0;           // times of game go up
        private int game_time_left = main_game_time;     // left time of a game
        private int game_level = 1;         // 1 ~ 5
        // initialize colors in game
        private final Color[] game_colors = { 
            new Color(200,200,193),  // 0: main frame background color
            new Color(198,225,122),    // 4: level 1
            new Color(80,216,144),   // 1: level 2
            new Color(116,127,220),  // 2: level 3
            new Color(255,68,124),   // 3: level 4
            new Color(166,216,0),    // 5: level 5 - final
            new Color(216,216,216),  // 6: blocks default color
            new Color(252,74,74),    // 7: warning color
            new Color(137,137,137),  // 8: border color           
            new Color(0,0,0)         // 9: count down time's text color         
        };
        // set rules for game level
        private int[][] game_rule = { 
            {44,  84, 114, 134, 999},   // number of blocks each level: 50, 40, 30, 20, or more
            {50, 100, 200, 400, 800}   // score each level
        };

		BirdShoot(){ // constructor
            // setting components
            initAllComponents();
            // initialize borders locations
            for(int a = 0; a < 6; a++){
                for(int b = 0; b < 4; b++){
                    // Rectangle(x, y, width, height)
                    rects[a][b] = new Rectangle(b*rect_width  + main_pos_x + 40, 
                                                a*rect_height + main_pos_y, 
                                                rect_width, rect_height);
                }
            }
            // random number
			Random rd = new Random();
			for(int a = 0; a < 6; a++){
				int position = rd.nextInt(4); // 0 ~ 3
				numFill[a][position] = 1;
                if(a == 5)
                    start_index = position;
			}
            // including image
            try{
                final_image = ImageIO.read(new File("image/final.png"));
                shoot_image = ImageIO.read(new File("image/shoot.png"));
                birds_image[1] = ImageIO.read(new File("image/b1.png"));
                birds_image[2] = ImageIO.read(new File("image/b2.png"));
                birds_image[3] = ImageIO.read(new File("image/b3.png"));
                birds_image[4] = ImageIO.read(new File("image/b4.png"));
                birds_image[5] = ImageIO.read(new File("image/b5.png"));
            }catch(Exception ex){ System.out.println("No image!!"); }
            // draw
            iniPaint = true;
            repaint();
		}

        @Override
        public void update(Graphics g){  
            if (iBuffer == null){ 
               iBuffer = createImage(this.getSize().width, this.getSize().height); 
               gBuffer = iBuffer.getGraphics();  
            }  
            gBuffer.setColor(getBackground());  
            gBuffer.fillRect(0, 0, this.getSize().width, this.getSize().height);  
            paint(gBuffer);  
            g.drawImage(iBuffer, 0, 0, this);  
        }

        @Override
		public void paint(Graphics g){
            if(iniPaint == true)
            {              
                // paint rectangle
                for(int a = 0; a < 6; a++){
                    for(int b = 0; b < 4; b++){
                        // fill rectangle
                        if(numFill[a][b] != 0){ // set levels bird
                            g.drawImage(birds_image[numFill[a][b]], rects[a][b].x, rects[a][b].y,
                                        rects[a][b].width, rects[a][b].height, null);
                        // draw aiming shoot
                            if(a == 5){
                                g.drawImage(shoot_image, rects[a][b].x, rects[a][b].y,
                                            rects[a][b].width, rects[a][b].height, null);   
                            }
                        }
                        else{ // set blcok default color
                            g.setColor(game_colors[6]);
                            g.fillRect(rects[a][b].x,     rects[a][b].y,
                                       rects[a][b].width, rects[a][b].height);
                        }
                    }
                }
                // draw left bar
                lb.drawBar(g);
                // draw time left
                g.setFont(new Font("", Font.BOLD, 20));
                g.setColor(game_colors[9]);
                String str1 = "Time: " + Integer.toString(game_time_left) + "s";
                g.drawString(str1, main_pos_x + 60, main_pos_y - 15);
                // draw score
                g.setColor(Color.black);
                String str2 = "Score: " + Integer.toString(game_score);
                g.drawString(str2, main_pos_x + 220, main_pos_y - 15);
                // start index
                if(start_index != -1){
                    g.setFont(new Font("", Font.BOLD, 25));
                    g.setColor(Color.red);
                    g.drawString("Start",rects[5][start_index].x + 20,
                                         rects[5][start_index].y + 55);
                }
                // game ending
                if(game_time_left == 0){
                    g.setFont(new Font("", Font.BOLD, 80));
                    g.setColor(Color.red);
                    g.drawString("End", main_pos_x + 165, main_pos_y + 300);
                }
                // draw asdf hint
                g.setFont(new Font("", Font.BOLD, 25));
                g.setColor(new Color(108,106,106));
                g.drawString("A",rects[5][0].x + 45 , rects[5][0].y + 135);
                g.drawString("S",rects[5][1].x + 45 , rects[5][0].y + 135);
                g.drawString("D",rects[5][2].x + 45 , rects[5][0].y + 135);
                g.drawString("F",rects[5][3].x + 45 , rects[5][0].y + 135);
                // draw count down time
                if(game_time_left <= 3 && game_time_left > 0){
                    g.setFont(new Font("", Font.BOLD, 80));
                    g.setColor(Color.red);
                    g.drawString(Integer.toString(game_time_left),
                                 main_pos_x + 215, main_pos_y + 300);
                }
            }
		}

        void initAllComponents(){
            // setting frame
            setTitle("BirdShoot");
            setSize(500,700); 
            setLocation(10,10);
            setVisible(true);
            setResizable(false);
            setBackground(game_colors[0]);
            // setting menu
            mb.add(menu1);
            menu1.add(mi1);
        }

		void execute(){
            // including window close
            addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e) {System.exit(0);}
            });
            // including menu and listener
            setMenuBar(mb);
            MenuLis mls = new MenuLis();
            mi1.addActionListener(mls);
            // including key Detection
            KeyLis kls = new KeyLis();
            addKeyListener(kls);
		}

        void startCountDown(){
            new Thread() {
                @Override
                public void run() {
                    while(true){
                        try {
                            if(reset == true){
                                reset = false;
                                break;
                            }
                            game_time_left--;
                            if(game_time_left == 10){
                                game_colors[9] = Color.red;
                                doBlink();
                            }
                            repaint();
                            if(game_time_left == 0)
                                break;
                            Thread.sleep(1000);
                        } catch ( InterruptedException e ) {}
                    }
                }
            }.start();
        }

        int Rc, Gc, Bc, reduce;
        void doBlink(){
            new Thread() {
                @Override
                public void run() {
                    Rc = 255;
                    Gc = 255;
                    Bc = 168;
                    reduce = 5;
                    while(true){
                        try {
                            if(game_time_left == 0 ||  Bc <= 10 || Gc <= 10 || reset == true){
                                reduce = 0;
                                break;
                            }
                            game_colors[0] = new Color(Rc,Gc,Bc);
                            if(DelayLock == true)
                                game_colors[0] = game_colors[7];
                            setBackground(game_colors[0]);
                            Thread.sleep(220);
                            Gc -= reduce;
                            Bc -= reduce;
                        } catch ( InterruptedException e ) {}
                    }
                }
            }.start();
        }

        private class MenuLis implements ActionListener
        {
            @Override
            public void actionPerformed(ActionEvent e){
                reset = (game_time_left == 0 || game_time_left == main_game_time) ? false : true;
                Random rd = new Random();
                for(int a = 0; a < 6; a++){
                    for(int b = 0; b < 4; b++)
                        numFill[a][b] = 0;
                    int position = rd.nextInt(4); // 0 ~ 3
                    numFill[a][position] = 1;
                    if(a == 5)
                        start_index = position;
                }
                game_score = 0;
                game_exp = 0;
                game_time_left = main_game_time;
                game_level = 1;
                DelayLock = true;
                game_colors[0] = new Color(200,200,193); // main background
                setBackground(game_colors[0]);
                game_colors[9] = new Color(0,0,0); // score text
                token_pos = new Point(main_pos_x + 5, main_pos_y + (6*rect_height) - 5 + 20);
            }

        } // MenuLis

        private class KeyLis extends KeyAdapter
        {
            @Override
            public void keyPressed(KeyEvent e){
                if(game_time_left == 0) // safe lock
                    DelayLock = true;
                switch(e.getKeyCode())
                {
                    case KeyEvent.VK_A:
                        pressResponse(0);
                        break;
                    case KeyEvent.VK_S:
                        pressResponse(1);
                        break;
                    case KeyEvent.VK_D:
                        pressResponse(2);
                        break;
                    case KeyEvent.VK_F:
                        pressResponse(3);
                        break;
                }
            }

            private void pressResponse(int index){
                if(start_index != -1 && index == start_index){ // game start here
                    DelayLock = false;
                    BirdShoot.this.startCountDown();
                    start_index = -1;
                }
                if(DelayLock == false)
                {
                    if(numFill[5][index] != 0){ // if pressed right
                        // add exp and decide game level
                        game_exp = game_exp + 1;
                        for(int a = 4; a >= 0; a--){
                            if(game_exp <= game_rule[0][a])
                                game_level = a+1;
                        }
                        // add game score
                        game_score += game_rule[1][game_level-1];
                        // left bar go up
                        lb.goUp();
                        // drop from top
                        for(int a = 5; a >= 1 ; a--){
                            for(int b = 0; b < 4; b++){
                               numFill[a][b] = numFill[a-1][b];
                            }
                        }
                        // random new item
                        Random rd = new Random();
                        for(int a = 0; a < 4; a++)
                            numFill[0][a] = 0;
                        numFill[0][rd.nextInt(4)] = game_level; // rd: 0 ~ 3
                        // paint
                        BirdShoot.this.repaint();
                    }
                    else{ // delay here
                        DelayLock = true; // lock here
                        BirdShoot.this.setBackground(game_colors[7]); // warning
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(500);
                                    DelayLock = false; // unlock here
                                    BirdShoot.this.setBackground(game_colors[0]); // recover
                                } catch ( InterruptedException e ) {}
                            }
                        }.start();
                    }
                } // Delay Lock
            }
        } // class KeyLis

        private class LeftBar
        {
            private Rectangle[] left_bar = new Rectangle[4];
            private Rectangle token = new Rectangle();

            LeftBar(){
                // initialize rectangle position
                left_bar[0] = new Rectangle(main_pos_x, main_pos_y      , 20, 80);
                left_bar[1] = new Rectangle(main_pos_x, main_pos_y + 80 , 20, 120);
                left_bar[2] = new Rectangle(main_pos_x, main_pos_y + 200, 20, 160);
                left_bar[3] = new Rectangle(main_pos_x, main_pos_y + 360, 20, 200);
            }

            public void drawBar(Graphics g){
                for(int a = 0; a < 4; a++){
                    // fill color
                    g.setColor(game_colors[4-a]);
                    g.fillRect(left_bar[a].x,     left_bar[a].y, 
                               left_bar[a].width, left_bar[a].height);
                    // draw border line
                    g.setColor(Color.black);
                    g.drawRect(left_bar[a].x,     left_bar[a].y, 
                               left_bar[a].width, left_bar[a].height);
                    // draw circle
                    g.setColor(Color.red);
                    g.fillOval(token_pos.x, token_pos.y,10,10);
                    g.setColor(Color.black);
                    g.drawOval(token_pos.x, token_pos.y,10,10);
                    // draw bird
                }
                g.drawImage(final_image, main_pos_x - 10, main_pos_y - 45, 40, 40,null);
            }

            public void goUp(){
                if(token_pos.y >= main_pos_y - 2)
                    token_pos.y = token_pos.y - 4;
            }

        } // class LeftBar

	} // class BirdShoot

    public static void main(String args[])
    {
    	BirdShoot asdf = new BirdShoot();
    	asdf.execute();
    }

} // class finalProject

















