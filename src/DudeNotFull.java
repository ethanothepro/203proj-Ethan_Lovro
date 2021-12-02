import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import processing.core.PImage;
public class DudeNotFull extends MovingEntity{


    private final int resourceLimit;
    private int resourceCount;



    public DudeNotFull(
            String id,
            Point position,
            List<PImage> images,
            int resourceLimit,
            int resourceCount,
            int actionPeriod,
            int animationPeriod)
    {
        super(actionPeriod,animationPeriod,id,position,images);
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
    }






    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        List<PImage> images = new ArrayList<>();
        Tree tree = new Tree("0", new Point(0,0),images,0,0,0);
        Sapling sapling = new Sapling("0",new Point(0,0),images,0,0,0,0);
        Optional<Entity> target =
                Functions.findNearest(world, super.getPosition(), new ArrayList<>(Arrays.asList(tree, sapling)));

        if (!target.isPresent() || !moveTo( world,
                (Plant) target.get(),
                scheduler)
                || !transformNotFull(world, scheduler, imageStore))
        {
            scheduler.scheduleEvent(this,
                    Factory.createActivityAction(this, world, imageStore),
                    this.getActionPeriod());
        }
    }



    public boolean transformNotFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (this.resourceCount >= this.resourceLimit) {
            DudeFull miner = Factory.createDudeFull(super.getId(),
                    super.getPosition(), this.getActionPeriod(),
                    this.getAnimationPeriod(),
                    this.resourceLimit,
                    this.getImages());

            world.removeEntity( this);
            scheduler.unscheduleAllEvents( this);

            world.addEntity(miner);
            miner.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public Point nextPositionDude(WorldModel world, Point destPos)
    {

        PathingStrategy strat = new AStarPathingStrategy();
        Predicate<Point> p = p1 -> world.withinBounds(p1) && (!(world.isOccupied(p1)) || world.getOccupancyCell(p1) instanceof Stump);
        BiPredicate<Point,Point> b = (p1,p2) -> Functions.adjacent(p1,p2) ;
        List<Point> path = strat.computePath(super.getPosition(), destPos,p,b,PathingStrategy.CARDINAL_NEIGHBORS);

        if (path.size() == 0)
        {
            return super.getPosition();
        }

        return path.get(0);



    }







    public boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (Functions.adjacent(super.getPosition(), (target).getPosition())) {
            this.resourceCount += 1;

            ((Plant)target).changeHealth(-1);
            return true;
        }
        else {
            Point nextPos = this.nextPositionDude(world, (target).getPosition());

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


