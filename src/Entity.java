import processing.core.PImage;

import java.util.List;

public abstract class Entity {

    private final List<PImage> images;
    private int imageIndex = 0;
    private final String id;
    private Point position;

    public Entity(String id,
                  Point position,
                  List<PImage> images){
        this.id = id;
        this.position = position;
        this.images = images;
    }


    public String getId(){return id;}

    public Point getPosition(){return position;}

    public void setPosition(Point a){
        position = a;
    }




    public PImage getCurrentImage() {

        return (this.images.get(this.imageIndex));


    }
    public void nextImage() {
        this.imageIndex = (this.imageIndex + 1) % this.images.size();
    }

    protected List<PImage> getImages() {
        return images;
    }

    protected int getImageIndex() {
        return imageIndex;
    }
}
