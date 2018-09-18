import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.color.*;
import java.util.*;
import java.util.ArrayList;

/**
 * GUI by Temi Omitoogun, algorithm by Haiming Xu
 * @author HaimingXu and TemiOmitoogun
 * 11/30/2016 
 * Version 8
 */
public class ChessG {
	public static class ChessGui{
	    //Creating variables to use in the program
	    //Creating JPanel for layout
		private final JPanel gui = new JPanel(new BorderLayout(3,3));
	    //Best method for squares on the chess board is to do a JButton for each square
		private JButton[][] chessBoardSquares = new JButton[8][8];
		private int[][] pieces = new int[8][8];
		private Image[][] chessPieceImages = new Image[2][6];
		private JPanel chessBoard;
		 // Assigning an integer to each piece to make the pieces
		public static final int KING=0,QUEEN=1,ROOK=2,KNIGHT=3,BISHOP=4,PAWN=5;
		 //Assigning the pieces
		public static final int[] STARTINGROW={ ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK};
		private static final String columns="ABCDEFGH";
		public static final int BLACK=0,WHITE=1;
		boolean firstClick=true;
		Color Black=new Color(0, 0, 0);
		Color Red=new Color(245,245,220);
		Color Blue=new Color(204, 255, 255);
		Color Attack=new Color(255,204,204);
		Color Check=new Color(255,0,0);
		JButton movingPiece;
		ChessGui(){
			initializeGui();
		}
		//global turn variable and castle capacity
		boolean GAMEOVER=false,turn=true,k1=false,k2=false,wr1=false,wr2=false,br1=false,br2=false,castling1=false,castling2=false;
		ArrayList<Integer> moves=new ArrayList<Integer>();
		public final void initializeGui(){
			createImages();   
			gui.setBorder(new EmptyBorder(5,5,5,5));
			JToolBar tools = new JToolBar();
			tools.setFloatable(false);
			gui.add(tools, BorderLayout.PAGE_START);
			Action newGameAction = new AbstractAction("New")
			{
                //What Overriding allows you to do is to override the functionality of an existing method
                //So if two methods conflict, this one comes out on top :D
				@Override
				public void actionPerformed(ActionEvent e)
				{ setupNewGame(); }
			};
			Action exitAction = new AbstractAction("Exit")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{ System.exit(0); }
			};
			tools.add(newGameAction);
			tools.add(exitAction);
	    	chessBoard = new JPanel(new GridLayout(0, 8)){
	    		@Override
	    		public final Dimension getPreferredSize() {
	    			Dimension d = super.getPreferredSize();
	    			Dimension prefSize = null;
	    			Component c = getParent();
	    			if (c == null) 
	    				prefSize = new Dimension((int)d.getWidth(),(int)d.getHeight());
	    			else if (c!=null && c.getWidth()>d.getWidth() && c.getHeight()>d.getHeight())
	    				prefSize = c.getSize();
	    			else prefSize = d;
	    			int w = (int) prefSize.getWidth();
	    			int h = (int) prefSize.getHeight();
	    			int s = (w>h ? h : w);
	    			return new Dimension(s,s);
	    		}
	    	};
	    	Color bgcolor = new Color(209, 220 ,224);
	    	chessBoard.setBorder(new CompoundBorder(new EmptyBorder(8,8,8,8),new LineBorder(bgcolor)));
	    	chessBoard.setBackground(bgcolor);
	    	JPanel boardConstrain = new JPanel(new GridBagLayout());
	    	boardConstrain.setBackground(bgcolor);
	    	boardConstrain.add(chessBoard);
	    	gui.add(boardConstrain);
	    	Insets buttonMargin = new Insets(0, 0, 0, 0);
	    	for ( int ii = 0;  ii < chessBoardSquares.length; ii++) {
	    		for ( int jj = 0; jj < chessBoardSquares[ii].length; jj++) {
	    			JButton b = new JButton();
	    			b.setName(Integer.toString(ii*8+jj));
	    			b.setMargin(buttonMargin);
	    			ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
	    			b.setIcon(icon);
	    			if ((jj % 2 == 1 && ii % 2 == 1)|| (jj % 2 == 0 && ii % 2 == 0))
	    				b.setBackground(Red);
	    			else b.setBackground(Black);
	    			chessBoardSquares[jj][ii] = b;
	    			if (!GAMEOVER){
	    				b. addActionListener( new ActionListener(){
	    					public void actionPerformed(ActionEvent e){
	    						int pos = Integer.parseInt(((JButton) (e.getSource())).getName());
	    						int ii=pos/8,jj=pos%8;
	    						//I stored all the highlighted squares in an array list.
	    						//By multiplying x by 100 and adding y, I can store two coordinates with one number
	    						for (int i=0; i<moves.size();i++){
	    							//I restore squares to original color based off of location on the grid
	    							int tx=moves.get(i)%100,ty=moves.get(i)/100;
	    							if (tx%2==0 && ty%2==0) chessBoardSquares[ty][tx].setBackground(Red);
	    							if (tx%2==1 && ty%2==0) chessBoardSquares[ty][tx].setBackground(Black);
	    							if (tx%2==0 && ty%2==1) chessBoardSquares[ty][tx].setBackground(Black);
	    							if (tx%2==1 && ty%2==1) chessBoardSquares[ty][tx].setBackground(Red);
	    						}
	    						//Reset bool variables castling to false so that castling doesn't occur if not requested.
	    						castling1=false;
	    						castling2=false;
	    						moves.clear();
	    						if(firstClick){
	    							if (pieces[jj][ii]==-1) return;
	    							else if (pieces[jj][ii]>5 && turn==false) return;
	    							else if (pieces[jj][ii]<=5 && turn==true) return;
	    							firstClick=false;
	    							movingPiece=b;
	    							int type=pieces[jj][ii];
	    							moves.clear();
	    							//Calling these functions/methods to highlight squares depending on the piece selected.
	    							if (type==5) highlight5(ii,jj);
	    							else if (type==11) highlight11(ii,jj);
	    							else if (type==1) highlight1(ii,jj);
						            else if (type==7) highlight7(ii,jj);
						            else if (type==4) highlight4(ii,jj);
						            else if (type==10) highlight10(ii,jj);
						            else if (type==2) highlight2(ii,jj);
						            else if (type==8) highlight8(ii,jj);
						            else if (type==3) highlight3(ii,jj);
						            else if (type==9) highlight9(ii,jj);
						            else if (type==6) highlight6(ii,jj);
						            else highlight0(ii,jj);
	    						}
	    						else{
	    							firstClick=true;
	    							int temp1=Integer.parseInt(b.getName()),temp2=Integer.parseInt(movingPiece.getName());
	    							int xc=temp2/8,yc=temp2%8,x=temp1/8,y=temp1%8;
	    							int type=pieces[yc][xc];
	    							if (type==0){
	    								int i,j;
	    								//Black castling
	    								if (y==yc+2 || y==yc-2){
	    									if (y==yc+2){
	    										if (pieces[y][x]!=-1){
	    											firstClick=true;
		    										return;
	    										}
	    									}
	    									if (y==yc-2){
	    										if (pieces[y][x]!=-1){
	    											firstClick=true;
		    										return;
	    										}
	    									}
	    									if (x!=xc){
	    										firstClick=true;
	    										return;
	    									}
							            	if (k1==true){
							            		firstClick=true;
							            		return;
							            	}
							            	if (y==yc+2 && br1==true){
							            		firstClick=true;
							            		return;      				
							            	}
							            	if (y==yc-2 && br2==true){
							            		firstClick=true;
							            		return;   
							            	}
							            	if (y==yc+2){
							            		for (i=yc+1;i<8;i++){
							            			if (pieces[i][x]!=-1){
							            				firstClick=true;
							            				return;
							            			}
							            		}
							            	}
							            	if (y==yc-2){
							            		for (i=yc-1;i>0;i--){
							            			if (pieces[i][xc]!=-1){
							            				firstClick=true;
							            				return;
							            			}
							            		}
							            	}
							            	//If all the above tests are passed, then castling may be done if king will NOT be in check.
							            	castling1=true;
	    								}
	    								else if (xc==x && yc==y){
	    									firstClick=true;
	    									return;
	    								}
	    								else if (x-1>xc){
	    									firstClick=true;
	    									return;
	    								}
	    								else if (x+1<xc){
	    									firstClick=true;
	    									return;
	    								}
	    								else if (y-1>yc){
	    									firstClick=true;
	    									return;
	    								}
	    								else if (y+1<yc){
	    									firstClick=true;
	    									return;
	    								}
	    								else if (pieces[y][x]!=-1 && pieces[y][x]<=5){
	    									firstClick=true;
	    									return;
	    								}
	    								if (!castling1) k1=true;
	    								//The black king has moved.
	    							}
	    							if (type==6){
	    								int i,j;
	    								//White castling
	    								if (y==yc+2 || y==yc-2){	
	    									if (y==yc+2){
	    										if (pieces[y][x]!=-1){
	    											firstClick=true;
		    										return;
	    										}
	    									}
	    									if (y==yc-2){
	    										if (pieces[y][x]!=-1){
	    											firstClick=true;
		    										return;
	    										}
	    									}
	    									if (x!=xc){
	    										firstClick=true;
	    										return;
	    									}	
	    									if (k2==true){
	    										firstClick=true;
	    										return;
	    									}
	    									if (y==yc+2 && wr1==true){
	    										firstClick=true;
	    										return;      				
	    									}
	    									if (y==yc-2 && wr2==true){
	    										firstClick=true;
	    										return;   
	    									}
	    									if (y==yc+2){
	    										for (i=yc+1;i<8;i++){
	    											if (pieces[i][x]!=-1){
	    												firstClick=true;
	    												return;
	    											}
	    										}
	    									}
	    									if (y==yc-2){
	    										for (i=yc-1;i>0;i--){
	    											if (pieces[i][xc]!=-1){
	    												firstClick=true;
	    												return;
	    											}
	    										}
	    									}
	    									//See black castling
	    									castling2=true;
	    								}
	    								else if (xc==x && yc==y){
	    									firstClick=true;
	    									return;
	    								}
	    								else if (x-1>xc){
	    									firstClick=true;
	    									return;
	    								}
	    								else if (x+1<xc){
	    									firstClick=true;
	    									return;
	    								}
	    								else if (y-1>yc){
	    									firstClick=true;
	    									return;
	    								}
	    								else if (y+1<yc){
	    									firstClick=true;
	    									return;
	    								}
	    								else if (pieces[y][x]>5){
	    									firstClick=true;
	    									return;
	    								}
	    								if (!castling2) k2=true;
	    								//The white king has moved.
	    							}
	    							if (type==8){
	    								//White rook movement
	    								if (x==xc && y==yc){
	    									firstClick = true; 
	    									return;
	    								}
	    								//It can't move to same square.
	    								if (x!=xc && y!=yc){
	    									firstClick=true;
	    									return;
	    								}
	    								//Looping to see if the position between the rook and the click has any pieces in between.
	    								int i,j;
	    								if (x==xc){
	    									if (y<yc){
	    										for (i=yc-1;i>=y;i--){
	    											if (pieces[i][x]!=-1){
	    												if (pieces[i][x]>5){
	    													firstClick=true;
	    													return;
	    												}
	    												else{
	    													if (i!=y){
	    														firstClick=true;
	    														return;
	    													}
	    												}
	    											}
	    										}	   
	    									}
	    									else{
	    										for (i=yc+1;i<=y;i++){
	    											if (pieces[i][x]!=-1){
	    												if (pieces[i][x]>5){
	    													firstClick=true;
	    													return;
	    												}
	    												else{
	    													if (i!=y){
	    														firstClick=true;
	    														return;
	    													}
	    												}
	    											}
	    										}
	    									} 
	    								}
	    								else if (y==yc){
	    									if (x<xc){
	    										for (i=xc-1;i>=x;i--){
	    											if (pieces[y][i]!=-1){
	    												if (pieces[y][i]>5){
	    													firstClick=true;
	    													return;
	    												}
	    												else{
	    													if (i!=x){
	    														firstClick=true;
	    														return;
	    													}
	    												}
	    											}
	    										}	   
	    									}
	    									else{
	    										for (i=xc+1;i<=x;i++){
	    											if (pieces[y][i]!=-1){
	    												if (pieces[y][i]>5){
	    													firstClick=true;
	    													return;
	    												}
	    												else{
	    													if (i!=x){
	    														firstClick=true;
	    														return;
	    													}
	    												}
	    											}
	    										}
	    									} 
	    								}
	    								if (yc==7 && xc==7) wr1=true;
	    								else if (yc==0 && xc==7) wr2=true;
	    								//The rook has moved.
	    							}
	    							if (type==2){
	    								//See type==8
	    								if (x==xc && y==yc){
	    									firstClick = true; 
	    									return;
	    								}
	    								if (x!=xc && y!=yc){
	    									firstClick=true;
	    									return;
	    								}
	    								int i,j;
	    								if (x==xc){
	    									if (y<yc){
	    										for (i=yc-1;i>=y;i--){
	    											if (pieces[i][x]!=-1){
	    												if (pieces[i][x]<=5){
	    													firstClick=true;
	    													return;
	    												}
	    												else{
	    													if (i!=y){
	    														firstClick=true;
	    														return;
	    													}
	    												}
	    											}
	    										}	   
	    									}
	    									else{
	    										for (i=yc+1;i<=y;i++){
	    											if (pieces[i][x]!=-1){
	    												if (pieces[i][x]<=5){
	    													firstClick=true;
	    													return;
	    												}
	    												else{
	    													if (i!=y){
	    														firstClick=true;
	    														return;
	    													}
	    												}
	    											}
	    										}	
	    									} 
	    								}
	    								else if (y==yc){
	    									if (x<xc){
	    										for (i=xc-1;i>=x;i--){
	    											if (pieces[y][i]!=-1){
	    												if (pieces[y][i]<=5){
	    													firstClick=true;
	    													return;
	    												}
	    												else{
	    													if (i!=x){
	    														firstClick=true;
	    														return;
	    													}
	    												}
	    											}
	    										}	   
	    									}
	    									else{
	    										for (i=xc+1;i<=x;i++){
	    											if (pieces[y][i]!=-1){
	    												if (pieces[y][i]<=5){
	    													firstClick=true;
	    													return;
	    												}
	    												else{
	    													if (i!=x){
	    														firstClick=true;
	    														return;
	    													}
	    												}
	    											}
	    										}
	    									} 
	    								}
	    								if (yc==7 && xc==0) br1=true;
	    								else if (yc==0 && xc==0) br2=true;
	    							}
	    							if (type==10){
	    								//Black bishop movement.
	    								if (x==xc || y==yc){
	    									firstClick = true; 
	    									return;
	    								}
	    								int i,j,r1=xc,r2=yc;
	    								//Check all points on the diagonal between the bishop and the click to see if there is a piece in between.
	    								if (x<xc && y<yc){
	    									while (true){
	    										r1--;
	    										r2--;
	    										if (r1==x && r2==y) break;
	    										if (r1<0 || r2<0){
	    											firstClick=true;
	    											return;
	    										}
	    									}
	    									j=yc-1;
	    									for (i=xc-1;i>=x;i--){
	    										if (pieces[j][i]!=-1 && pieces[j][i]>5){
	    											firstClick=true;
	    											return;
	    										}
	    										if (pieces[j][i]!=-1 && pieces[j][i]<=5 && i!=x){
	    											firstClick=true;
	    											return;
	    										}
	    										j--; 
	    									}
	    								}
	    								if (x<xc && y>yc){
	    									while (true){
	    										r1--;
	    										r2++;
	    										if (r1==x && r2==y) break;
	    										if (r1<0 || r2>7){
	    											firstClick=true;
	    											return;
	    										}
	    									}
	    									j=yc+1;
	    									for (i=xc-1;i>=x;i--){
	    										if (pieces[j][i]!=-1 && pieces[j][i]>5){
	    											firstClick=true;
	    											return;
	    										}
	    										if (pieces[j][i]!=-1 && pieces[j][i]<=5 && i!=x){
	    											firstClick=true;
	    											return;
	    										}
	    										j++; 
	    									}
	    								}
	    								if (x>xc && y<yc){
	    									while (true){
	    										r1++;
	    										r2--;
	    										if (r1==x && r2==y) break;
	    										if (r1>7 || r2<0){
	    											firstClick=true;
	    											return;
	    										}
	    									}
	    									j=yc-1;
	    									for (i=xc+1;i<=x;i++){
	    										if (pieces[j][i]!=-1 && pieces[j][i]>5){
	    											firstClick=true;
	    											return;
	    										}
	    										if (pieces[j][i]!=-1 && pieces[j][i]<=5 && i!=x){
	    											firstClick=true;
	    											return;
	    										}
	    										j--; 
	    									}
	    								}
	    								if (x>xc && y>yc){
	    									while (true){
	    										r1++;
	    										r2++;
	    										if (r1==x && r2==y) break;
	    										if (r1>7 || r2>7){
	    											firstClick=true;
	    											return;
	    										}
	    									}
	    									j=yc+1;
	    									for (i=xc+1;i<=x;i++){
	    										if (pieces[j][i]!=-1 && pieces[j][i]>5){
	    											firstClick=true;
	    											return;
	    										}
	    										if (pieces[j][i]!=-1 && pieces[j][i]<=5 && i!=x){
	    											firstClick=true;
	    											return;
	    										}
	    										j++; 
	    									}
	    								}
	    								//Bishop can move.
	    							}
	    							if (type==4){
	    								//See above.
	    								if (x==xc || y==yc){
	    									firstClick = true; 
	    									return;
	    								}
	    								int r1=xc,r2=yc,i,j;
	    								if (x<xc && y<yc){
	    									while (1<10){
	    										r1--;
	    										r2--;
	    										if (r1==x && r2==y) break;
	    										if (r1<0 || r2<0){
	    											firstClick=true;
	    											return;
	    										}
	    									}
	    									j=yc-1;
	    									for (i=xc-1;i>=x;i--){
	    										if (pieces[j][i]!=-1 && pieces[j][i]>5){
	    											firstClick=true;
	    											return;
	    										}
	    										if (pieces[j][i]!=-1 && pieces[j][i]<=5 && i!=x){
	    											firstClick=true;
	    											return;
	    										}
	    										j--; 
	    									}
	    								}
	    								if (x<xc && y>yc){
	    									while (1<10){
	    										r1--;
	    										r2++;
	    										if (r1==x && r2==y) break;
	    										if (r1<0 || r2>7){
	    											firstClick=true;
	    											return;
	    										}
	    									}
	    									j=yc+1;
	    									for (i=xc-1;i>=x;i--){
	    										if (pieces[j][i]!=-1 && pieces[j][i]<=5){
	    											firstClick=true;
	    											return;
	    										}
	    										if (pieces[j][i]!=-1 && pieces[j][i]>5 && i!=x){
	    											firstClick=true;
	    											return;
	    										}
	    										j++; 
	    									}
	    								}
	    								if (x>xc && y<yc){
	    									while (1<10){
	    										r1++;
	    										r2--;
	    										if (r1==x && r2==y) break;
	    										if (r1>7 || r2<0){
	    											firstClick=true;
	    											return;
	    										}
	    									}
	    									j=yc-1;
	    									for (i=xc+1;i<=x;i++){
	    										if (pieces[j][i]!=-1 && pieces[j][i]<=5){
	    											firstClick=true;
	    											return;
	    										}
	    										if (pieces[j][i]!=-1 && pieces[j][i]>5 && i!=x){
	    											firstClick=true;
	    											return;
	    										}
	    										j--; 
	    									}
	    								}
	    								if (x>xc && y>yc){
	    									while (1<10){
	    										r1++;
	    										r2++;
	    										if (r1==x && r2==y) break;
	    										if (r1>7 || r2>7){
	    											firstClick=true;
	    											return;
	    										}
	    									}
	    									j=yc+1;
	    									for (i=xc+1;i<=x;i++){
	    										if (pieces[j][i]!=-1 && pieces[j][i]<=5){
	    											firstClick=true;
	    											return;
	    										}
	    										if (pieces[j][i]!=-1 && pieces[j][i]>5 && i!=x){
	    											firstClick=true;
	    											return;
	    										}
	    										j++; 
	    									}
	    								}
	    							}
	    							if (type==1){
	    								//Queen movement is the combination of rook and bishop movement.
	    								int i,j;
	    								if (x==xc && y==yc){
	    									firstClick = true; 
	    									return;
	    								}
	    								if (x==xc){
	    									if (y<yc){
	    										for (i=yc-1;i>=y;i--){
	    											if (pieces[i][x]!=-1){
	    												if (pieces[i][x]<=5){
	    													firstClick=true;
	    													return;
	    												}
	    												else{
	    													if (i!=y){
	    														firstClick=true;
	    														return;
	    													}
	    												}
	    											}
	    										}	   
	    									}
	    									else{
	    										for (i=yc+1;i<=y;i++){
	    											if (pieces[i][x]!=-1){
	    												if (pieces[i][x]<=5){
	    													firstClick=true;
	    													return;
	    												}
	    												else{
	    													if (i!=y){
	    														firstClick=true;
	    														return;
	    													}
	    												}
	    											}
	    										}
	    									} 
	    								}
	    								else if (y==yc){
	    									if (x<xc){
	    										for (i=xc-1;i>=x;i--){
	    											if (pieces[y][i]!=-1){
	    												if (pieces[y][i]<=5){
	    													firstClick=true;
	    													return;
	    												}
	    												else{
	    													if (i!=x){
	    														firstClick=true;
	    														return;
	    													}
	    												}
	    											}
	    										}	   
	    									}
	    									else{
	    										for (i=xc+1;i<=x;i++){
	    											if (pieces[y][i]!=-1){
	    												if (pieces[y][i]<=5){
	    													firstClick=true;
	    													return;
	    												}
	    												else{
	    													if (i!=x){
	    														firstClick=true;
	    														return;
	    													}
	    												}
	    											}
	    										}
	    									} 
	    								}
	    								else{
	    									int r1=xc,r2=yc;
	    									//case 1
	    									if (x<xc && y<yc){
	    										while (1<10){
	    											r1--;
	    											r2--;
	    											if (r1==x && r2==y) break;
	    											if (r1<0 || r2<0){
	    												firstClick=true;
	    												return;
	    											}
	    										}
	    										j=yc-1;
	    										for (i=xc-1;i>=x;i--){
	    											if (pieces[j][i]!=-1 && pieces[j][i]<=5){
	    												firstClick=true;
	    												return;
	    											}
	    											if (pieces[j][i]!=-1 && pieces[j][i]>5 && i!=x){
	    												firstClick=true;
	    												return;
	    											}
	    											j--; 
	    										}
	    									}
	    									//case 2
	    									if (x<xc && y>yc){
	    										while (1<10){
	    											r1--;
	    											r2++;
	    											if (r1==x && r2==y) break;
	    											if (r1<0 || r2>7){
	    												firstClick=true;
	    												return;
	    											}
	    										}
	    										j=yc+1;
	    										for (i=xc-1;i>=x;i--){
	    											if (pieces[j][i]!=-1 && pieces[j][i]<=5){
	    												firstClick=true;
	    												return;
	    											}
	    											if (pieces[j][i]!=-1 && pieces[j][i]>5 && i!=x){
	    												firstClick=true;
	    												return;
	    											}
	    											j++; 
	    										}
	    									}
	    									//case 3
	    									if (x>xc && y<yc){
	    										while (1<10){
	    											r1++;
	    											r2--;
	    											if (r1==x && r2==y) break;
	    											if (r1>7 || r2<0){
	    												firstClick=true;
	    												return;
	    											}
	    										}
	    										j=yc-1;
	    										for (i=xc+1;i<=x;i++){
	    											if (pieces[j][i]!=-1 && pieces[j][i]<=5){
	    												firstClick=true;
	    												return;
	    											}
	    											if (pieces[j][i]!=-1 && pieces[j][i]>5 && i!=x){
	    												firstClick=true;
	    												return;
	    											}
	    											j--; 
	    										}
	    									}
	    									//case 4
	    									if (x>xc && y>yc){
	    										while (1<10){
	    											r1++;
	    											r2++;
	    											if (r1==x && r2==y) break;
	    											if (r1>7 || r2>7){
	    												firstClick=true;
	    												return;
	    											}
	    										}
	    										j=yc+1;
	    										for (i=xc+1;i<=x;i++){
	    											if (pieces[j][i]!=-1 && pieces[j][i]<=5){
	    												firstClick=true;
	    												return;
	    											}
	    											if (pieces[j][i]!=-1 && pieces[j][i]>5 && i!=x){
	    												firstClick=true;
	    												return;
	    											}
	    											j++; 
	    										}
	    									}
	    								}
	    							}
	    							if (type==7){
	    								//Queen movement is the combination of rook and bishop movement,
	    								if (x==xc && y==yc){
	    									firstClick = true; 
	    									return;
	    								}
	    								int i,j;
	    								if (x==xc){
	    									if (y<yc){
	    										for (i=yc-1;i>=y;i--){
	    											if (pieces[i][x]!=-1){
	    												if (pieces[i][x]>5){
	    													firstClick=true;
	    													return;
	    												}
	    												else{
	    													if (i!=y){
	    														firstClick=true;
	    														return;
	    													}
	    												}
	    											}
	    										}	   
	    									}
	    									else{
	    										for (i=yc+1;i<=y;i++){
	    											if (pieces[i][x]!=-1){
	    												if (pieces[i][x]>5){
	    													firstClick=true;
	    													return;
	    												}
	    												else{
	    													if (i!=y){
	    														firstClick=true;
	    														return;
	    													}
	    												}
	    											}
	    										}
	    									} 
	    								}
	    								else if (y==yc){
	    									if (x<xc){
	    										for (i=xc-1;i>=x;i--){
	    											if (pieces[y][i]!=-1){
	    												if (pieces[y][i]>5){
	    													firstClick=true;
	    													return;
	    												}
	    												else{
	    													if (i!=x){
	    														firstClick=true;
	    														return;
	    													}
	    												}
	    											}
	    										}	   
	    									}
	    									else{
	    										for (i=xc+1;i<=x;i++){
	    											if (pieces[y][i]!=-1){
	    												if (pieces[y][i]>5){
	    													firstClick=true;
	    													return;
	    												}
	    												else{
	    													if (i!=x){
	    														firstClick=true;
	    														return;
	    													}
	    												}
	    											}
	    										}
	    									} 
	    								}
	    								else{
	    									int r1=xc,r2=yc;
	    									//case 1
	    									if (x<xc && y<yc){
	    										while (1<10){
	    											r1--;
	    											r2--;
	    											if (r1==x && r2==y) break;
	    											if (r1<0 || r2<0){
	    												firstClick=true;
	    												return;
	    											}
	    										}
	    										j=yc-1;
	    										for (i=xc-1;i>=x;i--){
	    											if (pieces[j][i]!=-1 && pieces[j][i]>5){
	    												firstClick=true;
	    												return;
	    											}
	    											if (pieces[j][i]!=-1 && pieces[j][i]<=5 && i!=x){
	    												firstClick=true;
	    												return;
	    											}
	    											j--; 
	    										}
	    									}
	    									//case 2
	    									if (x<xc && y>yc){
	    										while (true){
	    											r1--;
	    											r2++;
	    											if (r1==x && r2==y) break;
	    											if (r1<0 || r2>7){
	    												firstClick=true;
	    												return;
	    											}
	    										}
	    										j=yc+1;
	    										for (i=xc-1;i>=x;i--){
	    											if (pieces[j][i]!=-1 && pieces[j][i]>5){
	    												firstClick=true;
	    												return;
	    											}
	    											if (pieces[j][i]!=-1 && pieces[j][i]<=5 && i!=x){
	    												firstClick=true;
	    												return;
	    											}
	    											j++; 
	    										}
	    									}
	    									//case 3
	    									if (x>xc && y<yc){
	    										while (true){
	    											r1++;
	    											r2--;
	    											if (r1==x && r2==y) break;
	    											if (r1>7 || r2<0){
	    												firstClick=true;
	    												return;
	    											}
	    										}
	    										j=yc-1;
	    										for (i=xc+1;i<=x;i++){
	    											if (pieces[j][i]!=-1 && pieces[j][i]>5){
	    												firstClick=true;
	    												return;
	    											}
	    											if (pieces[j][i]!=-1 && pieces[j][i]<=5 && i!=x){
	    												firstClick=true;
	    												return;
	    											}
	    											j--; 
	    										}
	    									}
	    									//case 4
	    									if (x>xc && y>yc){
	    										while (true){
	    											r1++;
	    											r2++;
	    											if (r1==x && r2==y) break;
	    											if (r1>7 || r2>7){
	    												firstClick=true;
	    												return;
	    											}
	    										}
	    										j=yc+1;
	    										for (i=xc+1;i<=x;i++){
	    											if (pieces[j][i]!=-1 && pieces[j][i]>5){
	    												firstClick=true;
	    												return;
	    											}
	    											if (pieces[j][i]!=-1 && pieces[j][i]<=5 && i!=x){
	    												firstClick=true;
	    												return;
	    											}
	    											j++;
	    										}
	    									}
	    								}
	    							}
	    							if (type==3 || type==9){
	    								//Knight movement; checking whether click location is viable.
	    								if (xc-x>=3 || x-xc>=3 || y-yc>=3 || yc-y>=3){
	    									firstClick = true; 
	    									return;
	    								}
	    								if (x==xc){
	    									firstClick = true; 
	    									return;
	    								}
	    								if (y==yc){
	    									firstClick = true; 
	    									return;
	    								}
	    								if (x==xc+2){ 
	    									if (y!=yc+1 && y!=yc-1){
	    										firstClick = true; 
	    										return;
	    									}
	    								}
	    								if (x==xc+1){
	    									if (y!=yc+2 && y!=yc-2){
	    										firstClick = true; 
	    										return; 
	    									}
	    								}
	    								if (x==xc-2){
	    									if (yc!=y+1 && yc!=y-1){
	    										firstClick = true; 
	    										return; 
	    									}
	    								}
	    								if (x==xc-1){
	    									if (y!=yc+2 && y!=yc-2){
	    										firstClick = true; 
	    										return; 
	    									}
	    								}
	    								if (type==3 && pieces[y][x]>=0 && pieces[y][x]<=5){
	    									firstClick=true;
	    									return;
	    								}
	    								if (type==9 && pieces[y][x]>5){
	    									firstClick=true;
	    									return;
	    								}
	    							}
	    							if (type == 5){
	    								//Pawn movement; pretty simple.
	    								if (x<=xc){
	    									firstClick = true; 
	    									return; 
	    								}
	    								if (xc==1){
	    									//Pawn can move two spaces in the beginning.
	    									if (!(x==xc+1||x==xc+2) && y==yc){
	    										firstClick = true; 
	    										return;
	    									}
	    									if (x==xc+2 && y==yc && pieces[yc][xc+2]!=-1){
	    										firstClick = true; 
	    										return;
	    									}
	    									if (x==xc-2 && y==yc && pieces[yc][xc-1]!=-1){
	    										firstClick = true; 
	    										return;
	    									}
	    									if (x==xc+1 && y==yc && pieces[yc][xc+1]!=-1){
	    										firstClick = true; 
	    										return;
	    									}
	    									if (x==xc+1 && y==yc+1 && pieces[y][x]<=5){
	    										firstClick=true; 
	    										return;
	    									}
	    									if (y!=yc){
	    										if (x!=xc+1){
	    											firstClick=true; 
	    											return;
	    										}
	    										if (yc!=y-1 && yc!=y+1){
	    											firstClick=true; 
	    											return;
	    										}
	    										if (x==xc+1 && pieces[y][x]<=5){
	    											firstClick=true; 
	    											return;
	    										}
	    									}
	    								}
	    								else{
	    									if (!(x==xc+1) && y==yc){
	    										firstClick = true;
	    										return;
	    									}
	    									if (x==xc+1 && y==yc && pieces[yc][xc+1]!=-1){
	    										firstClick = true;
	    										return;
	    									}
	    									if (y!=yc){
	    										if (x!=xc+1){
	    											firstClick=true; 
	    											return;
	    										}
	    										if (yc!=y-1 && yc!=y+1){
	    											firstClick=true; 
	    											return;
	    										}
	    										if (pieces[y][x]<=5){
	    											firstClick = true; 
	    											return;
	    										}
	    									}
	    								}
	    							}
	    							if (type==11){
	    								//See above
	    								if (x>=xc){
	    									firstClick = true;  
	    									return; 
	    								}
	    								if (xc==6){
	    									if (!(x==xc-1 || x==xc-2) && y==yc) {
	    										firstClick = true; 
	    										return;
	    									}
	    									if (x==xc-2 && y==yc && pieces[yc][xc-2]!=-1){
	    										firstClick = true; 
	    										return;
	    									}
	    									if (x==xc-2 && y==yc && pieces[yc][xc-1]!=-1){
	    										firstClick = true; 
	    										return;
	    									}
	    									if (x==xc-1 && y==yc && pieces[yc][xc-1]!=-1){
	    										firstClick = true; 
	    										return;
	    									}
	    									if (y!=yc){
	    										if (x!=xc-1){
	    											firstClick=true; 
	    											return;
	    										}
	    										if (yc!=y-1 && yc!=y+1){
	    											firstClick=true; 
	    											return;
	    										}
	    										if (x==xc-1 && (pieces[y][x]>5 || pieces[y][x]==-1)){
	    											firstClick = true; 
	    											return;
	    										}
	    									}
	    								}
	    								else{
	    									if (!(x==xc-1) && y==yc){
	    										firstClick=true;
	    										return;
	    									}
	    									if (x==xc-1 && y==yc && pieces[yc][xc-1]!=-1){
	    										firstClick = true;
	    										return;
	    									}
	    									if (yc!=y){
	    										if (x!=xc-1){
	    											firstClick=true; 
	    											return;
	    										}
	    										if (yc!=y-1 && yc!=y+1){
	    											firstClick=true; 
	    											return;
	    										}
	    										if (x==xc-1 && (pieces[y][x]>5 || pieces[y][x]==-1)){ 
	    											firstClick=true; 
	    											return;
	    										}
	    									}
	    								}
	    							}
	    							int previous=pieces[y][x];
	    							pieces[y][x]=type;
	    							pieces[yc][xc]=-1;
	    							//will the king be in check
	    							if (turn==false){
	    								//From the king's position, check to see if there is any piece that can attack it.
	    								//Go through the diagonals, up and down, and knight movement, and if there is a piece that can attack the king, the king would be in check.
	    								int i,j=0;
	    								boolean breaker=false;
	    								for (i=0;i<8;i++){
	    									for (j=0;j<8;j++){
	    										if (pieces[j][i]==0){
	    											breaker=true;
	    											break;
	    										}
	    									}
	    									if (breaker==true) break;
	    								}
	    								int tx=i, ty=j;
	    								//bishops first
	    								while (true){
	    									tx--;
						            		ty--;
						            		if (tx<0 || ty<0) break;
						            		if (pieces[ty][tx]==10 || pieces[ty][tx]==7){
						            			firstClick=true;
						            			moves.add(j*100+i);
						            			chessBoardSquares[j][i].setBackground(Check);
						            			pieces[y][x]=previous;
						            			pieces[yc][xc]=type;
						            			return;
						            		}
						            		if (pieces[ty][tx]!=-1) break;
						            	}	            	
	    								tx=i; ty=j;
						            	while (true){
						            		tx--;
						            		ty++;
						            		if (tx<0 || ty>7) break;
						            		if (pieces[ty][tx]==10 || pieces[ty][tx]==7){
						            			firstClick=true;
						            			pieces[y][x]=previous;
						            			pieces[yc][xc]=type;
						            			moves.add(j*100+i);
						            			chessBoardSquares[j][i].setBackground(Check);
						            			return;
						            		}
						            		if (pieces[ty][tx]!=-1) break;
						            	}	            	
						            	tx=i; ty=j;
						            	while (true){
						            		tx++;
						            		ty++;
						            		if (tx>7 || ty>7) break;
						            		if (pieces[ty][tx]==10 || pieces[ty][tx]==7){
						            			firstClick=true;
						            			pieces[y][x]=previous;
						            			moves.add(j*100+i);
						            			chessBoardSquares[j][i].setBackground(Check);
						            			pieces[yc][xc]=type;
						            			return;
						            		}
						            		if (pieces[ty][tx]!=-1) break;
						            	}       	
						            	tx=i; ty=j;
						            	while (true){
						            		tx++;
						            		ty--;
						            		if (tx>7 || ty<0) break;
						            		if (pieces[ty][tx]==10 || pieces[ty][tx]==7){
						            			firstClick=true;
						            			moves.add(j*100+i);
						            			chessBoardSquares[j][i].setBackground(Check);
						            			pieces[y][x]=previous;
						            			pieces[yc][xc]=type;
						            			return;
						            		}
						            		if (pieces[ty][tx]!=-1) break;
						            	}	            	
						            	//rooks next
						            	tx=i; ty=j;
						            	while (true){
						            		tx--;
						            		if (tx<0) break;
						            		if (pieces[ty][tx]==8 || pieces[ty][tx]==7){
						            			firstClick=true;
						            			moves.add(j*100+i);
						            			chessBoardSquares[j][i].setBackground(Check);
						            			pieces[y][x]=previous;
						            			pieces[yc][xc]=type;
						            			return;
						            		}
						            		if (pieces[ty][tx]!=-1) break;
						            	}	            	
						            	tx=i; ty=j;
						            	while (true){
						            		tx++;
						            		if (tx>7) break;
						            		if (pieces[ty][tx]==8 || pieces[ty][tx]==7){
						            			firstClick=true;
						            			pieces[y][x]=previous;
						            			pieces[yc][xc]=type;
						            			moves.add(j*100+i);
						            			chessBoardSquares[j][i].setBackground(Check);
						            			return;
						            		}
						            		if (pieces[ty][tx]!=-1) break;
						            	}
						            	tx=i; ty=j;
						            	while (true){
						            		ty--;
						            		if (ty<0) break;
						            		if (pieces[ty][tx]==8 || pieces[ty][tx]==7){
						            			firstClick=true;
						            			pieces[yc][xc]=type;
						            			moves.add(j*100+i);
						            			chessBoardSquares[j][i].setBackground(Check);
						            			pieces[y][x]=previous;
						            			return;
						            		}
						            		if (pieces[ty][tx]!=-1) break;
						            	}	            	
						            	tx=i; ty=j;
						            	while (true){
						            		ty++;
						            		if (ty>7) break;
						            		if (pieces[ty][tx]==8 || pieces[ty][tx]==7){
						            			firstClick=true;
						            			pieces[y][x]=previous;
						            			moves.add(j*100+i);
						            			chessBoardSquares[j][i].setBackground(Check);
						            			pieces[yc][xc]=type;
						            			return;
						            		}
						            		if (pieces[ty][tx]!=-1) break;
						            	}	            	
						            	//knights
						            	tx=i; ty=j;
						            	if (tx+1<8 && ty+2<8 && pieces[ty+2][tx+1]==9){
						            		firstClick=true;
					            			pieces[y][x]=previous;
					            			moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[yc][xc]=type;
					            			return;
						            	}	            	
						            	if (tx+1<8 && ty-2>=0 && pieces[ty-2][tx+1]==9){
						            		firstClick=true;
					            			pieces[y][x]=previous;
					            			moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[yc][xc]=type;
					            			return;
						            	}	            	
						            	if (tx+2<8 && ty+1<8 && pieces[ty+1][tx+2]==9){
						            		firstClick=true;
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			return;
						            	}	            	
						            	if (tx+2<8 && ty-1>=0 && pieces[ty-1][tx+2]==9){
						            		firstClick=true;
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			return;
						            	}	            	
						            	if (tx-1>=0 && ty+2<8 && pieces[ty+2][tx-1]==9){
						            		firstClick=true;
					            			pieces[y][x]=previous;
					            			moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[yc][xc]=type;
					            			return;
						            	}	            
						            	if (tx-2>=0 && ty-1>=0 && pieces[ty-1][tx-2]==9){
						            		firstClick=true;
						            		moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;	
					            			return;
						            	}	            	
						            	if (tx-2>=0 && ty+1<8 && pieces[ty+1][tx-2]==9){
						            		firstClick=true;
						            		moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			return;
						            	}	            
						            	if (tx-1>=0 && ty-2>=0 && pieces[ty-2][tx-1]==9){
						            		firstClick=true;
						            		moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			return;
						            	}	            	
						            	//pawns
						            	if (ty+1<8 && tx+1<8 && pieces[ty+1][tx+1]==11){
						            		firstClick=true;
					            			pieces[y][x]=previous;
					            			moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[yc][xc]=type;
					            			return;
						            	}	            	
						            	if (ty-1>=0 && tx+1<8 && pieces[ty-1][tx+1]==11){
						            		firstClick=true;
						            		moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			return;
						            	}
						            	//kings
						            	if (ty+1<8 && tx+1<8 && pieces[ty+1][tx+1]==6){
						            		firstClick=true;
						            		moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			return;
						            	}
						            	if (ty+1<8 && tx-1>=0 && pieces[ty+1][tx-1]==6){
						            		firstClick=true;
						            		moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			return;
						            	}
						            	if (ty+1<8 && pieces[ty+1][tx]==6){
						            		firstClick=true;
						            		moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			return;
						            	}
						            	if (ty-1>=0 && pieces[ty-1][tx]==6){
						            		firstClick=true;
						            		moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			return;
						            	}
						            	if (ty-1>=0 && tx+1<8 && pieces[ty-1][tx+1]==6){
						            		firstClick=true;
						            		moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			return;
						            	}
						            	if (ty-1>=0 && tx-1>=0 && pieces[ty-1][tx-1]==6){
						            		firstClick=true;
						            		moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			return;
						            	}
						            	if (tx+1<8 && pieces[ty][tx+1]==6){
						            		firstClick=true;
						            		moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			return;
						            	}
						            	if (tx-1>=0 && pieces[ty][tx-1]==6){
						            		firstClick=true;
						            		moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			return;
						            	}
	    							}
	    							if (turn==true){
	    								//See above; check for other king
	    								int i,j=0;
	    								boolean breaker=false;
	    								for (i=0;i<8;i++){
	    									for (j=0;j<8;j++){
	    										if (pieces[j][i]==6){
	    											breaker=true;
	    											break;
	    										}
	    									}
	    									if (breaker==true) break;
	    								}
	    								int tx=i, ty=j;
	    								//bishops first
	    								while (true){
	    									tx--;
	    									ty--;
	    									if (tx<0 || ty<0) break;
	    									if (pieces[ty][tx]==4 || pieces[ty][tx]==1){
	    										firstClick=true;
	    										moves.add(j*100+i);
	    										chessBoardSquares[j][i].setBackground(Check);
	    										pieces[y][x]=previous;
	    										pieces[yc][xc]=type;
	    										return;
	    									}
	    									if (pieces[ty][tx]!=-1) break;
	    								}
						            	tx=i; ty=j;
						            	while (true){
						            		tx--;
						            		ty++;
						            		if (tx<0 || ty>7) break;
						            		if (pieces[ty][tx]==4 || pieces[ty][tx]==1){
						            			firstClick=true;
						            			pieces[y][x]=previous;	  
						            			pieces[yc][xc]=type;
						            			moves.add(j*100+i);
						            			chessBoardSquares[j][i].setBackground(Check);
						            			return;
						            		}
						            		if (pieces[ty][tx]!=-1) break;
						            	}
						            	tx=i; ty=j;
						            	while (true){
						            		tx++;
						            		ty++;
						            		if (tx>7 || ty>7) break;
						            		if (pieces[ty][tx]==4 || pieces[ty][tx]==1){
						            			firstClick=true;
						            			pieces[y][x]=previous;
						            			moves.add(j*100+i);
						            			chessBoardSquares[j][i].setBackground(Check);
						            			pieces[yc][xc]=type;
						            			return;
						            		}
						            		if (pieces[ty][tx]!=-1) break;
						            	}
						            	tx=i; ty=j;
						            	while (true){
						            		tx++;
						            		ty--;
						            		if (tx>7 || ty<0) break;
						            		if (pieces[ty][tx]==4 || pieces[ty][tx]==1){
						            			firstClick=true;
						            			moves.add(j*100+i);
						            			chessBoardSquares[j][i].setBackground(Check);
						            			pieces[y][x]=previous;
						            			pieces[yc][xc]=type;
						            			return;
						            		}
						            		if (pieces[ty][tx]!=-1) break;
						            	}
						            	//rooks next
						            	tx=i; ty=j;
						            	while (true){
						            		tx--;
						            		if (tx<0) break;
						            		if (pieces[ty][tx]==2 || pieces[ty][tx]==1){
						            			firstClick=true;
						            			pieces[y][x]=previous;
						            			moves.add(j*100+i);
						            			chessBoardSquares[j][i].setBackground(Check);
						            			pieces[yc][xc]=type;
						            			return;
						            		}
						            		if (pieces[ty][tx]!=-1) break;
						            	}
						            	tx=i; ty=j;
						            	while (true){
						            		tx++;
						            		if (tx>7) break;
						            		if (pieces[ty][tx]==2 || pieces[ty][tx]==1){
						            			firstClick=true;
						            			pieces[y][x]=previous;
						            			moves.add(j*100+i);
						            			chessBoardSquares[j][i].setBackground(Check);
						            			pieces[yc][xc]=type;
						            			return;
						            		}       		
						            		if (pieces[ty][tx]!=-1) break;
						            	}
						            	tx=i; ty=j;
						            	while (true){
						            		ty--;
						            		if (ty<0) break;
						            		if (pieces[ty][tx]==2 || pieces[ty][tx]==1){
						            			firstClick=true;
						            			pieces[yc][xc]=type;
						            			pieces[y][x]=previous;
						            			moves.add(j*100+i);
						            			chessBoardSquares[j][i].setBackground(Check);
						            			return;
						            		}
						            		if (pieces[ty][tx]!=-1) break;
						            	}
						            	tx=i; ty=j;
						            	while (true){
						            		ty++;
						            		if (ty>7) break;
						            		if (pieces[ty][tx]==2 || pieces[ty][tx]==1){
						            			firstClick=true;
						            			moves.add(j*100+i);
						            			chessBoardSquares[j][i].setBackground(Check);
						            			pieces[y][x]=previous;
						            			pieces[yc][xc]=type;
						            			return;
						            		}
						            		if (pieces[ty][tx]!=-1) break;
						            	}
						            	//knights
						            	tx=i; ty=j;
						            	if (tx+1<8 && ty+2<8 && pieces[ty+2][tx+1]==3){
						            		firstClick=true;
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			return;
						            	}
						            	if (tx+1<8 && ty-2>=0 && pieces[ty-2][tx+1]==3){
						            		firstClick=true;
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			return;
						            	}
						            	if (tx+2<8 && ty+1<8 && pieces[ty+1][tx+2]==3){
						            		firstClick=true;
					            			pieces[y][x]=previous;
					            			moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[yc][xc]=type;
					            			return;
						            	}
						            	if (tx+2<8 && ty-1>=0 && pieces[ty-1][tx+2]==3){
						            		firstClick=true;
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			return;
						            	}
						            	if (tx-1>=0 && ty+2<8 && pieces[ty+2][tx-1]==3){
						            		firstClick=true;
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			return;
						            	}
						            	if (tx-2>=0 && ty-1>=0 && pieces[ty-1][tx-2]==3){
						            		firstClick=true;
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;	
					            			moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			return;
						            	}
						            	if (tx-2>=0 && ty+1<8 && pieces[ty+1][tx-2]==3){
						            		firstClick=true;
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			return;
						            	}
						            	if (tx-1>=0 && ty-2>=0 && pieces[ty-2][tx-1]==3){
						            		firstClick=true;
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			return;
						            	}
						            	//pawns
						            	if (ty+1<8 && tx-1>=0 && pieces[ty+1][tx-1]==5){
						            		firstClick=true;
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			return;
						            	}
						            	if (ty-1>=0 && tx-1>=0 && pieces[ty-1][tx-1]==5){
						            		firstClick=true;
					            			pieces[y][x]=previous;
					            			moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[yc][xc]=type;
					            			return;
						            	}
						            	//kings
						            	if (ty+1<8 && tx+1<8 && pieces[ty+1][tx+1]==0){
						            		firstClick=true;
						            		moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			return;
						            	}
						            	if (ty+1<8 && tx-1>=0 && pieces[ty+1][tx-1]==0){
						            		firstClick=true;
						            		moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			return;
						            	}
						            	if (ty+1<8 && pieces[ty+1][tx]==0){
						            		firstClick=true;
						            		moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			return;
						            	}
						            	if (ty-1>=0 && pieces[ty-1][tx]==0){
						            		firstClick=true;
						            		moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			return;
						            	}
						            	if (ty-1>=0 && tx+1<8 && pieces[ty-1][tx+1]==0){
						            		firstClick=true;
						            		moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			return;
						            	}
						            	if (ty-1>=0 && tx-1>=0 && pieces[ty-1][tx-1]==0){
						            		firstClick=true;
						            		moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			return;
						            	}
						            	if (tx+1<8 && pieces[ty][tx+1]==0){
						            		firstClick=true;
						            		moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			return;
						            	}
						            	if (tx-1>=0 && pieces[ty][tx-1]==0){
						            		firstClick=true;
						            		moves.add(j*100+i);
					            			chessBoardSquares[j][i].setBackground(Check);
					            			pieces[y][x]=previous;
					            			pieces[yc][xc]=type;
					            			return;
						            	}
	    							}
	    							//Moving the castle and the king in castling.
	    							if (castling1){
	    								if (y==yc+2){
	    									chessBoardSquares[y-1][x].setIcon(new ImageIcon(chessPieceImages[BLACK][2]));
	    									ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
	    									chessBoardSquares[7][0].setIcon(icon);
	    									pieces[y-1][xc]=2;
	    									pieces[7][0]=-1;
	    								}
	    								if (y==yc-2){
	    									chessBoardSquares[y+1][x].setIcon(new ImageIcon(chessPieceImages[BLACK][2]));
	    									ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
	    									chessBoardSquares[0][0].setIcon(icon);
	    									pieces[y+1][xc]=2;
	    									pieces[0][0]=-1;
	    								}
	    								k1=true;
	    							}
	    							if (castling2){
	    								if (y==yc+2){
	    									chessBoardSquares[y-1][x].setIcon(new ImageIcon(chessPieceImages[WHITE][2]));
	    									ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
	    									chessBoardSquares[7][7].setIcon(icon);
	    									pieces[y-1][xc]=8;
	    									pieces[7][7]=-1;
	    								}
	    								if (y==yc-2){
	    									chessBoardSquares[y+1][x].setIcon(new ImageIcon(chessPieceImages[WHITE][2]));
	    									ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
	    									chessBoardSquares[0][7].setIcon(icon);
	    									pieces[y+1][xc]=8;
	    									pieces[0][7]=-1;
	    								}
	    								k2=true;
	    							}
	    							//Replacing old square with an empty square and vice versa.
	    							ImageIcon icon=(ImageIcon)(movingPiece.getIcon());
	    							ImageIcon noIcon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
	    							movingPiece.setIcon(noIcon);
	    							b.setIcon(icon);
	    							//Turn change
	    							if (turn==false) turn=true;
	    							else turn=false;
	    							moves.clear();
	    							//changing pawns to queens when they reach the end
	    							if (type==11 && x==0){
	    								pieces[y][x]=7;
	    								chessBoardSquares[y][x].setIcon(new ImageIcon(chessPieceImages[WHITE][1]));
	    							}
	    							if (type==5 && x==7){
	    								pieces[y][x]=1;
	    								chessBoardSquares[y][x].setIcon(new ImageIcon(chessPieceImages[BLACK][1]));
	    							}
	    							pieces[yc][xc]=-1;
	    							castling1=false;
	    							castling2=false;
	    						}
	    					}
	    				});
	    			}
	    		}
	    	}
	    	for (int ii = 0; ii < 8; ii++) {
	    		for (int jj = 0; jj < 8; jj++) {
	    			switch (jj) {
	    			case 0:
	    			default: chessBoard.add(chessBoardSquares[jj][ii]);
	    			}
	    		}
	    	}
		}
		/**
		 * This function highlights the squares a black pawn can go to.
		 * @param ii x location
		 * @param jj y location
		 */
		public void highlight5(int ii, int jj){
			if (ii+1<8 && pieces[jj][ii+1]==-1){
				moves.add(jj*100+ii+1);
				chessBoardSquares[jj][ii+1].setBackground(Blue);
				if (ii==1 && pieces[jj][ii+2]==-1){
					moves.add(jj*100+ii+2);
					chessBoardSquares[jj][ii+2].setBackground(Blue);
				}
			}
			if (ii+1<8 && jj+1<8 && pieces[jj+1][ii+1]>5){
				moves.add((jj+1)*100+ii+1);
				chessBoardSquares[jj+1][ii+1].setBackground(Attack);
			}
			if (ii+1<8 && jj-1>=0 && pieces[jj-1][ii+1]>5){
				moves.add((jj-1)*100+ii+1);
				chessBoardSquares[jj-1][ii+1].setBackground(Attack);
			}
		}
		/**
		 * This function highlights the squares a white pawn can go to.
		 * @param ii x location
		 * @param jj y location
		 */
		public void highlight11(int ii, int jj){
			if (ii-1>=0 && pieces[jj][ii-1]==-1){
	    		moves.add(jj*100+ii-1);
	    		chessBoardSquares[jj][ii-1].setBackground(Blue);
	    		if (ii==6 && pieces[jj][ii-2]==-1){
	    			moves.add(jj*100+ii-2);
	    			chessBoardSquares[jj][ii-2].setBackground(Blue);
	    		}
	    	}
	    	if (ii-1>=0 && jj+1<8 && pieces[jj+1][ii-1]<=5 && pieces[jj+1][ii-1]!=-1){
	    		moves.add((jj+1)*100+ii-1);
				chessBoardSquares[jj+1][ii-1].setBackground(Attack);
	    	}
	    	if (ii-1>=0 && jj-1>=0 && pieces[jj-1][ii-1]<=5 && pieces[jj-1][ii-1]!=-1){
	    		moves.add((jj-1)*100+ii-1);
				chessBoardSquares[jj-1][ii-1].setBackground(Attack);
	    	}
		}
		/**
		 * This function highlights the squares a black queen can go to.
		 * @param ii x location
		 * @param jj y location
		 */
		public void highlight1(int ii, int jj){
			int tx=ii,ty=jj;
	    	while (true){
	    		tx--;
	    		ty--;
	    		if (tx<0 || ty<0) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1) break;
	    		if (pieces[ty][tx]>5){
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			moves.add(ty*100+tx);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		tx++;
	    		ty--;
	    		if (tx>7 || ty<0) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1) break;
	    		if (pieces[ty][tx]>5){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		tx++;
	    		ty++;
	    		if (tx>7 || ty>7) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1) break;
	    		if (pieces[ty][tx]>5){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		tx--;
	    		ty++;
	    		if (tx<0 || ty>7) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1) break;
	    		if (pieces[ty][tx]>5){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		tx++;
	    		if (tx>7) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1) break;
	    		if (pieces[ty][tx]>5){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		tx--;
	    		if (tx<0) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1) break;
	    		if (pieces[ty][tx]>5){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		ty--;
	    		if (ty<0) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1) break;
	    		if (pieces[ty][tx]>5){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		ty++;
	    		if (ty>7) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1) break;
	    		if (pieces[ty][tx]>5){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
		}
		/**
		 * This function highlights the squares a white queen can go to.
		 * @param ii x location
		 * @param jj y location
		 */
		public void highlight7(int ii, int jj){
			int tx=ii,ty=jj;
	    	while (true){
	    		tx--;
	    		ty--;
	    		if (tx<0 || ty<0) break;
	    		if (pieces[ty][tx]>5) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1){
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			moves.add(ty*100+tx);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		tx++;
	    		ty--;
	    		if (tx>7 || ty<0) break;
	    		if (pieces[ty][tx]>5) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		tx++;
	    		ty++;
	    		if (tx>7 || ty>7) break;
	    		if (pieces[ty][tx]>5) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		tx--;
	    		ty++;
	    		if (tx<0 || ty>7) break;
	    		if (pieces[ty][tx]>5) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		tx++;
	    		if (tx>7) break;
	    		if (pieces[ty][tx]>5) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		tx--;
	    		if (tx<0) break;
	    		if (pieces[ty][tx]>5) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		ty--;
	    		if (ty<0) break;
	    		if (pieces[ty][tx]>5) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		ty++;
	    		if (ty>7) break;
	    		if (pieces[ty][tx]>5) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
		}
		/**
		 * This function highlights the squares a white bishop can go to.
		 * @param ii x location
		 * @param jj y location
		 */
		public void highlight10(int ii, int jj){
			int tx=ii,ty=jj;
	    	while (true){
	    		tx--;
	    		ty--;
	    		if (tx<0 || ty<0) break;
	    		if (pieces[ty][tx]>5) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1){
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			moves.add(ty*100+tx);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		tx++;
	    		ty--;
	    		if (tx>7 || ty<0) break;
	    		if (pieces[ty][tx]>5) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		tx++;
	    		ty++;
	    		if (tx>7 || ty>7) break;
	    		if (pieces[ty][tx]>5) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		tx--;
	    		ty++;
	    		if (tx<0 || ty>7) break;
	    		if (pieces[ty][tx]>5) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
		}
		/**
		 * This function highlights the squares a black bishop can go to.
		 * @param ii x location
		 * @param jj y location
		 */
		public void highlight4(int ii, int jj){
			int tx=ii,ty=jj;
	    	while (true){
	    		tx--;
	    		ty--;
	    		if (tx<0 || ty<0) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1) break;
	    		if (pieces[ty][tx]>5){
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			moves.add(ty*100+tx);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		tx++;
	    		ty--;
	    		if (tx>7 || ty<0) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1) break;
	    		if (pieces[ty][tx]>5){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		tx++;
	    		ty++;
	    		if (tx>7 || ty>7) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1) break;
	    		if (pieces[ty][tx]>5){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		tx--;
	    		ty++;
	    		if (tx<0 || ty>7) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1) break;
	    		if (pieces[ty][tx]>5){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
		}
		/**
		 * This function highlights the squares a black rook can go to.
		 * @param ii x location
		 * @param jj y location
		 */
		public void highlight2(int ii, int jj){
			int tx=ii,ty=jj;
	    	while (true){
	    		tx--;
	    		if (tx<0) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1) break;
	    		if (pieces[ty][tx]>5){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		ty--;
	    		if (ty<0) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1) break;
	    		if (pieces[ty][tx]>5){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}    	
	    	tx=ii;ty=jj;
	    	while (true){
	    		ty++;
	    		if (ty>7) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1) break;
	    		if (pieces[ty][tx]>5){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		tx++;
	    		if (tx>7) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1) break;
	    		if (pieces[ty][tx]>5){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
		}
		/**
		 * This function highlights the squares a white rook can go to.
		 * @param ii x location
		 * @param jj y location
		 */
		public void highlight8(int ii, int jj){
			int tx=ii,ty=jj;
	    	while (true){
	    		tx++;
	    		if (tx>7) break;
	    		if (pieces[ty][tx]>5) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		tx--;
	    		if (tx<0) break;
	    		if (pieces[ty][tx]>5) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		ty--;
	    		if (ty<0) break;
	    		if (pieces[ty][tx]>5) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
	    	tx=ii;ty=jj;
	    	while (true){
	    		ty++;
	    		if (ty>7) break;
	    		if (pieces[ty][tx]>5) break;
	    		if (pieces[ty][tx]<=5 && pieces[ty][tx]!=-1){
	    			moves.add(ty*100+tx);
	    			chessBoardSquares[ty][tx].setBackground(Attack);
	    			break;
	    		}
				chessBoardSquares[ty][tx].setBackground(Blue);
	    		moves.add(ty*100+tx);
	    	}
		}
		/**
		 * This function highlights the squares a black knight can go to.
		 * @param ii x location
		 * @param jj y location
		 */
		public void highlight3(int ii, int jj){
			int tx=ii,ty=jj;
	    	if (tx+1<8 && ty+2<8 && pieces[ty+2][tx+1]==-1){
	    		chessBoardSquares[ty+2][tx+1].setBackground(Blue);
	    		moves.add((ty+2)*100+tx+1);
	    	}
	    	else if (tx+1<8 && ty+2<8 && pieces[ty+2][tx+1]>5){
	    		chessBoardSquares[ty+2][tx+1].setBackground(Attack);
	    		moves.add((ty+2)*100+tx+1);
	    	}
	    	if (tx+1<8 && ty-2>=0 && pieces[ty-2][tx+1]==-1){
	    		chessBoardSquares[ty-2][tx+1].setBackground(Blue);
	    		moves.add((ty-2)*100+tx+1);
	    	}
	    	else if (tx+1<8 && ty-2>=0 && pieces[ty-2][tx+1]>5){
	    		chessBoardSquares[ty-2][tx+1].setBackground(Attack);
	    		moves.add((ty-2)*100+tx+1);
	    	} 	
	    	if (tx+2<8 && ty+1<8 && pieces[ty+1][tx+2]==-1){
	    		chessBoardSquares[ty+1][tx+2].setBackground(Blue);
	    		moves.add((ty+1)*100+tx+2);
	    	}
	    	else if (tx+2<8 && ty+1<8 && pieces[ty+1][tx+2]>5){
	    		chessBoardSquares[ty+1][tx+2].setBackground(Attack);
	    		moves.add((ty+1)*100+tx+2);
	    	}
	    	if (tx+2<8 && ty-1>=0 && pieces[ty-1][tx+2]==-1){
	    		chessBoardSquares[ty-1][tx+2].setBackground(Blue);
	    		moves.add((ty-1)*100+tx+2);
	    	}
	    	else if (tx+2<8 && ty-1>=0 && pieces[ty-1][tx+2]>5){
	    		chessBoardSquares[ty-1][tx+2].setBackground(Attack);
	    		moves.add((ty-1)*100+tx+2);
	    	}
	    	if (tx-1>=0 && ty+2<8 && pieces[ty+2][tx-1]==-1){
	    		chessBoardSquares[ty+2][tx-1].setBackground(Blue);
	    		moves.add((ty+2)*100+tx-1);
	    	}
	    	else if (tx-1>=0 && ty+2<8 && pieces[ty+2][tx-1]>5){
	    		chessBoardSquares[ty+2][tx-1].setBackground(Attack);
	    		moves.add((ty+2)*100+tx-1);
	    	}
	    	if (tx-2>=0 && ty-1>=0 && pieces[ty-1][tx-2]==-1){
	    		chessBoardSquares[ty-1][tx-2].setBackground(Blue);
	    		moves.add((ty-1)*100+tx-2);
	    	}
	    	else if (tx-2>=0 && ty-1>=0 && pieces[ty-1][tx-2]>5){
	    		chessBoardSquares[ty-1][tx-2].setBackground(Attack);
	    		moves.add((ty-1)*100+tx-2);
	    	}
	    	if (tx-2>=0 && ty+1<8 && pieces[ty+1][tx-2]==-1){
	    		chessBoardSquares[ty+1][tx-2].setBackground(Blue);
	    		moves.add((ty+1)*100+tx-2);
	    	}
	    	else if (tx-2>=0 && ty+1<8 && pieces[ty+1][tx-2]>5){
	    		chessBoardSquares[ty+1][tx-2].setBackground(Attack);
	    		moves.add((ty+1)*100+tx-2);
	    	}
	    	if (tx-1>=0 && ty-2>=0 && pieces[ty-2][tx-1]==-1){
	    		chessBoardSquares[ty-2][tx-1].setBackground(Blue);
	    		moves.add((ty-2)*100+tx-1);
	    	}
	    	else if (tx-1>=0 && ty-2>=0 && pieces[ty-2][tx-1]>5){
	    		chessBoardSquares[ty-2][tx-1].setBackground(Attack);
	    		moves.add((ty-2)*100+tx-1);
	    	}
		}
		/**
		 * This function highlights the squares a white knight can go to.
		 * @param ii x location
		 * @param jj y location
		 */
		public void highlight9(int ii, int jj){
			int tx=ii,ty=jj;
	    	if (tx+1<8 && ty+2<8 && pieces[ty+2][tx+1]==-1){
	    		chessBoardSquares[ty+2][tx+1].setBackground(Blue);
	    		moves.add((ty+2)*100+tx+1);
	    	}
	    	else if (tx+1<8 && ty+2<8 && pieces[ty+2][tx+1]<=5){
	    		chessBoardSquares[ty+2][tx+1].setBackground(Attack);
	    		moves.add((ty+2)*100+tx+1);
	    	}
	    	if (tx+1<8 && ty-2>=0 && pieces[ty-2][tx+1]==-1){
	    		chessBoardSquares[ty-2][tx+1].setBackground(Blue);
	    		moves.add((ty-2)*100+tx+1);
	    	}
	    	else if (tx+1<8 && ty-2>=0 && pieces[ty-2][tx+1]<=5){
	    		chessBoardSquares[ty-2][tx+1].setBackground(Attack);
	    		moves.add((ty-2)*100+tx+1);
	    	} 	
	    	if (tx+2<8 && ty+1<8 && pieces[ty+1][tx+2]==-1){
	    		chessBoardSquares[ty+1][tx+2].setBackground(Blue);
	    		moves.add((ty+1)*100+tx+2);
	    	}
	    	else if (tx+2<8 && ty+1<8 && pieces[ty+1][tx+2]<=5){
	    		chessBoardSquares[ty+1][tx+2].setBackground(Attack);
	    		moves.add((ty+1)*100+tx+2);
	    	}
	    	if (tx+2<8 && ty-1>=0 && pieces[ty-1][tx+2]==-1){
	    		chessBoardSquares[ty-1][tx+2].setBackground(Blue);
	    		moves.add((ty-1)*100+tx+2);
	    	}
	    	else if (tx+2<8 && ty-1>=0 && pieces[ty-1][tx+2]<=5){
	    		chessBoardSquares[ty-1][tx+2].setBackground(Attack);
	    		moves.add((ty-1)*100+tx+2);
	    	}
	    	if (tx-1>=0 && ty+2<8 && pieces[ty+2][tx-1]==-1){
	    		chessBoardSquares[ty+2][tx-1].setBackground(Blue);
	    		moves.add((ty+2)*100+tx-1);
	    	}
	    	else if (tx-1>=0 && ty+2<8 && pieces[ty+2][tx-1]<=5){
	    		chessBoardSquares[ty+2][tx-1].setBackground(Attack);
	    		moves.add((ty+2)*100+tx-1);
	    	}
	    	if (tx-2>=0 && ty-1>=0 && pieces[ty-1][tx-2]==-1){
	    		chessBoardSquares[ty-1][tx-2].setBackground(Blue);
	    		moves.add((ty-1)*100+tx-2);
	    	}
	    	else if (tx-2>=0 && ty-1>=0 && pieces[ty-1][tx-2]<=5){
	    		chessBoardSquares[ty-1][tx-2].setBackground(Attack);
	    		moves.add((ty-1)*100+tx-2);
	    	}
	    	if (tx-2>=0 && ty+1<8 && pieces[ty+1][tx-2]==-1){
	    		chessBoardSquares[ty+1][tx-2].setBackground(Blue);
	    		moves.add((ty+1)*100+tx-2);
	    	}
	    	else if (tx-2>=0 && ty+1<8 && pieces[ty+1][tx-2]<=5){
	    		chessBoardSquares[ty+1][tx-2].setBackground(Attack);
	    		moves.add((ty+1)*100+tx-2);
	    	}
	    	if (tx-1>=0 && ty-2>=0 && pieces[ty-2][tx-1]==-1){
	    		chessBoardSquares[ty-2][tx-1].setBackground(Blue);
	    		moves.add((ty-2)*100+tx-1);
	    	}
	    	else if (tx-1>=0 && ty-2>=0 && pieces[ty-2][tx-1]<=5){
	    		chessBoardSquares[ty-2][tx-1].setBackground(Attack);
	    		moves.add((ty-2)*100+tx-1);
	    	}
		}
		/**
		 * This function highlights the squares a black king can go to.
		 * @param ii x location
		 * @param jj y location
		 */
		public void highlight6(int ii, int jj){
			int tx=ii,ty=jj,i;
			if (!wr2 && !k2){
	    		for (i=ty-1;i>0;i--) if (pieces[i][ii]!=-1) break;
	    		if (i==0){
	    			chessBoardSquares[ty-2][tx].setBackground(Blue);
	        		moves.add((ty-2)*100+tx);
	    		}
	    	}
			tx=ii;ty=jj;
	    	if (!wr1 && !k2){
	    		for (i=ty+1;i<7;i++) if (pieces[i][ii]!=-1) break;
	    		if (i==7){
	    			chessBoardSquares[ty+2][tx].setBackground(Blue);
	        		moves.add((ty+2)*100+tx);
	    		}
	    	}
	    	tx=ii;ty=jj;
	    	if (ty+1<8 && tx+1<8 && pieces[ty+1][tx+1]==-1){
	    		chessBoardSquares[ty+1][tx+1].setBackground(Blue);
	    		moves.add((ty+1)*100+tx+1);
	    	}
	    	else if (ty+1<8 && tx+1<8 && pieces[ty+1][tx+1]<=5){
	    		chessBoardSquares[ty+1][tx+1].setBackground(Attack);
	    		moves.add((ty+1)*100+tx+1);
	    	}
	    	if (ty+1<8 && tx-1>=0 && pieces[ty+1][tx-1]==-1){
	    		chessBoardSquares[ty+1][tx-1].setBackground(Blue);
	    		moves.add((ty+1)*100+tx-1);
	    	}
	    	else if (ty+1<8 && tx-1>=0 && pieces[ty+1][tx-1]<=5){
	    		chessBoardSquares[ty+1][tx-1].setBackground(Attack);
	    		moves.add((ty+1)*100+tx-1);
	    	}
	    	if (tx+1<8 && ty-1>=0 && pieces[ty-1][tx+1]==-1){
	    		chessBoardSquares[ty-1][tx+1].setBackground(Blue);
	    		moves.add((ty-1)*100+tx+1);
	    	}
	    	else if (tx+1<8 && ty-1>=0 && pieces[ty-1][tx+1]<=5){
	    		chessBoardSquares[ty-1][tx+1].setBackground(Attack);
	    		moves.add((ty-1)*100+tx+1);
	    	}
	    	if (ty-1>=0 && tx-1>=0 && pieces[ty-1][tx-1]==-1){
	    		chessBoardSquares[ty-1][tx-1].setBackground(Blue);
	    		moves.add((ty-1)*100+tx-1);
	    	}
	    	else if (ty-1>=0 && tx-1>=0 && pieces[ty-1][tx-1]<=5){
	    		chessBoardSquares[ty-1][tx-1].setBackground(Attack);
	    		moves.add((ty-1)*100+tx-1);
	    	}
	    	if (tx+1<8 && pieces[ty][tx+1]==-1){
	    		chessBoardSquares[ty][tx+1].setBackground(Blue);
	    		moves.add(ty*100+tx+1);
	    	}
	    	else if (tx+1<8 && pieces[ty][tx+1]<=5){
	    		chessBoardSquares[ty][tx+1].setBackground(Attack);
	    		moves.add(ty*100+tx+1);
	    	}
	    	if (tx-1>=0 && pieces[ty][tx-1]==-1){
	    		chessBoardSquares[ty][tx-1].setBackground(Blue);
	    		moves.add(ty*100+tx-1);
	    	}
	    	else if (tx-1>=0 && pieces[ty][tx-1]<=5){
	    		chessBoardSquares[ty][tx-1].setBackground(Attack);
	    		moves.add(ty*100+tx-1);
	    	}
	    	if (ty+1<8 && pieces[ty+1][tx]==-1){
	    		chessBoardSquares[ty+1][tx].setBackground(Blue);
	    		moves.add((ty+1)*100+tx);
	    	}
	    	else if (ty+1<8 && pieces[ty+1][tx]<=5){
	    		chessBoardSquares[ty+1][tx].setBackground(Attack);
	    		moves.add((ty+1)*100+tx);
	    	}
	    	if (ty-1>=0 && pieces[ty-1][tx]==-1){
	    		chessBoardSquares[ty-1][tx].setBackground(Blue);
	    		moves.add((ty-1)*100+tx);
	    	}
	    	else if (ty-1>=0 && pieces[ty-1][tx]<=5){
	    		chessBoardSquares[ty-1][tx].setBackground(Attack);
	    		moves.add((ty-1)*100+tx);
	    	}
		}
		/**
		 * This function highlights the squares a white king can go to.
		 * @param ii x location
		 * @param jj y location
		 */
		public void highlight0(int ii, int jj){
			int tx=ii,ty=jj,i;
	    	if (ty+1<8 && tx+1<8 && pieces[ty+1][tx+1]==-1){
	    		chessBoardSquares[ty+1][tx+1].setBackground(Blue);
	    		moves.add((ty+1)*100+tx+1);
	    	}
	    	else if (ty+1<8 && tx+1<8 && pieces[ty+1][tx+1]>5){
	    		chessBoardSquares[ty+1][tx+1].setBackground(Attack);
	    		moves.add((ty+1)*100+tx+1);
	    	}
	    	if (!br2 && !k1){
	    		for (i=ty-1;i>0;i--) if (pieces[i][ii]!=-1) break;
	    		if (i==0){
	    			chessBoardSquares[ty-2][tx].setBackground(Blue);
	        		moves.add((ty-2)*100+tx);
	    		}
	    	}
	    	tx=ii;ty=jj;
	    	if (!br1 && !k1){
	    		for (i=ty+1;i<7;i++) if (pieces[i][ii]!=-1) break;
	    		if (i==7){
	    			chessBoardSquares[ty+2][tx].setBackground(Blue);
	        		moves.add((ty+2)*100+tx);
	    		}
	    	}
	    	tx=ii;ty=jj;
	    	if (ty+1<8 && tx-1>=0 && pieces[ty+1][tx-1]==-1){
	    		chessBoardSquares[ty+1][tx-1].setBackground(Blue);
	    		moves.add((ty+1)*100+tx-1);
	    	}
	    	else if (ty+1<8 && tx-1>=0 && pieces[ty+1][tx-1]>5){
	    		chessBoardSquares[ty+1][tx-1].setBackground(Attack);
	    		moves.add((ty+1)*100+tx-1);
	    	}
	    	if (tx+1<8 && ty-1>=0 && pieces[ty-1][tx+1]==-1){
	    		chessBoardSquares[ty-1][tx+1].setBackground(Blue);
	    		moves.add((ty-1)*100+tx+1);
	    	}
	    	else if (tx+1<8 && ty-1>=0 && pieces[ty-1][tx+1]>5){
	    		chessBoardSquares[ty-1][tx+1].setBackground(Attack);
	    		moves.add((ty-1)*100+tx+1);
	    	}
	    	if (ty-1>=0 && tx-1>=0 && pieces[ty-1][tx-1]==-1){
	    		chessBoardSquares[ty-1][tx-1].setBackground(Blue);
	    		moves.add((ty-1)*100+tx-1);
	    	}
	    	else if (ty-1>=0 && tx-1>=0 && pieces[ty-1][tx-1]>5){
	    		chessBoardSquares[ty-1][tx-1].setBackground(Attack);
	    		moves.add((ty-1)*100+tx-1);
	    	}
	    	if (tx+1<8 && pieces[ty][tx+1]==-1){
	    		chessBoardSquares[ty][tx+1].setBackground(Blue);
	    		moves.add(ty*100+tx+1);
	    	}
	    	else if (tx+1<8 && pieces[ty][tx+1]>5){
	    		chessBoardSquares[ty][tx+1].setBackground(Attack);
	    		moves.add(ty*100+tx+1);
	    	}
	    	if (tx-1>=0 && pieces[ty][tx-1]==-1){
	    		chessBoardSquares[ty][tx-1].setBackground(Blue);
	    		moves.add(ty*100+tx-1);
	    	}
	    	else if (tx-1>=0 && pieces[ty][tx-1]>5){
	    		chessBoardSquares[ty][tx-1].setBackground(Attack);
	    		moves.add(ty*100+tx-1);
	    	}
	    	if (ty+1<8 && pieces[ty+1][tx]==-1){
	    		chessBoardSquares[ty+1][tx].setBackground(Blue);
	    		moves.add((ty+1)*100+tx);
	    	}
	    	else if (ty+1<8 && pieces[ty+1][tx]>5){
	    		chessBoardSquares[ty+1][tx].setBackground(Attack);
	    		moves.add((ty+1)*100+tx);
	    	}
	    	if (ty-1>=0 && pieces[ty-1][tx]==-1){
	    		chessBoardSquares[ty-1][tx].setBackground(Blue);
	    		moves.add((ty-1)*100+tx);
	    	}
	    	else if (ty-1>=0 && pieces[ty-1][tx]>5){
	    		chessBoardSquares[ty-1][tx].setBackground(Attack);
	    		moves.add((ty-1)*100+tx);
	    	}
		}
		public final JComponent getGui() {
			return gui;
		}
		/**
		 * This function creates the chess pieces from a public domain
		 */
		private final void createImages() {
			try{
				URL url = new URL("http://i.stack.imgur.com/memI0.png");
				BufferedImage bi = ImageIO.read(url);
				for (int ii = 0; ii < 2; ii++)
					for (int jj = 0; jj < 6; jj++)
						chessPieceImages[ii][jj] = bi.getSubimage(jj * 64, ii * 64, 64, 64);
			}
			catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}	
		}
		/**
		 * This function sets up a new game whenever the New button is pressed.
		 * In addition, this completely resets all modifiers, such as king and rook movement.
		 */
		private final void setupNewGame() {
			turn=true;k1=false;k2=false;br1=false;br2=false;wr1=false;wr2=false;
			for (int i=0; i<moves.size();i++){
				int tx=moves.get(i)%100,ty=moves.get(i)/100;
				if (tx%2==0 && ty%2==0) chessBoardSquares[ty][tx].setBackground(Red);
				if (tx%2==1 && ty%2==0) chessBoardSquares[ty][tx].setBackground(Black);
				if (tx%2==0 && ty%2==1) chessBoardSquares[ty][tx].setBackground(Black);
				if (tx%2==1 && ty%2==1) chessBoardSquares[ty][tx].setBackground(Red);
			}
			for (int ii=0;ii<STARTINGROW.length;ii++)
				for (int jj=0;jj<STARTINGROW.length;jj++)
					pieces[ii][jj] = -1;
			for (int ii=0;ii<STARTINGROW.length;ii++){
				for (int jj=0;jj<STARTINGROW.length;jj++){
					ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
					chessBoardSquares[jj][ii].setIcon(icon);
				}
			}
			for (int ii = 0; ii < STARTINGROW.length; ii++) {
				chessBoardSquares[ii][0].setIcon(new ImageIcon(chessPieceImages[BLACK][STARTINGROW[ii]]));
				pieces[ii][0] = STARTINGROW[ii];
			}
			for (int ii = 0; ii < STARTINGROW.length; ii++) {
				chessBoardSquares[ii][1].setIcon(new ImageIcon(chessPieceImages[BLACK][PAWN]));
				pieces[ii][1] = PAWN;
			}
			for (int ii = 0; ii < STARTINGROW.length; ii++) {
				chessBoardSquares[ii][6].setIcon(new ImageIcon(chessPieceImages[WHITE][PAWN]));
				pieces[ii][6] = PAWN+6;
			}
			for (int ii = 0; ii < STARTINGROW.length; ii++) {
				chessBoardSquares[ii][7].setIcon(new ImageIcon(chessPieceImages[WHITE][STARTINGROW[ii]]));
				pieces[ii][7] = STARTINGROW[ii]+6;
			}
			for (int ii=2;ii<5;ii++) {
				for(int jj=0;jj<8;jj++){
					ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
					chessBoardSquares[jj][ii].setIcon(icon);
				}
			}
		}
		/**
		 * This function is the entry point of the project.
		 * @param augs command line arguments
		 */
		public static void main (String[] augs) {
			ChessGui cg = new ChessGui();
			JFrame f = new JFrame("Chess by Eric and Temi");
			f.add(cg.getGui());
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
     		f.setLocationByPlatform(true);
     		f.pack();
     		f.setMinimumSize(f.getSize());
     		f.setVisible(true);
		}
	}
}
