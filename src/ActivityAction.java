import javax.swing.*;

public class ActivityAction implements Action{
    private final ActiveEntity entity;
    private final WorldModel world;
    private final ImageStore imageStore;
    ////
    public ActivityAction(ActiveEntity entity, WorldModel world, ImageStore imageStore) {

        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }

    public void executeAction(EventScheduler scheduler) {

        this.executeActivityAction(scheduler);

    }

    public void executeActivityAction(EventScheduler scheduler)
    {

        this.entity.executeActivity(this.world,
            this.imageStore, scheduler);



    }
}
