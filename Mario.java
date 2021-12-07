import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Mario extends MovingEntity{

    public ImageStore imageStore;

    public Mario(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod,
            int animationPeriod)
    {
        super(actionPeriod,animationPeriod,id,position,images);

    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {

        this.imageStore = imageStore;
        System.out.println("Activity");
        List<PImage> images = new ArrayList<>();
        Coin coin= new Coin("0", new Point(0, 0), images, 0, 0, false);
        Goomba goomba = new Goomba("Goomba", new Point(0,0), images, 0,0);
        Optional<Entity> target =
                Functions.findNearest(world, super.getPosition(), new ArrayList<>(Arrays.asList(coin, goomba)));

        if (!target.isPresent() || !moveTo( world, target.get(), scheduler))
        {

            scheduler.scheduleEvent(this,
                    Factory.createActivityAction(this, world, imageStore),
                    this.getActionPeriod());
        }






    }



    public boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {


        if (Functions.adjacent(super.getPosition(), (target).getPosition())) {
            if(target instanceof Coin) {
                ((Coin)target).setCollected();
                scheduleActions(scheduler,world,imageStore);
            }

            if(target instanceof Goomba) {
                ((Goomba)target).setDead();
                scheduleActions(scheduler,world,imageStore);
            }




            return true;
        }
        else {
            Point nextPos = this.nextPosition(world, (target).getPosition());

            if (!super.getPosition().equals(nextPos)) {

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

    public Point nextPosition(WorldModel world, Point destPos)
    {

        PathingStrategy strat = new AStarPathingStrategy();
        Predicate<Point> p = p1 -> world.withinBounds(p1) && (!(world.isOccupied(p1)) || world.getOccupancyCell(p1) instanceof Pipe);
        BiPredicate<Point,Point> b = (p1, p2) -> Functions.adjacent(p1,p2) ;
        List<Point> path = strat.computePath(super.getPosition(), destPos,p,b,PathingStrategy.CARDINAL_NEIGHBORS);

        if (path.size() == 0)
        {
            return super.getPosition();
        }

        return path.get(0);



    }


}