/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Water;

/**
 *
 * @author dprovorn
 */

public class WaterCell {
   
    private float amount;


    public float terrainHeitgt;
    public int   direction;
    public float incomingAmount;
    
    public void adjustAmount(float delta)
    {
        amount += delta;
    }
    
    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
    
    public float   compareCells(WaterCell cell2)
    {
        float difference = (cell2.terrainHeitgt + cell2.getAmount() - terrainHeitgt - getAmount());
        float amountToChange = difference * 0.5f;
        amountToChange = Math.min(amountToChange, cell2.getAmount());
        if (amountToChange > 0)
        {
            incomingAmount += amountToChange;
            cell2.adjustAmount(-amountToChange);
        }
        return amountToChange;
    }
   
}
