/*
����� ��������� �����
 */
package org.yourorghere;

import javax.media.opengl.GL;

/*
    m[0]----m[1]------m[2]------m[3]
    |       |         |         |
    m[4]----m[5]------m[6]------m[7]
    |       |         |         |
    m[8]----m[9]------m[10]-----m[11]

 */
public class FabricSimulation extends Simulation {

    int col = 0;//���������� ���� �� ����������� (�������� �������)
    int row = 0;//���������� ���� �� ��������� (����� �������)
    int numOfSprings = 0;//�������� ����������� � ���������������� � ������������
    Spring[] springs = new Spring[numOfSprings];//������ ������ 
    Vector3D gravitation = new Vector3D();    // ��������� ���������� �������  

    //������� � �������� ��������� ����� masses[0]
    //����� � ������������, ������� ������������ ��� ������� ������� ������ ����� � ������������ (� �������� 0)
    Vector3D fabricConnectionPos = new Vector3D(0, 2, 0);
    // �������� ����������� ropeConnectionPos � ������� ��� �� ����� ����������� �������
    Vector3D fabricConnectionVel;//=new Vector3D(0,0,0); 
    double springLength = 0;//���������� ��� ������� ������� � ��������� �����
    double springConstant = 0;
    double springFrictionConstant = 0;

    double groundRepulsionConstant = 0;//����������� ������������ ���� �� ����� 
    double groundFrictionConstant = 0;//����������� ������ ���� � ������
    double groundAbsorptionConstant = 0;//����������� ���������� ������ �� ����� (��� ������������ ������������� ������� � ������)
    double groundHeight = 0;//Y ���������� �����
    double airFrictionConstant = 0;//���������� ������ � ������

    public FabricSimulation(
            double m,
            int col,//�������
            int row,//������
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
        super(col * row, m);
        this.gravitation = gravitation;
        this.airFrictionConstant = airFrictionConstant;
        this.groundAbsorptionConstant = groundAbsorptionConstant;
        this.groundFrictionConstant = groundFrictionConstant;
        this.groundHeight = groundHeight;
        this.groundRepulsionConstant = groundRepulsionConstant;

        this.springLength = springLength;
        this.springConstant = springConstant;
        this.springFrictionConstant = springFrictionConstant;

        this.col = col;
        this.row = row;
        //������������� ���������� ������
        //((col-1)*row)-���������� ������ ��������������
        //((row-1)*col)-���������� ������ ������������
        this.numOfSprings = ((col - 1) * row) + ((row - 1) * col);
        //������� ������ ������ 
        springs = new Spring[numOfSprings];
        //������������� ��������� ������� ����----------------------------------

        int i = 0;//�� ������ ������
        int j = 0;//�� �������� ������

        while (j < row) {
            i = 0;
            while (i < col) {
                masses[j * col + i].pos.x = i * springLength;
                masses[j * col + i].pos.y = (row - j) * springLength;
                masses[j * col + i].pos.z = 0;
                i++;
            }
            j++;
        }
        //����� ��������� ��������� ������� ����
        //----------------------------------------------------------------------
        //������� �������, ����������� �����
        i = 0;
        j = 0;
        int k = 0;//������ �������
        //�������������� �������
        while (j < row) {
            i = 0;
            while (i < col - 1) {
                springs[k] = new Spring(masses[j * col + i], masses[j * col + i + 1], springConstant, springLength, springFrictionConstant);
                i++;
                k++;
            }
            j++;
        }
        //������������ �������
        j = 0;
        while (j < row - 1) {
            i = 0;
            while (i < col) {
                springs[k] = new Spring(masses[j * col + i], masses[(j + 1) * col + i], springConstant, springLength, springFrictionConstant);
                i++;
                k++;
            }
            j++;
        }
        //����� �������� ������ ����������� �����-------------------------------

    }

