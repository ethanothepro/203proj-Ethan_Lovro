import processing.core.PImage;

import java.util.List;

public class Coin extends Collectible {

    public static int coinCount = 0;

    public Coin(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod,
            int animationPeriod,
            boolean collected)
    {
        super(collected, actionPeriod, animationPeriod, id, position, images);



    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {

        if (!transformCoin(world,scheduler, imageStore)) {

            scheduler.scheduleEvent(this,
                    Factory.createActivityAction(this, world, imageStore),
                    this.getActionPeriod());
        }
    }




    public boolean transformCoin(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (this.collected) {
            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);
            return true;
        }
        return false;
    }

    public int getCoinCount(){
        return coinCount;
    }
}
