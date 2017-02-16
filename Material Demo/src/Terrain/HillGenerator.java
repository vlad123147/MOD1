/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Terrain;

import com.jme3.math.Vector3f;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import static com.jme3.terrain.heightmap.AbstractHeightMap.NORMALIZE_RANGE;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class HillGenerator extends AbstractHeightMap {

    private int iterations; // how many hills to generate
    private float minRadius; // the minimum size of a hill radius
    private float maxRadius; // the maximum size of a hill radius
    private long seed; // the seed for the random number generator
    private ArrayList<Vector3f> points;
    private float zmax;
    
    public HillGenerator(int size, ArrayList<Vector3f> points, float minRadius,
            float maxRadius, long seed, float zmax) throws Exception {
        if (size <= 0 || minRadius <= 0 || maxRadius <= 0
                || minRadius >= maxRadius) {
            throw new Exception(
                    "Either size of the terrain is not greater that zero, "
                    + "or number of iterations is not greater that zero, "
                    + "or minimum or maximum radius are not greater than zero, "
                    + "or minimum radius is greater than maximum radius, "
                    + "or power of flattening is below one");
        }
        this.size = size;
        this.seed = seed;
        this.iterations= points.size();
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.points = points;
        this.zmax = zmax;
        load();
    }

    public boolean load() {
        // clean up data if needed.
        if (null != heightData) {
            unloadHeightMap();
        }
        heightData = new float[size * size];
        float[][] tempBuffer = new float[size][size];
        Random random = new Random(seed);


        for (int i = 0; i < iterations; i++) {
            addHill(tempBuffer, random, points.get(i).x, points.get(i).y, points.get(i).z);
        }

        // transfer temporary buffer to final heightmap
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                setHeightAtPoint((float) tempBuffer[i][j], j, i);
            }
        }

        normalizeTerrain(NORMALIZE_RANGE);
        return true;
    }

    protected void addHill(float[][] tempBuffer, Random random, float x, float y, float z) {
        // Pick the radius for the hill
        float coef = (float)190 / zmax;
        //float radius = (float)Math.sqrt(z);
        float radius = z * coef;
        System.err.printf("X = %f Y = %f  FOR HEIGHT %fRADIUS IS %f",x,y,z, radius);
        float radiusSq = radius * radius;
        float distSq;
        float height;

        // Find the range of hills affected by this hill
        int xMin = Math.round(x - radius - 1);
        int xMax = Math.round(x + radius + 1);

        int yMin = Math.round(y - radius - 1);
        int yMax = Math.round(y + radius + 1);

        // Don't try to affect points outside the heightmap
        if (xMin < 0) {
            xMin = 0;
        }
        if (xMax > size) {
            xMax = size - 1;
        }

        if (yMin < 0) {
            yMin = 0;
        }
        if (yMax > size) {
            yMax = size - 1;
        }
        float MAX = Float.MIN_VALUE;
        int maxI = 0;
        int maxy = 0;
        for (int i = xMin; i <= xMax; i++) {
            for (int j = yMin; j <= yMax; j++) {
                distSq = (x - i) * (x - i) + (y - j) * (y - j);
                height = radiusSq - distSq;
                
                if (height > 0) {
                    tempBuffer[i][j] += height;
                    if (tempBuffer[i][j] > MAX)
                    {
                        maxI = i;
                        maxy = j;
                        MAX = (int)tempBuffer[i][j];
                    }
                }
            }
        }

    }

    private float randomRange(Random random, float min, float max) {
        int next = Math.abs(random.nextInt());
        return (next * (max - min)/ Integer.MAX_VALUE) + min;
    }

    /**
     * Sets the number of hills to grow. More hills usually mean a nicer
     * heightmap.
     *
     * @param iterations
     *            the number of hills to grow
     * @throws Exception
     * @throws JmeException
     *             if iterations if not greater than zero
     */
    public void setIterations(int iterations) throws Exception {
        if (iterations <= 0) {
            throw new Exception(
                    "Number of iterations is not greater than zero");
        }
        this.iterations = iterations;
    }

    /**
     * Sets the minimum radius of a hill.
     *
     * @param maxRadius
     *            the maximum radius of a hill
     * @throws Exception
     * @throws JmeException
     *             if the maximum radius if not greater than zero or not greater
     *             than the minimum radius
     */
    public void setMaxRadius(float maxRadius) throws Exception {
        if (maxRadius <= 0 || maxRadius <= minRadius) {
            throw new Exception("The maximum radius is not greater than 0, "
                    + "or not greater than the minimum radius");
        }
        this.maxRadius = maxRadius;
    }

    /**
     * Sets the maximum radius of a hill.
     *
     * @param minRadius
     *            the minimum radius of a hill
     * @throws Exception
     * @throws JmeException if the minimum radius is not greater than zero or not
     *        lower than the maximum radius
     */
    public void setMinRadius(float minRadius) throws Exception {
        if (minRadius <= 0 || minRadius >= maxRadius) {
            throw new Exception("The minimum radius is not greater than 0, "
                    + "or not lower than the maximum radius");
        }
        this.minRadius = minRadius;
    }
}