    @Override
    void solve() {
        //��������� ���� ��������� � ������ ��� ������
        for (int i = 0; i < numOfSprings; i++) {
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

    @Override
    int GetTypeOfSimulation() {
        return 5;
    }
    //------------------------------------------------------------------------------
    //�������������� ����� �� ������ Simulation

    @Override
    void simulate(double dt) {
        //��������� ������� 
        //pos+=speed*dt

        fabricConnectionPos = fabricConnectionPos.Add(fabricConnectionVel.Mult(dt));
        //������� �� ����� ��������� ��� ������
        if (fabricConnectionPos.y < groundHeight) {
            fabricConnectionPos.y = groundHeight;
            fabricConnectionVel.y = 0;
        }
        //����� ������� ����
        for (int i = 0; i < col; i++) {
            masses[i].pos = fabricConnectionPos.Add(new Vector3D(i * springLength, 0, 0));
            //��������� �������� ������� �����
            masses[i].speed = fabricConnectionVel;
        }
        //��� ����� ����� � ������ ��������� ������ �������� ����������������
        fabricConnectionVel = fabricConnectionVel.Mult(0.95);

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
    void setFabricConnectionVel(Vector3D fabricConnectionVel) {
        this.fabricConnectionVel = fabricConnectionVel;
    }
//------------------------------------------------------------------------------

    public void setFabricConnectionPos(Vector3D fabricConnectionPos) {
        this.fabricConnectionPos = fabricConnectionPos;
    }

    public void setSpringLength(double springLength) {
        this.springLength = springLength;
    }

    public void setGroundRepulsionConstant(double groundRepulsionConstant) {
        this.groundRepulsionConstant = groundRepulsionConstant;
    }

    public void setGroundFrictionConstant(double groundFrictionConstant) {
        this.groundFrictionConstant = groundFrictionConstant;
    }

    public void setGroundAbsorptionConstant(double groundAbsorptionConstant) {
        this.groundAbsorptionConstant = groundAbsorptionConstant;
    }

    public void setAirFrictionConstant(double airFrictionConstant) {
        this.airFrictionConstant = airFrictionConstant;
    }

    void Draw(GL gl) {
        //��������� �����-----------------------------------------------
        //������ �����
        gl.glBegin(gl.GL_QUADS);
        gl.glColor3d(0.0, 0.0, 1.0);												// Set Color To Light Blue
        gl.glVertex3d(20, groundHeight, 20.0);
        gl.glVertex3d(-20, groundHeight, 20.0);
        gl.glColor3d(0, 0, 0);												// Set Color To Black
        gl.glVertex3d(-20, groundHeight, -20.0);
        gl.glColor3d(1, 0, 0);
        gl.glVertex3d(20, groundHeight, -20.0);
        gl.glEnd();
        //������ ���� �� ������� � ���� �������
        //�������������� �������
        int i = 0;
        int j = 0;
        int k = 0;
        while (j < row) {
            i = 0;
            while (i < col - 1) {
                Mass mass1 = this.getMass(j * col + i);
                Vector3D pos1 = mass1.pos;
                Mass mass2 = this.getMass(j * col + i + 1);
                Vector3D pos2 = mass2.pos;
                //------------------------------------------
                //�������� ����
                gl.glLineWidth(2);
                gl.glBegin(gl.GL_LINE_LOOP);
                gl.glColor3d(0.0, 0.0, 0.0);
                gl.glVertex3d(pos1.x, groundHeight, pos1.z);		// Draw Shadow At groundHeight
                gl.glVertex3d(pos2.x, groundHeight, pos2.z);		// Draw Shadow At groundHeight
                gl.glEnd();
                //------------------------------------------
                //������ �������
                gl.glLineWidth(3);
                gl.glBegin(gl.GL_LINE_LOOP);
                gl.glColor3d(0.0, 1.0, 0.0);
                gl.glVertex3d(pos1.x, pos1.y, pos1.z);
                gl.glVertex3d(pos2.x, pos2.y, pos2.z);
                gl.glEnd();
                //------------------------------------------
                i++;
                k++;
            }
            j++;
        }
        //����� ��������� �������������� �������
        //------------------------------------------------------
        //������������ �������
        j = 0;
        while (j < row - 1) {
            i = 0;
            while (i < col) {
                Mass mass1 = this.getMass(j * col + i);
                Vector3D pos1 = mass1.pos;
                Mass mass2 = this.getMass((j + 1) * col + i);
                Vector3D pos2 = mass2.pos;
                //------------------------------------------
                //�������� ����
                gl.glLineWidth(2);
                gl.glBegin(gl.GL_LINE_LOOP);
                gl.glColor3d(0.0, 0.0, 0.0);
                gl.glVertex3d(pos1.x, groundHeight, pos1.z);
                gl.glVertex3d(pos2.x, groundHeight, pos2.z);
                gl.glEnd();
                //------------------------------------------
                //������ �������
                gl.glLineWidth(3);
                gl.glBegin(gl.GL_LINE_LOOP);
                gl.glColor3d(0.0, 1.0, 0.0);
                gl.glVertex3d(pos1.x, pos1.y, pos1.z);
                gl.glVertex3d(pos2.x, pos2.y, pos2.z);
                gl.glEnd();
                //------------------------------------------
                i++;
                k++;
            }
            j++;
        }
        //����� ��������� ������������ �������
        //------------------------------------------------------
    }

}
