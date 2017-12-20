/*
Класс симуляции веревки
1.Гравитация действует в отрицательном направлении y с 9.81м/с^2
2.Один конец веревки подвешен в 4 метрах над поверхностью
3.Веревка лежит горизонтально до запуска симуляции
4.Расстояние между частицами 5см - это d, расстояние при котором веревка в состоянии покоя
5.Частиц 80 шт
6.Веревка  без напряжения в начале моделирования
7.Масса веревки 4кг
8.На одну частицу 4кг/80шт=0.05кг=50гр
-------------------------------------------
найдем константу пружины k
1.Мы вешаем веревку за верхний конец, она вытянется. 
2.Пружина наверху веревки больше всего вытянется.
3.Нельзя чтобы пружина вытянулась больше, чем на 1 см (0.01 м). 
4.Вес, который эта пружина несет – это почти вся веревка (частица на верхнем конце исключительна). 

Fтяжести =(масса веревки)*(гравитационное ускорение)= 4*9.8=40Н

5.Силы упругости пружины должна сбалансировать 40Н

Fупругости = -k*x=-k*0.01м

6.Сумма этих сил должна быть равной нулю

40Н+(-k*0.01м)=0
k=4000[Н/м]
-------------------------------------------
найдем константу трения в пружине f
выбрано экспериментальным путем f=0.2 H/(м/c)
 */
package org.yourorghere;

import javax.media.opengl.GL;

/*В классе RopeSimulation, мы переопределим метод solve() и simulate(float dt), 
поскольку мы имеем специальную реализацию этих методов для веревки. 
Мы применим метод solve(), 
и закрепим верхний конец веревки в методе simulate(float dt).*/
//------------------------------------------------------------------------------
public class RopeSimulation extends Simulation {
//------------------------------------------------------------------------------

    Spring[] springs = new Spring[numOfMasses - 1];//массив пружин 
    Vector3D gravitation = new Vector3D();    // ускорение свободного падения  

    //позиция и скорость начальной точки masses[0]
    //Точка в пространстве, которая используется для задания позиции первой массы в пространстве (с индексом 0)
    Vector3D ropeConnectionPos = new Vector3D(0, 2, 0);
    // Скорость перемещения ropeConnectionPos с помощью нее мы можем раскачивать веревку
    Vector3D ropeConnectionVel;//=new Vector3D(0,0,0);
    double springConstant = 0;
    double springLength = 0;
    double springFrictionConstant = 0;

    double groundRepulsionConstant = 0;//коэффициент отталкивания масс от земли 
    double groundFrictionConstant = 0;//коэффициент трения масс с землей
    double groundAbsorptionConstant = 0;//коэффициент поглощения трения об землю (при вертикальных столкновениях веревки с землей)
    double groundHeight = 0;//Y координата земли
    double airFrictionConstant = 0;//коэфициент трения о воздух
//------------------------------------------------------------------------------
    //конструктор

    public RopeSimulation(
            int numOfMasses,
            double m,
            double springConstant,//коэффициент натяжения пружины
            double springLength,//расстояние, при котором пружина будет устойчива
            double springFrictionConstant,//коэффициент трения пружины
            Vector3D gravitation,//ускорение свободного падения  
            double airFrictionConstant,//коэфициент трения о воздух
            double groundRepulsionConstant,//коэффициент отталкивания масс от земли 
            double groundFrictionConstant,//коэффициент трения масс с землей
            double groundAbsorptionConstant,//коэффициент поглощения трения об землю (при вертикальных столкновениях веревки с землей)
            double groundHeight//Y координата земли  
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
        //веревка лежит горизонтально параллельно земле
        //      0-0-0-0-0-0-0-0
        //---------------------------
        //устанавливаем начальную позицию масс
        for (int i = 0; i < numOfMasses; i++) {

            masses[i].pos.x = i * springLength;//X-позиция masses[a] с расстоянием
            masses[i].pos.y = 0;
            masses[i].pos.z = 0;

        }
        //---------------------------
        //создаем пружины соединяющие массы
        springs = new Spring[numOfMasses - 1];
        for (int i = 0; i < numOfMasses - 1; i++) {
            springs[i] = new Spring(masses[i], masses[i + 1], this.springConstant, this.springLength, this.springFrictionConstant);
        }

    }
//------------------------------------------------------------------------------
    //применение сил

