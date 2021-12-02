public class AnimationAction implements Action {

    private final Animators entity;
    private final int repeatCount;


    public AnimationAction(Animators entity, int repeatCount ) {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler) {

        this.executeAnimationAction( scheduler);


    }
    public void executeAnimationAction(
            EventScheduler scheduler)
    {
        this.entity.nextImage();

        if (this.repeatCount != 1) {
            scheduler.scheduleEvent(this.entity,
                    Factory.createAnimationAction(this.entity,
                            Math.max(this.repeatCount - 1,
                                    0)),
                    (this.entity).getAnimationPeriod());
        }
    }
}
