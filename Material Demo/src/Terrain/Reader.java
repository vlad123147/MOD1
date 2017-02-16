/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Terrain;

import com.jme3.math.Vector3f;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Scanner;


public class Reader {

    public static ArrayList<Vector3f> readPoints(String fileName)
    {
        ArrayList<Vector3f> list = new ArrayList<>();
        int x;
        int y;
        int z;
        String [] strings;
        String [] nums;
        try
        {
            Scanner in = new Scanner(new FileReader(fileName));
            String string;
            while(in.hasNextLine())
            {
               string = in.nextLine();
               x = 0;
               y = 0;
               z = 0;
               strings = string.split(" ");
               
               for (String s : strings)
               {
                   nums = s.split(",");
                   if (nums.length != 3)
                        throw new IOException("Expresia nu are 3 integere"
                                + Arrays.toString(nums));
                   x = Integer.parseInt(nums[0].substring(1));
                   y = Integer.parseInt(nums[1]);
                   z = Integer.parseInt(nums[2].substring(0, nums[2].length() - 1));
                   list.add(new Vector3f(x, y, z));
               }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return list;
    }
}
