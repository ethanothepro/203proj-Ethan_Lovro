import processing.core.PImage;

import java.util.List;

public abstract class Collectible extends ActiveEntity {
    public boolean collected = false;

    public Collectible(boolean collected, int actionPeriod, int animationPeriod, String id,
                 Point position,
                 List<PImage> images){
        super(actionPeriod,animationPeriod,id,position,images);
        this.collected = collected;
    }


    public void setCollected(){collected = true;}
}
