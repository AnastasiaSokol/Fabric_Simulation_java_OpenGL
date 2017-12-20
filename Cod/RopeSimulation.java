/*
����� ��������� �������
1.���������� ��������� � ������������� ����������� y � 9.81�/�^2
2.���� ����� ������� �������� � 4 ������ ��� ������������
3.������� ����� ������������� �� ������� ���������
4.���������� ����� ��������� 5�� - ��� d, ���������� ��� ������� ������� � ��������� �����
5.������ 80 ��
6.�������  ��� ���������� � ������ �������������
7.����� ������� 4��
8.�� ���� ������� 4��/80��=0.05��=50��
-------------------------------------------
������ ��������� ������� k
1.�� ������ ������� �� ������� �����, ��� ���������. 
2.������� ������� ������� ������ ����� ���������.
3.������ ����� ������� ���������� ������, ��� �� 1 �� (0.01 �). 
4.���, ������� ��� ������� ����� � ��� ����� ��� ������� (������� �� ������� ����� �������������). 

F������� =(����� �������)*(�������������� ���������)= 4*9.8=40�

5.���� ��������� ������� ������ �������������� 40�

F��������� = -k*x=-k*0.01�

6.����� ���� ��� ������ ���� ������ ����

40�+(-k*0.01�)=0
k=4000[�/�]
-------------------------------------------
������ ��������� ������ � ������� f
������� ����������������� ����� f=0.2 H/(�/c)
 */
package org.yourorghere;

import javax.media.opengl.GL;

/*� ������ RopeSimulation, �� ������������� ����� solve() � simulate(float dt), 
��������� �� ����� ����������� ���������� ���� ������� ��� �������. 
�� �������� ����� solve(), 
� �������� ������� ����� ������� � ������ simulate(float dt).*/
//------------------------------------------------------------------------------
public class RopeSimulation extends Simulation {
//------------------------------------------------------------------------------

    Spring[] springs = new Spring[numOfMasses - 1];//������ ������ 
    Vector3D gravitation = new Vector3D();    // ��������� ���������� �������  

    //������� � �������� ��������� ����� masses[0]
    //����� � ������������, ������� ������������ ��� ������� ������� ������ ����� � ������������ (� �������� 0)
    Vector3D ropeConnectionPos = new Vector3D(0, 2, 0);
    // �������� ����������� ropeConnectionPos � ������� ��� �� ����� ����������� �������
    Vector3D ropeConnectionVel;//=new Vector3D(0,0,0);
    double springConstant = 0;
    double springLength = 0;
    double springFrictionConstant = 0;

    double groundRepulsionConstant = 0;//����������� ������������ ���� �� ����� 
    double groundFrictionConstant = 0;//����������� ������ ���� � ������
    double groundAbsorptionConstant = 0;//����������� ���������� ������ �� ����� (��� ������������ ������������� ������� � ������)
    double groundHeight = 0;//Y ���������� �����
    double airFrictionConstant = 0;//���������� ������ � ������
//------------------------------------------------------------------------------
    //�����������

    public RopeSimulation(
            int numOfMasses,
            double m,
            double springConstant,//����������� ��������� �������
            double springLength,//����������, ��� ������� ������� ����� ���������
            double springFrictionConstant,//����������� ������ �������
            Vector3D gravitation,//��������� ���������� �������  
            double airFrictionConstant,//���������� ������ � ������
            double groundRepulsionConstant,//����������� ������������ ���� �� ����� 
            double groundFrictionConstant,//����������� ������ ���� � ������
            double groundAbsorptionConstant,//����������� ���������� ������ �� ����� (��� ������������ ������������� ������� � ������)
            double groundHeight//Y ���������� �����  
    ) {
        super(numOfMasses, m);

        //---------------------------
        this.gravitation = gravitation;
        this.airFrictionConstant = airFrictionConstant;

        this.groundAbsorptionConstant = groundAbsorptionConstant;
        this.groundFrictionConstant = groundFrictionConstant;
        this.groundHeight = groundHeight;
        this.groundRepulsionConstant = groundRepulsionConstant;

        this.springConstant = springConstant;
        this.springLength = springLength;
        this.springFrictionConstant = springFrictionConstant;
        //������� ����� ������������� ����������� �����
        //      0-0-0-0-0-0-0-0
        //---------------------------
        //������������� ��������� ������� ����
        for (int i = 0; i < numOfMasses; i++) {

            masses[i].pos.x = i * springLength;//X-������� masses[a] � �����������
            masses[i].pos.y = 0;
            masses[i].pos.z = 0;

        }
        //---------------------------
        //������� ������� ����������� �����
        springs = new Spring[numOfMasses - 1];
        for (int i = 0; i < numOfMasses - 1; i++) {
            springs[i] = new Spring(masses[i], masses[i + 1], this.springConstant, this.springLength, this.springFrictionConstant);
        }

    }
//------------------------------------------------------------------------------
    //���������� ���

