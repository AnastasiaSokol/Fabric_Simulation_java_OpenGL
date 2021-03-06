package org.yourorghere;
/*
����� ������� ��������� ��� ����� � ������������ ���� � ������ �� ���� ����
--------------------------------------------------------------------------------
F�������=-k*(x-d)
k - ����������� ���������
x - ���������� ����� �� ����� �� ����� �������������
d - ����������, ��� ������� ������� ����� ���������
����� �������� ��������� � �� ������������

���� �� ����� 100 ������ � d=0.5� �� ����� ���������� ������� � 5������
--------------------------------------------------------------------------------
F������=-k*��������
� ������� ��������������� ������ ���� �����
����� ��������� ������� ����� ���������� ���� ���� � 
�������� ������������� ��������
*/
public class Spring {
       public Mass mass1=new Mass(1.0);//������ ����� - ���� ����� �������
       public Mass mass2=new Mass(1.0);;//������ ����� - ������ ����� �������
       public double k=0;  //����������� ��������� �������  
       public double d=0;//����������, ��� ������� ������� ����� ���������
       public double f=0;//����������� ������ �������
       
       //�����������
       Spring(
               Mass mass1,
               Mass mass2,
               double k,
               double d,
               double f
       ){
           this.k=k;
           this.d=d;
           this.f=f;
           this.mass1=mass1;
           this.mass2=mass2;
       }
//------------------------------------------------------------------------------
       //���������� ����
       //����������� ���� ��������� � ������
       void solve(){
           Vector3D springVector=mass1.pos.Sub(mass2.pos);//������ ����� 2�� �������
           double r=springVector.length();
           Vector3D force=new Vector3D(0.0,0.0,0.0);//����
           //��������� ���� ��������� �������
           if(r!=0){
               //������� �������:force = -k * (x - d) 
               //force += -(springVector / r) * (r - d) * k;
               //(springVector / r)  =������ ��������� ����� ����� �������
               force=force.Add(springVector.Div(r).invert().Mult((r-d)*k));
            }
           //��������� ���� ������:F������=-k*��������
           force=force.Add(mass1.speed.Sub(mass2.speed).invert().Mult(f));
           //��������� ���� � ������
           //���� ������ ��������� �� ��� ����� � ��������������� ������������. 
            mass1.applyForce(force.GetNormalizeVector3D());
            mass2.applyForce(force.invert().GetNormalizeVector3D());
       }
       
}
