package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.Encoder;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.internal.libvlc_logo_position_e;
import uk.co.caprica.vlcj.binding.internal.libvlc_marquee_position_e;
import uk.co.caprica.vlcj.component.AudioMediaPlayerComponent;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class DisplayFrame {
	private  JFrame frame;
	private  JButton pauseButton,rewindButton,skipButton , addMedia,playModel;
	public  EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private JSlider progressBar,volumeBar;
	public long mediaLength,currentTime;
	public JLabel label;
	private boolean flag=true;
	private Media currentMedia=null;
	private JComboBox<Media> mediaList;
    public DisplayFrame(String[] args) {
        frame = new JFrame("My First Media Player");
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mediaPlayerComponent.release();
                System.exit(0);
            }
            
        });
        JPanel contentPane=setContentPane();
        JPanel controlsPane=setControlsPane();
        JPanel menuPane=setMenuPane();
        contentPane.add(controlsPane,BorderLayout.SOUTH);
        add_Listener();
        frame.setContentPane(contentPane);
        frame.add(menuPane,BorderLayout.NORTH );
        frame.setVisible(true);
		mediaPlayerComponent.getMediaPlayer().setVolume(volumeBar.getValue());
        MyThread t=new MyThread();
        t.start();
    }
    private JPanel setMenuPane()
    {
    	JPanel menuPane=new JPanel();
    	playModel=new JButton("顺序");
    	menuPane.add(playModel);
    	addMedia=new JButton("add");
    	menuPane.add(addMedia);
    	mediaList=new JComboBox<Media>();
    	menuPane.add(mediaList);
    	return menuPane;
    }
    private JPanel setContentPane()
    {
    	JPanel contentPane=new JPanel();
        contentPane.setLayout(new BorderLayout());
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        contentPane.add(mediaPlayerComponent,BorderLayout.CENTER);
        return contentPane;
    }
    private JPanel setControlsPane()
    {
    	 JPanel controlsPane=new JPanel();
         controlsPane.setLayout(new BorderLayout());
         JPanel centerPane=new JPanel();
         rewindButton=new JButton("rewing");
         pauseButton =new JButton("pause");
         skipButton=new JButton("skip");
         volumeBar=new JSlider(0,100,20);
         centerPane.add(rewindButton);
         centerPane.add(pauseButton);
         centerPane.add(skipButton);
        // centerPane.add(volumeBar);
         controlsPane.add(centerPane);
         progressBar=new JSlider(0,100,0);
         setJSliderUI(progressBar);
         setJSliderUI(volumeBar);
         controlsPane.add(progressBar,BorderLayout.NORTH);
         controlsPane.add(volumeBar,BorderLayout.EAST);
         label=new JLabel();
         controlsPane.add(label,BorderLayout.WEST);
         return controlsPane;
    }
    private void add_Listener()
    {
    	pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				mediaPlayerComponent.getMediaPlayer().pause();
			}
        });
        rewindButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				mediaPlayerComponent.getMediaPlayer().skip(-10000);
			}
        	
        });
        skipButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				mediaPlayerComponent.getMediaPlayer().skip(10000);

			}
        	
        });
        addMedia.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				FileDialog fd=new FileDialog(frame,"选择要添加进播放列表的视频或音频",FileDialog.LOAD);
				fd.setFile(";*.mp3;*.mp4;*.mkv;*.wav;");
				fd.setVisible(true);
				String path=fd.getDirectory()+fd.getFile();
				String name=fd.getFile();
				if(name==null)
					return;
				Media media=new Media(path,name);
				mediaList.addItem(media);
				mediaList.setSelectedItem(media);
			}
        	
        });
        mediaList.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if(e.getStateChange()==ItemEvent.DESELECTED)
					return;
				if(currentMedia!=null)
				{
					long currentTime=mediaPlayerComponent.getMediaPlayer().getTime();
					currentMedia.setCurrentTime(currentTime);
				}
				currentMedia=(Media) e.getItem();
				String path=currentMedia.getPath();
				//将多媒体文件预先设置好地址，还未播放。
				mediaPlayerComponent.getMediaPlayer().prepareMedia(path);
				//播放多媒体文件
				mediaPlayerComponent.getMediaPlayer().play();
				//mediaPlayerComponent.getMediaPlayer().setTime(currentMedia.getCurrentTime());
			}
        	
        });
        playModel.addActionListener(new ActionListener()
        		{

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						String str=e.getActionCommand();
						switch(e.getActionCommand())
						{
						case "顺序":
							playModel.setText("随机");
							break;
						case "随机":
							playModel.setText("循环");							
							break;
						case "循环":
							playModel.setText("顺序");
							break;
						
						}
					}
		        	
        		});
        progressBar.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if(flag)
						{
							mediaLength=mediaPlayerComponent.getMediaPlayer().getLength();
							mediaPlayerComponent.getMediaPlayer().setTime(mediaLength*progressBar.getValue()/100);
							reSetLabelTime();
						}

						}
					
				});
			}
        	
        });
        volumeBar.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				mediaPlayerComponent.getMediaPlayer().setVolume(volumeBar.getValue());
			}
        	
        });
        mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(
        		new MediaPlayerEventAdapter() {
        		    @Override
        		    public void playing(MediaPlayer mediaPlayer) {
        		    	SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
							//	reSetLabelTime ();
							}
        		    		
        		    	});
        		    }

        		    @Override
        		    public void finished(MediaPlayer mediaPlayer) {
        		    	SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								switch(playModel.getText())
								{
								case "顺序":
									int index=mediaList.getSelectedIndex();
									int max=mediaList.getItemCount();
									if(index+1<max)
										mediaList.setSelectedIndex(index+1);
									else
									{
										if(index!=0)
											mediaList.setSelectedIndex(0);
										else
											mediaPlayerComponent.getMediaPlayer().play();
									}
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									break;
								case "随机":
									int currentIndex=mediaList.getSelectedIndex();
									int max2=mediaList.getItemCount();
									int index2=0;
									if(max2!=1)
										index2=(int)(Math.random()*100)%(max2-1);
									if(currentIndex!=index2)
										mediaList.setSelectedIndex(index2);
									else
										mediaPlayerComponent.getMediaPlayer().play();
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									break;
								case "循环":
									mediaPlayerComponent.getMediaPlayer().playMedia(currentMedia.getPath());
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								     break;
								}
							}
        		    		
        		    	});
        		    }

        		    @Override
        		    public void error(MediaPlayer mediaPlayer) {
        		    	SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								JOptionPane.showMessageDialog(
										frame, 
										"Failed to play media", 
										"Error",
						                JOptionPane.ERROR_MESSAGE);
								frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
							}
        		    		
        		    	});
        		    }
        	    }
        	);
        			
    }
    class MyThread extends Thread {
    	/*DisplayFrame d=null;
    	public MyThread(DisplayFrame d)
    	{
    		this.d=d;
    	}*/
    	public MyThread() {};
    	   public void run(){
    		   while(true)
    		   {
    			   flag=false;
    			   reSetLabelTime();
                 	if(mediaLength!=0)
                 	{
                 		progressBar.setValue((int)(currentTime*100/mediaLength));
                 	}
   	               try {
   	            	   		Thread.sleep(100);
	                  		flag=true;
							Thread.sleep(100);
   	               		} catch (InterruptedException e) {
   	               			e.printStackTrace();
						}
    		   }
           	
    	   }
    	}
    private void reSetLabelTime()
    {
		   mediaLength=mediaPlayerComponent.getMediaPlayer().getLength();
		   currentTime=mediaPlayerComponent.getMediaPlayer().getTime();
          	//slider.setValue((int)(currentTime*100/mediaLength));
          	long ch,cm,cs, ah,am,as;
              	ch=currentTime/(1000*60*60);
              	cm=(currentTime)/(1000*60)%60;
              	cs=(currentTime)/1000%60;
              	ah=mediaLength/(1000*60*60);
              	am=(mediaLength)/(1000*60)%60;
              	as=(mediaLength)/1000%60;
              	label.setText(ch+":"+cm+":"+cs+"/"
              					+ah+":"+am+":"+as);
    }
    private void setJSliderUI(JSlider slider)
    {
    	slider.setUI(new  javax.swing.plaf.metal.MetalSliderUI(){
            @Override
            public void paintThumb(Graphics g) {
                //绘制指示物
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.BLUE);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.fillOval(thumbRect.x, thumbRect.y, thumbRect.width,
                        thumbRect.height);//修改为圆形
                //也可以帖图(利用鼠标事件转换image即可体现不同状态)
                //g2d.drawImage(image, thumbRect.x, thumbRect.y, thumbRect.width,thumbRect.height,null);

            }
            public void paintTrack(Graphics g) {
                //绘制刻度的轨迹
                int cy,cw;
                Rectangle trackBounds = trackRect;
                if (slider.getOrientation() == JSlider.HORIZONTAL) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setPaint(Color.black);//将背景设为黑色
                    cy = (trackBounds.height/2) - 2;
                    cw = trackBounds.width;

                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.translate(trackBounds.x, trackBounds.y + cy);
                    g2.fillRect(0, -cy + 5, cw, cy);

                    int trackLeft = 0;
                    int trackRight = 0;
                    trackRight = trackRect.width - 1;

                    int middleOfThumb = 0;
                    int fillLeft = 0;
                    int fillRight = 0;
                    //换算坐标
                    middleOfThumb = thumbRect.x + (thumbRect.width / 2);
                    middleOfThumb -= trackRect.x;

                    if (!drawInverted()) {
                        fillLeft = !slider.isEnabled() ? trackLeft : trackLeft + 1;
                        fillRight = middleOfThumb;
                        } else {
                        fillLeft = middleOfThumb;
                        fillRight = !slider.isEnabled() ? trackRight - 1
                        : trackRight - 2;
                        }
                    //设定渐变,在这里从红色变为红色,则没有渐变,滑块划过的地方自动变成红色
                    g2.setPaint(new GradientPaint(0, 0, Color.red, cw, 0,
                            Color.red, true));
                    g2.fillRect(0, -cy + 5, fillRight - fillLeft, cy);

                    g2.setPaint(slider.getBackground());
                    g2.fillRect(10, 10, cw, 5);

                    g2.setPaint(Color.WHITE);
                    g2.drawLine(0, cy, cw - 1, cy);

                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
                    g2.translate(-trackBounds.x, -(trackBounds.y + cy));                    
                }
                else {
                    super.paintTrack(g);
                    }
            }

});
    }
/*	public static void main(String[] args)
	{
		new NativeDiscovery().discover();
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	DisplayFrame d = new DisplayFrame(args);
            }
        });
		
	}*/
}
