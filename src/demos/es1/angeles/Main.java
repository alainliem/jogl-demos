package demos.es1.angeles;

import java.nio.*;
import javax.media.opengl.*;
import com.sun.javafx.newt.*;

public class Main implements MouseListener {

    public boolean quit = false;
    public GLWindow window = null;

    public void mouseClicked(MouseEvent e) {
        //System.out.println(e);
        switch(e.getClickCount()) {
            case 1:
                if(null!=window) {
                    window.setFullscreen(!window.isFullscreen());
                }
                break;
            default: 
                quit=true;
                break;
        }
    }
    public void mouseEntered(MouseEvent e) {
    }
    public void mouseExited(MouseEvent e) {
    }
    public void mousePressed(MouseEvent e) {
    }
    public void mouseReleased(MouseEvent e) {
    }
    public void mouseMoved(MouseEvent e) {
    }
    public void mouseDragged(MouseEvent e) {
    }

    private void run(int type) {
        int width = 800;
        int height = 480;
        System.out.println("angeles.Main.run()");
        GLProfile.setProfileGL2ES1();
        try {
            Window nWindow = null;
            if(0!=(type&USE_AWT)) {
                Display nDisplay = NewtFactory.createDisplay(NewtFactory.AWT, null); // local display
                Screen nScreen  = NewtFactory.createScreen(NewtFactory.AWT, nDisplay, 0); // screen 0
                nWindow = NewtFactory.createWindow(NewtFactory.AWT, nScreen, 0); // dummy VisualID
            }

            // Hook this into EGL
            GLCapabilities caps = new GLCapabilities();
            // For emulation library, use 16 bpp
            caps.setRedBits(5);
            caps.setGreenBits(6);
            caps.setBlueBits(5);
            caps.setDepthBits(16);
            window = GLWindow.create(nWindow, caps);

            window.addMouseListener(this);

            window.setSize(width, height);
            window.setFullscreen(true);
            window.setVisible(true);

            GL gl = window.getGL();
            if(gl.isGLES1() && 0==(type&USE_ANGELESF)) {
                System.out.println("Using: AngelesES1 .. ");
                AngelesES1 angel = new AngelesES1();
                window.addGLEventListener(angel);
            } else if(gl.isGL2ES1()) {
                System.out.println("Using: AngelesGL2ES1 .. ");
                AngelesGL2ES1 angel = new AngelesGL2ES1();
                window.addGLEventListener(angel);
            } else {
                System.out.println("Using: nop .. ");
            }

            long startTime = System.currentTimeMillis();
            long lastTime = startTime, curTime = 0, dt0, dt1;
            int totalFrames = 0, lastFrames = 0;

            do {
                window.display();

                totalFrames++; lastFrames++;
                curTime = System.currentTimeMillis();
                dt0 = curTime-lastTime;
                if ( (curTime-lastTime) > 5000 ) {
                    dt1 = curTime-startTime;
                    System.out.println(dt1/1000+"s, 5s: "+ (lastFrames*1000)/dt0 + " fps, "+
                                       "total: "+ (totalFrames*1000)/dt1 + " fps");
                    lastTime=curTime;
                    lastFrames=0;
                }

            } while (!quit && (curTime - startTime) < 215000);

            // Shut things down cooperatively
            window.close();
            window.getFactory().shutdown();
            System.out.println("angeles.Main shut down cleanly.");
        } catch (GLException e) {
            e.printStackTrace();
        }
    }

    public static int USE_NEWT      = 0;
    public static int USE_AWT       = 1 << 0;
    public static int USE_ANGELESF  = 1 << 1;

    public static void main(String[] args) {
        int type = USE_NEWT ;
        for(int i=args.length-1; i>=0; i--) {
            if(args[i].equals("-awt")) {
                type |= USE_AWT; 
            }
            if(args[i].equals("-angelesf")) {
                type |= USE_ANGELESF; 
            }
        }
        new Main().run(type);
        System.exit(0);
    }

}