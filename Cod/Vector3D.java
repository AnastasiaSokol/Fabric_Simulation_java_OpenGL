package org.yourorghere;
/*http://pmg.org.ru/nehe/nehe39.htm */
//------------------------------------------------------------------------------
public class Vector3D {
    double x;
    double y;
    double z;
//------------------------------------------------------------------------------  
    //конструктор
    Vector3D(){
        this.x=0;
        this.y=0;
        this.z=0;
    }
    
    //конструктор
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
    //сложение векторов
    Vector3D Add(Vector3D v){
        Vector3D newvector = new Vector3D(x+v.x,y+v.y,z+v.z);
        return newvector;
    }
//------------------------------------------------------------------------------
    //вычитание векторов
    Vector3D Sub(Vector3D v){
        Vector3D newvector = new Vector3D(x-v.x,y-v.y,z-v.z);
        return newvector;
    }
//------------------------------------------------------------------------------
    //умножение вектора на скаляр
    Vector3D Mult(double value){
        Vector3D newvector = new Vector3D(x*value,y*value,z*value);
        return newvector;
    }
//------------------------------------------------------------------------------
    //деление вектора на скаляр
    Vector3D Div(double value){
        Vector3D newvector = new Vector3D(x/value,y/value,z/value);
        return newvector;
    }
//------------------------------------------------------------------------------
    //инвертировать вектор
    Vector3D invert(){
        Vector3D newvector = new Vector3D(-x,-y,-z);
        return newvector;
    }
//------------------------------------------------------------------------------
    //длина вектора
    double length(){
        return Math.sqrt(x*x+y*y+z*z);
    }
//------------------------------------------------------------------------------
    //нормализует вектор
    void Normalize(){
        double lenght=this.length();
        if (lenght!=0){
            x=x/lenght;
            y=y/lenght;
            z=z/lenght;
        }
    }
//------------------------------------------------------------------------------
    //возвращает нормализованный вектор
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
//находим нормаль к поверхности построенной по 2м векторам (то есть по трем точкам)
//векторным произведением называется вектор с, которы перпендикулярен тем 2ум веторам
//представляет из себя правую тройку и его длина равна площади параллелограмма
//построенного на тех веторах
//операция векторного умножения текущего вектора на новый V2
    Vector3D Cross(Vector3D V2) {
        //A=y1z2-y2z1
        //B=-x1z2+x2z1
        //С=x1y2-x2y1
        Vector3D V1 = this;
        double A = V1.y*V2.z-V2.y*V1.z;
        double B = V1.z*V2.x-V2.z*V1.x;
        double C = V1.x*V2.y-V2.x*V1.y;
        Vector3D New= new Vector3D(A,B,C); 
        return New;       
    }    
}
