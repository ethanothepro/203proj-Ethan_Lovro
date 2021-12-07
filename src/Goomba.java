import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import processing.core.PImage;
public class Goomba extends MovingEntity {

    Point a;
    Point b;
    Point currentTarget;
    boolean isRight = false;
    public ImageStore imageStore;
    boolean alive = true;

    public Goomba(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod,
            int animationPeriod) {
        super(actionPeriod, animationPeriod, id, position, images);
        this.a = this.getPosition();
        this.b = new Point(this.getPosition().x + 5, this.getPosition().y);
        this.currentTarget = b;
    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {

        //System.out.println("hello");
        this.imageStore = imageStore;
        if (!moveTo(world, currentTarget, scheduler) || !transformGoomba(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this,
                    Factory.createActivityAction(this, world, imageStore),
                    this.getActionPeriod());
        }
    }

    public Point nextPositionGoomba(WorldModel world, Point destPos) {
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
            Point target,
            EventScheduler scheduler) {

        //System.out.println(currentTarget);
        if (Functions.adjacent(super.getPosition(), currentTarget)) {
            if (currentTarget.equals(a)) {
                currentTarget = b;
            }
            else {
                currentTarget = a;
            }


            return true;
        }
        else {
            Point nextPos = this.nextPositionGoomba(world, currentTarget);


            if (!super.getPosition().equals(nextPos)) {
                world.moveEntity(this, nextPos);
            }
            return false;
        }

    }

    public boolean transformGoomba(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (!this.alive) {
            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);
            return true;
        }
        return false;
    }

    public void setDead() {alive = false;}
}


