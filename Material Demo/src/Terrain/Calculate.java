/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Terrain;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *dawdawdada
 * @author dprovorn
 */
public class Calculate {
    public static float ZMAX = 0;
    public static ArrayList<Vector3f> surface(ArrayList<Vector3f> coord)
    {
       ArrayList<Vector3f> local = new ArrayList<>();
       Vector3f xmin = new Vector3f();
       Vector3f xmax = new Vector3f();
       Vector3f ymin = new Vector3f();
       Vector3f ymax = new Vector3f();
       ymin.y = xmin.x = Float.MAX_VALUE;
       xmax.x = ymax.y = Float.MIN_VALUE; 
       for(Vector3f t:coord)
       {
           if (t.x < xmin.x)
               xmin = t;
           if (t.x > xmax.x)
               xmax = t;
           if (t.y < ymin.y)
               ymin = t;
           if (t.y > ymax.y)
               ymax = t;
           if (t.z > ZMAX)
               ZMAX = t.z;
       }
       float dx; float ox; float distx;
       float dy; float oy; float disty;
       dx = FastMath.abs(xmax.x+(float)190/ZMAX*xmax.z - (xmin.x-190/ZMAX*xmax.z));
       dy = FastMath.abs(ymax.y+(float)190/ZMAX*ymax.z - (ymin.y-190/ZMAX*xmax.z));
       ox = xmin.x - dx/2;
       oy = ymin.y - dy/2;
       
       distx = dx *2;
       disty = dy *2;
       float coex; float coey;
       coex = (float)1024 / distx;
       coey = (float)1024 / disty;
       for(Vector3f t:coord)
       {
           Vector3f yes = new Vector3f();
           yes.x = (t.x-ox)*coex+190*coex;
           yes.y = (t.y-oy)*coey+190*coey;
           yes.z = t.z;
           local.add(yes);
       }
       return local;
    }
}
