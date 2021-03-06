package org.yourorghere;
/*http://pmg.org.ru/nehe/nehe39.htm */
/*� ������ �������� ���������� ���� � ��� �� �������. ���� ����������, 
����������� ��� �������������� ����, �������������� ����� �������� � ���������. 
���� ������� ������������ �� ��� ���, ���� ���������� �����. 
�� ����������� � ������ Simulation.*/
abstract public class Simulation {
    public int numOfMasses=0;//���������� ���� � ���������
    public Mass[] masses=new Mass[numOfMasses];//������ ����
//------------------------------------------------------------------------------
    //�����������
    Simulation(int numOfMasses, double m){
        this.numOfMasses=numOfMasses;
        masses = new Mass[numOfMasses];//������� ������
        // ������� Mass � ������� ��� � ������
        for (int i=0;i<numOfMasses;i++){
            masses[i]=new Mass(m);
        }
    }
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------    
    Mass getMass(int index){
     if (index<0 || index>numOfMasses) {return null;}
     else return masses[index];
    }
//------------------------------------------------------------------------------
    /*��������� ��������� ����� ��� ����:
 
      1. init() � ������������� ���� (Mass->force) � 0
      2. solve() � ��������� ����
      3. simulate(float dt) � ����� ��������
    */
//------------------------------------------------------------------------------
    void forceSetToZeroForAllMasses(){
        for(int i=0;i<numOfMasses;i++){
            masses[i].forceSetToZero();
        }
    }
//------------------------------------------------------------------------------
    //��������� ����
    abstract void solve();
        /*��� ���� �.�. � ������� ������ � ��� ��� ���
        � ������ ����������� �� ������������� ���� �����*/
    
//------------------------------------------------------------------------------
    //�������� ��� ������ ��cc
    //����� ��������� �������� � ��������� �����, � ��������� ������ ������� ������ �� ����������� ��� � ���������� �������.
    void simulate (double dt){
        for(int i=0;i<numOfMasses;i++){
            masses[i].simulate(dt);
        }
    }
//------------------------------------------------------------------------------
    //��������� ��������� ��������� � ���� �����
    void operate (double dt){
       forceSetToZeroForAllMasses(); 
       solve();
       simulate(dt);
    }
//------------------------------------------------------------------------------
    //1-���������� �������� ���� � ���������� ���������
    //2-���������� �������� ���� ��� ��������� ��� ����������
    //3-���������� �������� ���� �� �������
    //4-���������� �������
    abstract int GetTypeOfSimulation();
    
}