    @Override
    void solve() {
        //применяем силы натяжения и трения для пружин
        for (int i = 0; i < numOfMasses - 1; i++) {
            springs[i].solve();//к пружине применяется сила упругости и трения
        }
        //применяем силы к нашим массам
        for (int i = 0; i < numOfMasses; i++) {
            //применяем силу гравитации F=g*m
            masses[i].applyForce(gravitation.Mult(masses[i].m));

            //применяем силу трения о воздух Fтрения=-k*скорость
            masses[i].applyForce(masses[i].speed.Mult(airFrictionConstant).invert());
            //если массы коснулись земли то 
            //применим силы отталкивания от земли
            if (masses[i].pos.y <= groundHeight) {
                //Возьмем вектор скорости массы, принебрегая компонент скорости 
                //в Y направлении, так как мы будем применять силу трения-скольжения параллельно земле
                //Компонента в Y направлении будет нужна для эффекта поглощения
                Vector3D v = masses[i].speed;//временный вектор
                v.y = 0;
                //применяем силу трения о землю Fтрения=-k*скорость
                masses[i].applyForce(v.Mult(groundFrictionConstant).invert());
                //а теперь возмем компоненту в направлении Y
                v = masses[i].speed;
                v.x = 0;
                v.z = 0;
                //применяем силу поглощения
                if (v.y < 0) {
                    masses[i].applyForce(v.Mult(groundAbsorptionConstant).invert());
                }
                //------------------------------
                //применяем силу отталкивания
                //земля будет отталкивать массы подобно пружине
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
    //переопределяем метод из класса Simulation

    @Override
    void simulate(double dt) {
        //изменение позиции 
        //pos+=speed*dt

        ropeConnectionPos = ropeConnectionPos.Add(ropeConnectionVel.Mult(dt));
        //веревка не будет двигаться под землей
        if (ropeConnectionPos.y < groundHeight) {
            ropeConnectionPos.y = groundHeight;
            ropeConnectionVel.y = 0;
        }
        //сдвиг верхней массы
        masses[0].pos = ropeConnectionPos;
        //изменение скорости верхней массы
        masses[0].speed = ropeConnectionVel;
        //мне нужно чтобы с каждой иттерации вектор скорости стабилизировался
        ropeConnectionVel = ropeConnectionVel.Mult(0.95);

        //В классе simulate уже учитывается симуляция каждого узла веревки
        for (int i = 0; i < numOfMasses; i++) {
            masses[i].simulate(dt);
        }

    }
//------------------------------------------------------------------------------    

    /*функция используется при моделировании. 
    Используя клавиши мы задаем ropeConnectionVel, и 
    мы можем перемещать веревку так, как если бы мы 
    держали ее за один конец.*/
    void setRopeConnectionVel(Vector3D ropeConnectionVel) {
        this.ropeConnectionVel = ropeConnectionVel;
    }
//------------------------------------------------------------------------------    

    void Draw(GL gl) {

        //рисуем землю
        gl.glBegin(gl.GL_QUADS);
        gl.glColor3d(0.0, 0.0, 1.0);												// Set Color To Light Blue
        gl.glVertex3d(20, this.groundHeight, 20.0);
        gl.glVertex3d(-20, this.groundHeight, 20.0);
        gl.glColor3d(0, 0, 0);												// Set Color To Black
        gl.glVertex3d(-20, this.groundHeight, -20.0);
        gl.glColor3d(1, 0, 0);
        gl.glVertex3d(20, this.groundHeight, -20.0);
        gl.glEnd();
        //рисуем тень от веревки и саму веревку
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
            //рисуем веревку
            gl.glLineWidth(3);
            gl.glBegin(gl.GL_LINE_LOOP);
            gl.glColor3d(0.0, 1.0, 0.0);
            gl.glVertex3d(pos1.x, pos1.y, pos1.z);
            gl.glVertex3d(pos2.x, pos2.y, pos2.z);
            gl.glEnd();
        }

    }
}
