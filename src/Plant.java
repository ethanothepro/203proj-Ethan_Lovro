import processing.core.PImage;

import java.util.List;

public abstract class Plant extends ActiveEntity{
    public int health;





    public Plant(int health, int actionPeriod, int animationPeriod, String id,
                 Point position,
                 List<PImage> images){
        super(actionPeriod,animationPeriod,id,position,images);
        this.health = health;
    }


    public void changeHealth(int i){health+=i;}
    public int getHealth(){return health;}
}
