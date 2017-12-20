package org.yourorghere;
/*http://pmg.org.ru/nehe/nehe39.htm */
/*���� ����� ��������� ������ ������� �����
������ ����� ���������  � ������������
�� ���� ���� ����������� ����*/
public class Mass {
    public double m=1.0;//�����
    public Vector3D pos=new Vector3D();//������� 
    public Vector3D speed=new Vector3D();//��������
    public Vector3D force=new Vector3D();//����
//------------------------------------------------------------------------------    
    //�����������
    Mass(double m){
        this.m=m;
    }
//------------------------------------------------------------------------------
    //�������� ����
    void applyForce(Vector3D newforce){
        //�� � �������������� ���� ����������� �� ������ � ������
        //��������� ����� ����
        this.force=this.force.Add(newforce);
    }
//------------------------------------------------------------------------------
    //����� ������������� �������������� ���� � ����
    void forceSetToZero(){
        this.force.x=0;
        this.force.y=0;
        this.force.z=0;
    }
//------------------------------------------------------------------------------
    //����� ��������� �������� � ��������� �����, � ��������� ������ ������� ������ �� ����������� ��� � ���������� �������.
    void simulate(double dt){
        speed=speed.Add((force.Div(m)).Mult(dt));//speed+=(force/m)*dt
        pos=pos.Add(speed.Mult(dt));//pos+=speed*dt
    }

}

