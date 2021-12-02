import processing.core.PImage;

import java.util.List;

public abstract class ActiveEntity extends Animators{
    private final int actionPeriod;


    public ActiveEntity(int actionPeriod, int animationPeriod, String id,
                        Point position,
                        List<PImage> images){
        super(animationPeriod,id,position,images);

        this.actionPeriod = actionPeriod;
    }


    public abstract void executeActivity(WorldModel world,
                         ImageStore imageStore,
                         EventScheduler scheduler);

    public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore)
    {

        scheduler.scheduleEvent(this,
                Factory.createActivityAction(this, world, imageStore),
                this.actionPeriod);
        super.scheduleActions(scheduler, world, imageStore);

    }


    protected int getActionPeriod(){return this.actionPeriod;}



}

