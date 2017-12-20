package org.yourorghere;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.GLUT;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class DimamicOfSystem implements GLEventListener, KeyListener {
static double SlowMotionRatio=10.0;//����������� ���������� ��������
static double dt=0;//������� ����� ���������
//������� ���������-------------------------------------------------------------
//������� ��������� �������� � ���������� ���������
//��������� int numOfMasses, double m, Vector3D Velocity
//static ConstantVelocity constantVelocity = new ConstantVelocity(1,1.0,new Vector3D(1.0,1.0,0.0));
//������� ��������� �������� ��� ����� ����������
//static MotionUnderGravitation MotionUnderGravitation = new MotionUnderGravitation(1,1.0,new Vector3D(0.0,-9.8,0.0));
//������� ��������� �������� ����� �� �������
//static MassConnectedWithSpring massConnectedWithSpring = new MassConnectedWithSpring(1,1.0,2.0);
//������� ��������� �������
static Vector3D ConnectionVel=new Vector3D();//������ �������� ������� ���� ������� ��� ������� ���� ���� ��� �����
//��������� �������
static RopeSimulation ropeSimulation = new RopeSimulation(
	20,// ���������� ���� ������� �� 80 �����
	0.005,// ����� ������� ���� 50�������
	200000,//springConstant ��������� �������
	0.05,//springLength ���������� ����� ������, ��� ������� ������� ���������
	2000,//springFrictionConstant ������ �������
	new Vector3D(0, -9.81f, 0),// ��������� ���������� �������
	0.02,//airFrictionConstant ������ ���� � ������
	100,//groundRepulsionConstant ���������� ������������ ���� �� �����
	0.003,//groundFrictionConstant ���������� ������ ���� � ������
	1000,//groundAbsorptionConstant ���������� ���������� ���� ������ ���� ��� ������������ � ������
	-1.5);//GroundHeight Y ���������� ����� 
//��������� �����
static FabricSimulation fabricSimulation = new FabricSimulation(
	0.015,					// ����� ������� ���� 50�������
        10,                                      //�������
        5,                                      //������
	750,				// ��������� �������
	0.25,   			// ���������� ����� ������, ��� ������� ������� ���������
	1,			// ������ �������
	new Vector3D(0, -9.81f, 0),             // ��������� ���������� �������
	0.02,			// ������ ���� � ������
	100.0,		// ���������� ������������ ���� �� �����
	0,			// ���������� ������ ���� � ������
	50.0,		// ���������� ���������� ���� ������ ���� ��� ������������ � ������
	-1.5);				// Y ���������� ����� 
//------------------------------------------------------------------------------
static Frame frame = new Frame("Fabric/Rope simulation");
static GLCanvas canvas = new GLCanvas();
//------------------------------------------------------------------------------
static int messageMode=0;//����� ������ ���������
static int simulationMode=0;//����� ������ ��������� (�������/�����)
    
    public static void main(String[] args) {
        canvas.addGLEventListener(new DimamicOfSystem());
        canvas.addKeyListener(new DimamicOfSystem());
        canvas.setFocusable(true);
        frame.add(canvas);
        frame.setSize(640, 480);
        final Animator animator = new Animator(canvas);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                // Run this on another thread than the AWT event queue to
                // make sure the call to Animator.stop() completes before
                // exiting
                new Thread(new Runnable() {

                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });
        // Center frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        animator.start();
    }

    public void init(GLAutoDrawable drawable) {
        // Use debug pipeline
        // drawable.setGL(new DebugGL(drawable.getGL()));

        GL gl = drawable.getGL();
        System.err.println("INIT GL IS: " + gl.getClass().getName());

        // Enable VSync
        gl.setSwapInterval(1);

        // Setup the drawing area and shading mode
        PROPERTIESDefault(gl);
       
        ConnectionVel=new Vector3D();
        ropeSimulation.setRopeConnectionVel(ConnectionVel);
        fabricSimulation.setFabricConnectionVel(ConnectionVel);

        
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        GLU glu = new GLU();

        if (height <= 0) { // avoid a divide by zero error!
        
            height = 1;
        }
        final float h = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, h, 1.0, 20.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        GLU glu = new GLU();
        GLUT glut = new GLUT();
        
        dt+=0.1;
        dt=dt/SlowMotionRatio;
        //������������ ���������
        //����� ��������� �������� � ��������� �����, � ��������� ������ ������� ������ �� ����������� ��� � ���������� �������.     
        ropeSimulation.operate(dt);
        fabricSimulation.operate(dt);
        
        // Clear the drawing area
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        // Reset the current matrix to the "identity"
        gl.glLoadIdentity();

        // Move the "drawing cursor" around
        gl.glTranslatef(0f, 0.0f, -7.0f);
        
        //������ ���������
        if (simulationMode==0){ropeSimulation.Draw(gl);}
        else {fabricSimulation.Draw(gl);}
        
        
        
       
        //����� ��������� �� �����
        messages(gl,glut);
        // Flush all drawing operations to the graphics card
        gl.glFlush();
    }
   
    
    public void messages(
            GL gl,
            GLUT glut
    ) {
        //����� ��������� �� �����----------------------------------------------
            gl.glColor3f(1, 1, 1);
            Mass mymass;
            switch (messageMode){
                case 0:
                    gl.glWindowPos2i(20, 15*1); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Please Enter F1 to open the MENU");
                    break;
                case 1:
                    gl.glWindowPos2i(20, 15*1); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "[Enter]_______change simulation(rope/fabric)");    
                    gl.glWindowPos2i(20, 15*2); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "[home/end]___change ConnectionPos (Y)");
                    gl.glWindowPos2i(20, 15*3); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "[up/down]____change ConnectionPos (Y)");
                    gl.glWindowPos2i(20, 15*4); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "[left/right]_____change ConnectionPos (X)");
                    gl.glWindowPos2i(20, 15*5); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "[F2]_________show info about simulation");
                    gl.glWindowPos2i(20, 15*6); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "[F1]_________close menu");
                    gl.glWindowPos2i(20, 15*7); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "__________________MENU__________________");
                    break;
                case 2:
                    switch (simulationMode){
                        case 0:
                            gl.glWindowPos2i(20, 15*1); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "numOfMasses"+ ropeSimulation.numOfMasses);
                            gl.glWindowPos2i(20, 15*2); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "m="+ropeSimulation.masses[0].m);
                            gl.glWindowPos2i(20, 15*3); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "springConstant="+ropeSimulation.springConstant);
                            gl.glWindowPos2i(20, 15*4); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "springLength="+ropeSimulation.springLength);
                            gl.glWindowPos2i(20, 15*5); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "springFrictionConstant="+ropeSimulation.springFrictionConstant);
                            gl.glWindowPos2i(20, 15*6); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "groundAbsorptionConstant="+ropeSimulation.groundAbsorptionConstant);
                            gl.glWindowPos2i(20, 15*7); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "groundFrictionConstant="+ropeSimulation.groundFrictionConstant);
                            gl.glWindowPos2i(20, 15*8); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "groundRepulsionConstant"+ropeSimulation.groundRepulsionConstant);
                            gl.glWindowPos2i(20, 15*9); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "ropeConnectionPos={"+ ropeSimulation.ropeConnectionPos.x+","+ropeSimulation.ropeConnectionPos.y+","+ropeSimulation.ropeConnectionPos.z+"}");
                            gl.glWindowPos2i(20, 15*10); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "------ROPE SIMULATION---------------------------");
                        break;
                        
                        case 1:
                            gl.glWindowPos2i(20, 15*1); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "numOfMasses"+ fabricSimulation.numOfMasses);
                            gl.glWindowPos2i(20, 15*2); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "m="+fabricSimulation.masses[0].m);
                            gl.glWindowPos2i(20, 15*3); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "springConstant="+fabricSimulation.springConstant);
                            gl.glWindowPos2i(20, 15*4); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "springLength="+fabricSimulation.springLength);
                            gl.glWindowPos2i(20, 15*5); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "springFrictionConstant="+fabricSimulation.springFrictionConstant);
                            gl.glWindowPos2i(20, 15*6); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "groundAbsorptionConstant="+fabricSimulation.groundAbsorptionConstant);
                            gl.glWindowPos2i(20, 15*7); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "groundFrictionConstant="+fabricSimulation.groundFrictionConstant);
                            gl.glWindowPos2i(20, 15*8); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "groundRepulsionConstant"+fabricSimulation.groundRepulsionConstant);
                            gl.glWindowPos2i(20, 15*9); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "col="+fabricSimulation.col);
                            gl.glWindowPos2i(20, 15*10); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "row"+fabricSimulation.row);
                            gl.glWindowPos2i(20, 15*11); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "ropeConnectionPos={"+ fabricSimulation.fabricConnectionPos.x+","+fabricSimulation.fabricConnectionPos.y+","+fabricSimulation.fabricConnectionPos.z+"}");
                            gl.glWindowPos2i(20, 15*12); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "------FABRIC SIMULATION---------------------------");
                        break;
                    }
                    
                    
        
            }
            
    }        
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    public void keyTyped(KeyEvent ke) {
    }

    public void keyPressed(KeyEvent ke) {
        switch (ke.getKeyCode()) {
            //����� ������ ���������
            case KeyEvent.VK_F1:
                    if (messageMode<1){messageMode++;}
                    else{messageMode=0;}
                break;
            case KeyEvent.VK_F2:
                   messageMode=2;
                break;
            //����� ������ ���������
                case KeyEvent.VK_ENTER:
                    if (simulationMode<1){simulationMode++;}
                    else{simulationMode=0;}
                break;
               
            //------------------------------------------------------------------
            //���������� ���������� �������
            case KeyEvent.VK_RIGHT:
                ropeSimulation.ropeConnectionVel.x+=0.8; 
                fabricSimulation.fabricConnectionVel.x+=0.8; 
                break;
            case KeyEvent.VK_LEFT:
                ropeSimulation.ropeConnectionVel.x-=0.8; 
                fabricSimulation.fabricConnectionVel.x-=0.8; 
                break;
            case KeyEvent.VK_UP:
                ropeSimulation.ropeConnectionVel.z+=0.8;
                fabricSimulation.fabricConnectionVel.z+=0.8; 
                break;
            case KeyEvent.VK_DOWN:
                ropeSimulation.ropeConnectionVel.z-=0.8;
                fabricSimulation.fabricConnectionVel.z-=0.8; 
                break;
            case KeyEvent.VK_HOME:
                ropeSimulation.ropeConnectionVel.y+=0.8;
                fabricSimulation.fabricConnectionVel.y+=0.8; 
                break;
            case KeyEvent.VK_END:
                ropeSimulation.ropeConnectionVel.y-=0.8;
                fabricSimulation.fabricConnectionVel.y-=0.8; 
                break;
            //------------------------------------------------------------------
     
        }
    }

    public void keyReleased(KeyEvent ke) {
    }
    
    public void PROPERTIESDefault(GL gl) {
        gl.glClearColor(0.7f, 0.3f, 0.4f, 0.0f);//���� �������� ������
        gl.glShadeModel(GL.GL_SMOOTH); //��������� ������������ ������
        //��������� �������---------------------------------------------------------------
        gl.glEnable(GL.GL_DEPTH_TEST);//GL_DEPTH_BUFFER_BIT ��� Z-�����,�������� ��������� ����� 
        gl.glEnable(GL.GL_CULL_FACE);//������� �����  face culling, ����� �� �������� ��������, ������� � ��������� ����� ��������� � ������ �� ��� ��������   
    }
   
}

