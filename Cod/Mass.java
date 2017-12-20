package org.yourorghere;
/*http://pmg.org.ru/nehe/nehe39.htm */
/*Этот класс описывает объект имеющий массу
объект имеет положение  в пространстве
На него могу действовать силы*/
public class Mass {
    public double m=1.0;//масса
    public Vector3D pos=new Vector3D();//позиция 
    public Vector3D speed=new Vector3D();//скорость
    public Vector3D force=new Vector3D();//сила
//------------------------------------------------------------------------------    
    //конструктор
    Mass(double m){
        this.m=m;
    }
//------------------------------------------------------------------------------
    //добавить силу
    void applyForce(Vector3D newforce){
        //мы к результирующей силе действующий на объект с массой
        //добавляем новую силу
        this.force=this.force.Add(newforce);
    }
//------------------------------------------------------------------------------
    //метод устанавливает результирующую силу в ноль
    void forceSetToZero(){
        this.force.x=0;
        this.force.y=0;
        this.force.z=0;
    }
//------------------------------------------------------------------------------
    //метод вычисляет скорость и положение массы, в следующий момент времени исходя из примененных сил и прошедшего времени.
    void simulate(double dt){
        speed=speed.Add((force.Div(m)).Mult(dt));//speed+=(force/m)*dt
        pos=pos.Add(speed.Mult(dt));//pos+=speed*dt
    }

}

