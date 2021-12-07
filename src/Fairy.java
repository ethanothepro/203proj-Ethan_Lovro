import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import processing.core.PImage;
public class Fairy extends MovingEntity{



    public Fairy(
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

        List<PImage> images = new ArrayList<>();
        Stump stump = new Stump("0",new Point(0,0),images);
        Cactus cactus = new Cactus("0", new Point(0,0), images);
        Optional<Entity> fairyTarget =
                Functions.findNearest(world, super.getPosition(), new ArrayList<>(Arrays.asList(stump,cactus)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();


            if (moveTo(world, fairyTarget.get(), scheduler)) {

                if (fairyTarget.get() instanceof Stump) {
                    Sapling sapling = Factory.createSapling("sapling_" + super.getId(), tgtPos,
                            imageStore.getImageList(Functions.SAPLING_KEY));

                    world.addEntity(sapling);
                    sapling.scheduleActions(scheduler, world, imageStore);
                }

                else{
                    Coin coin= new Coin("Coin",tgtPos,imageStore.getImageList("coin"),1000,Functions.getNumFromRange(100,6),false) ;
                    world.addEntity(coin);
                    coin.scheduleActions(scheduler,world,imageStore);
                    world.setBackground(tgtPos, new Background("sandstone", imageStore.getImageList("sandstone")));
                }

            }
        }

        scheduler.scheduleEvent( this,
                Factory.createActivityAction(this, world, imageStore),
                this.getActionPeriod());
    }






    public Point nextPositionFairy(WorldModel world, Point destPos)
    {
        PathingStrategy strat = new AStarPathingStrategy();
        Predicate<Point> p = p1 -> world.withinBounds(p1) && (!(world.isOccupied(p1)));
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
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        }
        else {
            Point nextPos = this.nextPositionFairy(world, target.getPosition());

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



