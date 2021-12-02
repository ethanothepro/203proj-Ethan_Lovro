import processing.core.PImage;

import java.util.List;

public abstract class Animators extends Entity{

    private int animationPeriod;

    public Animators(int animationPeriod, String id,
                     Point position,
                     List<PImage> images){
        super(id,position,images);
        this.animationPeriod = animationPeriod;


    }

    public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore)
    {

        scheduler.scheduleEvent(this,
                Factory.createAnimationAction(this, 0),
                this.getAnimationPeriod());

    }



    public int getAnimationPeriod() {

        return this.animationPeriod;

    }













}
