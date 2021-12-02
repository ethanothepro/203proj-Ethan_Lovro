import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import processing.core.PImage;
public class DudeFull extends MovingEntity{


    private final int resourceLimit;


    public DudeFull(
            String id,
            Point position,
            List<PImage> images,
            int resourceLimit,
            int actionPeriod,
            int animationPeriod)
    {
        super(actionPeriod,animationPeriod,id,position,images);
        this.resourceLimit = resourceLimit;

    }





    public void transformFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        DudeNotFull miner = Factory.createDudeNotFull(super.getId(),
                super.getPosition(), this.getActionPeriod(),
                this.getAnimationPeriod(),
                this.resourceLimit,
                this.getImages());

        world.removeEntity(this);
        scheduler.unscheduleAllEvents( this);

        world.addEntity(miner);
        miner.scheduleActions(scheduler, world, imageStore);
    }


    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        House house = new House("0",new Point(0,0),getImages());
        Optional<Entity> fullTarget =
                Functions.findNearest(world, super.getPosition(), new ArrayList<>(Arrays.asList(house)));



        Boolean b = this.moveTo( world,
                fullTarget.get(), scheduler);

        if (fullTarget.isPresent() && b)
        {
            this.transformFull(world, scheduler, imageStore);
        }
        else {
            scheduler.scheduleEvent(this,
                    Factory.createActivityAction(this, world, imageStore),
                    this.getActionPeriod());

        }
    }



    public Point nextPositionDude(WorldModel world, Point destPos) {
        PathingStrategy strat = new AStarPathingStrategy();
        Predicate<Point> p = p1 -> world.withinBounds(p1) && (!(world.isOccupied(p1)) );
        BiPredicate<Point, Point> b = (p1, p2) -> Functions.adjacent(p1, p2);
        List<Point> path = strat.computePath(super.getPosition(), destPos, p, b, PathingStrategy.CARDINAL_NEIGHBORS);

        if (path.size() == 0) {
            return super.getPosition();
        }

        return path.get(0);
    }





    public boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (Functions.adjacent(super.getPosition(), target.getPosition())) {
            return true;
        }
        else {
            Point nextPos = this.nextPositionDude(world, target.getPosition());

            if (!super.getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }


}

