/*
Класс симуляции ткани
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

    int col = 0;//количество масс по горизонтали (столбцов сколько)
    int row = 0;//количество масс по вертикали (строк сколько)
    int numOfSprings = 0;//значение вычисляется и переопределяется в конструкторе
    Spring[] springs = new Spring[numOfSprings];//массив пружин 
    Vector3D gravitation = new Vector3D();    // ускорение свободного падения  

    //позиция и скорость начальной точки masses[0]
    //Точка в пространстве, которая используется для задания позиции первой массы в пространстве (с индексом 0)
    Vector3D fabricConnectionPos = new Vector3D(0, 2, 0);
    // Скорость перемещения ropeConnectionPos с помощью нее мы можем раскачивать веревку
    Vector3D fabricConnectionVel;//=new Vector3D(0,0,0); 
    double springLength = 0;//расстояние при котором веревка в состоянии покоя
    double springConstant = 0;
    double springFrictionConstant = 0;

    double groundRepulsionConstant = 0;//коэффициент отталкивания масс от земли 
    double groundFrictionConstant = 0;//коэффициент трения масс с землей
    double groundAbsorptionConstant = 0;//коэффициент поглощения трения об землю (при вертикальных столкновениях веревки с землей)
    double groundHeight = 0;//Y координата земли
    double airFrictionConstant = 0;//коэфициент трения о воздух

    public FabricSimulation(
            double m,
            int col,//столбцы
            int row,//строки
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
        //устанавливаем количество пружин
        //((col-1)*row)-количество пружин горизонтальных
        //((row-1)*col)-количество пружин вертикальных
        this.numOfSprings = ((col - 1) * row) + ((row - 1) * col);
        //создаем массив пружин 
        springs = new Spring[numOfSprings];
        //устанавливаем начальную позицию масс----------------------------------

        int i = 0;//по строке индекс
        int j = 0;//по столбцам индекс

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
        //конец установки начальных позиций масс
        //----------------------------------------------------------------------
        //создаем пружины, соединяющие массы
        i = 0;
        j = 0;
        int k = 0;//индекс пружины
        //горизонтальные пружины
        while (j < row) {
            i = 0;
            while (i < col - 1) {
                springs[k] = new Spring(masses[j * col + i], masses[j * col + i + 1], springConstant, springLength, springFrictionConstant);
                i++;
                k++;
            }
            j++;
        }
        //вертикальные пружины
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
        //конец создания пружин соединяющих массы-------------------------------

    }

    @Override
    void solve() {
        //применяем силы натяжения и трения для пружин
        for (int i = 0; i < numOfSprings; i++) {
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

    @Override
    int GetTypeOfSimulation() {
        return 5;
    }
    //------------------------------------------------------------------------------
    //переопределяем метод из класса Simulation

    @Override
    void simulate(double dt) {
        //изменение позиции 
        //pos+=speed*dt

        fabricConnectionPos = fabricConnectionPos.Add(fabricConnectionVel.Mult(dt));
        //веревка не будет двигаться под землей
        if (fabricConnectionPos.y < groundHeight) {
            fabricConnectionPos.y = groundHeight;
            fabricConnectionVel.y = 0;
        }
        //сдвиг верхних масс
        for (int i = 0; i < col; i++) {
            masses[i].pos = fabricConnectionPos.Add(new Vector3D(i * springLength, 0, 0));
            //изменение скорости верхней массы
            masses[i].speed = fabricConnectionVel;
        }
        //мне нужно чтобы с каждой иттерации вектор скорости стабилизировался
        fabricConnectionVel = fabricConnectionVel.Mult(0.95);

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
        //симуляция ткани-----------------------------------------------
        //рисуем землю
        gl.glBegin(gl.GL_QUADS);
        gl.glColor3d(0.0, 0.0, 1.0);												// Set Color To Light Blue
        gl.glVertex3d(20, groundHeight, 20.0);
        gl.glVertex3d(-20, groundHeight, 20.0);
        gl.glColor3d(0, 0, 0);												// Set Color To Black
        gl.glVertex3d(-20, groundHeight, -20.0);
        gl.glColor3d(1, 0, 0);
        gl.glVertex3d(20, groundHeight, -20.0);
        gl.glEnd();
        //рисуем тень от веревки и саму веревку
        //горизонтальные веревки
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
                //нарисуем тень
                gl.glLineWidth(2);
                gl.glBegin(gl.GL_LINE_LOOP);
                gl.glColor3d(0.0, 0.0, 0.0);
                gl.glVertex3d(pos1.x, groundHeight, pos1.z);		// Draw Shadow At groundHeight
                gl.glVertex3d(pos2.x, groundHeight, pos2.z);		// Draw Shadow At groundHeight
                gl.glEnd();
                //------------------------------------------
                //рисуем веревку
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
        //конец рисования горизонтальных веревок
        //------------------------------------------------------
        //вертикальные пружины
        j = 0;
        while (j < row - 1) {
            i = 0;
            while (i < col) {
                Mass mass1 = this.getMass(j * col + i);
                Vector3D pos1 = mass1.pos;
                Mass mass2 = this.getMass((j + 1) * col + i);
                Vector3D pos2 = mass2.pos;
                //------------------------------------------
                //нарисуем тень
                gl.glLineWidth(2);
                gl.glBegin(gl.GL_LINE_LOOP);
                gl.glColor3d(0.0, 0.0, 0.0);
                gl.glVertex3d(pos1.x, groundHeight, pos1.z);
                gl.glVertex3d(pos2.x, groundHeight, pos2.z);
                gl.glEnd();
                //------------------------------------------
                //рисуем веревку
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
        //конец рисования вертикальных веревок
        //------------------------------------------------------
    }

}
