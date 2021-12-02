import java.util.*;
import java.util.function.Function;

import processing.core.PImage;

public class Tree extends Plant{





    private static final String STUMP_KEY = "stump";


    public Tree(

            String id,
            Point position,
            List<PImage> images,
            int actionPeriod,
            int animationPeriod,
            int health)
    {
        super(health,actionPeriod,animationPeriod,id,position,images);

    }








    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {

        if (!transformTree( world,scheduler, imageStore)) {

            scheduler.scheduleEvent(this,
                    Factory.createActivityAction(this, world, imageStore),
                    this.getActionPeriod());
        }
    }




    public boolean transformTree(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (this.health <= 0) {
            Stump stump = Factory.createStump(super.getId(),
                    super.getPosition(),
                    imageStore.getImageList(STUMP_KEY));

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(stump);


            return true;
        }

        return false;
    }






}


