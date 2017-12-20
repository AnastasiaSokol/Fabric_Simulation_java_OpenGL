package org.yourorghere;
/*http://pmg.org.ru/nehe/nehe39.htm */
//------------------------------------------------------------------------------
public class Vector3D {
    double x;
    double y;
    double z;
//------------------------------------------------------------------------------  
    //�����������
    Vector3D(){
        this.x=0;
        this.y=0;
        this.z=0;
    }
    
    //�����������
    Vector3D(double x, double y, double z){
        this.x=x;
        this.y=y;
        this.z=z;    
    }
//------------------------------------------------------------------------------    
    Vector3D Set(Vector3D v){
        this.x=v.x;
        this.y=v.y;
        this.z=v.z;
        return this;
    }
//------------------------------------------------------------------------------
    //�������� ��������
    Vector3D Add(Vector3D v){
        Vector3D newvector = new Vector3D(x+v.x,y+v.y,z+v.z);
        return newvector;
    }
//------------------------------------------------------------------------------
    //��������� ��������
    Vector3D Sub(Vector3D v){
        Vector3D newvector = new Vector3D(x-v.x,y-v.y,z-v.z);
        return newvector;
    }
//------------------------------------------------------------------------------
    //��������� ������� �� ������
    Vector3D Mult(double value){
        Vector3D newvector = new Vector3D(x*value,y*value,z*value);
        return newvector;
    }
//------------------------------------------------------------------------------
    //������� ������� �� ������
    Vector3D Div(double value){
        Vector3D newvector = new Vector3D(x/value,y/value,z/value);
        return newvector;
    }
//------------------------------------------------------------------------------
    //������������� ������
    Vector3D invert(){
        Vector3D newvector = new Vector3D(-x,-y,-z);
        return newvector;
    }
//------------------------------------------------------------------------------
    //����� �������
    double length(){
        return Math.sqrt(x*x+y*y+z*z);
    }
//------------------------------------------------------------------------------
    //����������� ������
    void Normalize(){
        double lenght=this.length();
        if (lenght!=0){
            x=x/lenght;
            y=y/lenght;
            z=z/lenght;
        }
    }
//------------------------------------------------------------------------------
    //���������� ��������������� ������
    Vector3D GetNormalizeVector3D(){
        double lenght=this.length();
        Vector3D normVector3D = new Vector3D();
        if (lenght!=0){
            normVector3D.x=x/lenght;
            normVector3D.y=y/lenght;
            normVector3D.z=z/lenght;
        }
        return normVector3D;
    }
//------------------------------------------------------------------------------
//������� ������� � ����������� ����������� �� 2� �������� (�� ���� �� ���� ������)
//��������� ������������� ���������� ������ �, ������ ��������������� ��� 2�� �������
//������������ �� ���� ������ ������ � ��� ����� ����� ������� ���������������
//������������ �� ��� �������
//�������� ���������� ��������� �������� ������� �� ����� V2
    Vector3D Cross(Vector3D V2) {
        //A=y1z2-y2z1
        //B=-x1z2+x2z1
        //�=x1y2-x2y1
        Vector3D V1 = this;
        double A = V1.y*V2.z-V2.y*V1.z;
        double B = V1.z*V2.x-V2.z*V1.x;
        double C = V1.x*V2.y-V2.x*V1.y;
        Vector3D New= new Vector3D(A,B,C); 
        return New;       
    }    
}