    @Override
    void solve() {
        //��������� ���� ��������� � ������ ��� ������
        for (int i = 0; i < numOfMasses - 1; i++) {
            springs[i].solve();//� ������� ����������� ���� ��������� � ������
        }
        //��������� ���� � ����� ������
        for (int i = 0; i < numOfMasses; i++) {
            //��������� ���� ���������� F=g*m
            masses[i].applyForce(gravitation.Mult(masses[i].m));

            //��������� ���� ������ � ������ F������=-k*��������
            masses[i].applyForce(masses[i].speed.Mult(airFrictionConstant).invert());
            //���� ����� ��������� ����� �� 
            //�������� ���� ������������ �� �����
            if (masses[i].pos.y <= groundHeight) {
                //������� ������ �������� �����, ����������� ��������� �������� 
                //� Y �����������, ��� ��� �� ����� ��������� ���� ������-���������� ����������� �����
                //���������� � Y ����������� ����� ����� ��� ������� ����������
                Vector3D v = masses[i].speed;//��������� ������
                v.y = 0;
                //��������� ���� ������ � ����� F������=-k*��������
                masses[i].applyForce(v.Mult(groundFrictionConstant).invert());
                //� ������ ������ ���������� � ����������� Y
                v = masses[i].speed;
                v.x = 0;
                v.z = 0;
                //��������� ���� ����������
                if (v.y < 0) {
                    masses[i].applyForce(v.Mult(groundAbsorptionConstant).invert());
                }
                //------------------------------
                //��������� ���� ������������
                //����� ����� ����������� ����� ������� �������
                Vector3D force = new Vector3D(0, groundHeight - masses[i].pos.y, 0).Mult(groundRepulsionConstant);
                masses[i].applyForce(force);

            }
        }
    }
//------------------------------------------------------------------------------

    @Override
    int GetTypeOfSimulation() {
        return 4;
    }
//------------------------------------------------------------------------------
    //�������������� ����� �� ������ Simulation

    @Override
    void simulate(double dt) {
        //��������� ������� 
        //pos+=speed*dt

        ropeConnectionPos = ropeConnectionPos.Add(ropeConnectionVel.Mult(dt));
        //������� �� ����� ��������� ��� ������
        if (ropeConnectionPos.y < groundHeight) {
            ropeConnectionPos.y = groundHeight;
            ropeConnectionVel.y = 0;
        }
        //����� ������� �����
        masses[0].pos = ropeConnectionPos;
        //��������� �������� ������� �����
        masses[0].speed = ropeConnectionVel;
        //��� ����� ����� � ������ ��������� ������ �������� ����������������
        ropeConnectionVel = ropeConnectionVel.Mult(0.95);

        //� ������ simulate ��� ����������� ��������� ������� ���� �������
        for (int i = 0; i < numOfMasses; i++) {
            masses[i].simulate(dt);
        }

    }
//------------------------------------------------------------------------------    

    /*������� ������������ ��� �������������. 
    ��������� ������� �� ������ ropeConnectionVel, � 
    �� ����� ���������� ������� ���, ��� ���� �� �� 
    ������� �� �� ���� �����.*/
    void setRopeConnectionVel(Vector3D ropeConnectionVel) {
        this.ropeConnectionVel = ropeConnectionVel;
    }
//------------------------------------------------------------------------------    

    void Draw(GL gl) {

        //������ �����
        gl.glBegin(gl.GL_QUADS);
        gl.glColor3d(0.0, 0.0, 1.0);												// Set Color To Light Blue
        gl.glVertex3d(20, this.groundHeight, 20.0);
        gl.glVertex3d(-20, this.groundHeight, 20.0);
        gl.glColor3d(0, 0, 0);												// Set Color To Black
        gl.glVertex3d(-20, this.groundHeight, -20.0);
        gl.glColor3d(1, 0, 0);
        gl.glVertex3d(20, this.groundHeight, -20.0);
        gl.glEnd();
        //������ ���� �� ������� � ���� �������
        for (int i = 0; i < numOfMasses - 1; i++) {
            gl.glColor3d(0, 0, 0);													// Set Color To Black
            Mass mass1 = this.getMass(i);
            Vector3D pos1 = mass1.pos;
            Mass mass2 = this.getMass(i + 1);
            Vector3D pos2 = mass2.pos;

            gl.glLineWidth(2);
            gl.glBegin(gl.GL_LINE_LOOP);
            gl.glColor3d(0.0, 0.0, 0.0);
            gl.glVertex3d(pos1.x, this.groundHeight, pos1.z);
            gl.glVertex3d(pos2.x, this.groundHeight, pos2.z);
            gl.glEnd();
            //������ �������
            gl.glLineWidth(3);
            gl.glBegin(gl.GL_LINE_LOOP);
            gl.glColor3d(0.0, 1.0, 0.0);
            gl.glVertex3d(pos1.x, pos1.y, pos1.z);
            gl.glVertex3d(pos2.x, pos2.y, pos2.z);
            gl.glEnd();
        }

    }
}
