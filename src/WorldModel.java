import processing.core.PImage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel
{
    private final int numRows;
    private final int numCols;
    private final Background background[][];
    private final Entity occupancy[][];
    private final Set<Entity> entities;

    public WorldModel(int numRows, int numCols, Background defaultBackground) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.background = new Background[numRows][numCols];
        this.occupancy = new Entity[numRows][numCols];
        this.entities = new HashSet<>();

        for (int row = 0; row < numRows; row++) {
            Arrays.fill(this.background[row], defaultBackground);
        }
    }

    public Set<Entity> getEntities() {
        return entities;
    }

    public int getNumRows(){return numRows;}

    public int getNumCols(){return numCols;}

    public boolean withinBounds(Point pos) {
        return pos.y >= 0 && pos.y < this.numRows && pos.x >= 0
                && pos.x < this.numCols;
    }


    public void addEntity(Entity entity) {
        if (this.withinBounds(entity.getPosition())) {
            setOccupancyCell(entity.getPosition(), entity);
            this.entities.add(entity);
        }
    }



    public void setBackgroundCell(Point pos, Background background)
    {
        this.background[pos.y][pos.x] = background;
    }

    public void setBackground(Point pos, Background background)
    {
        if (this.withinBounds(pos)) {
            this.setBackgroundCell(pos, background);
        }
    }

    public void tryAddEntity(Entity entity) {
        if (this.isOccupied(entity.getPosition())) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        this.addEntity(entity);
    }



    public boolean parseBackground(
            String[] properties, ImageStore imageStore)
    {
        if (properties.length == Functions.BGND_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.BGND_COL]),
                    Integer.parseInt(properties[Functions.BGND_ROW]));
            String id = properties[Functions.BGND_ID];
            this.setBackground(pt,
                    new Background(id, imageStore.getImageList(id)));
        }

        return properties.length == Functions.BGND_NUM_PROPERTIES;
    }

    public boolean parseSapling(
            String[] properties,ImageStore imageStore)
    {
        if (properties.length == Functions.SAPLING_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.SAPLING_COL]),
                    Integer.parseInt(properties[Functions.SAPLING_ROW]));
            String id = properties[Functions.SAPLING_ID];
            int health = Integer.parseInt(properties[Functions.SAPLING_HEALTH]);
            Sapling sapling = new Sapling(id, pt, imageStore.getImageList(Functions.SAPLING_KEY), Functions.SAPLING_ACTION_ANIMATION_PERIOD, Functions.SAPLING_ACTION_ANIMATION_PERIOD, health, Functions.SAPLING_HEALTH_LIMIT);
            this.tryAddEntity(sapling);
        }

        return properties.length == Functions.SAPLING_NUM_PROPERTIES;
    }
    public boolean parseDude(String[] properties, ImageStore imageStore)
    {
        if (properties.length == Functions.DUDE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.DUDE_COL]),
                    Integer.parseInt(properties[Functions.DUDE_ROW]));
            Entity entity = Factory.createDudeNotFull(properties[Functions.DUDE_ID],
                    pt,
                    Integer.parseInt(properties[Functions.DUDE_ACTION_PERIOD]),
                    Integer.parseInt(properties[Functions.DUDE_ANIMATION_PERIOD]),
                    Integer.parseInt(properties[Functions.DUDE_LIMIT]),
                    imageStore.getImageList(Functions.DUDE_KEY));
            this.tryAddEntity(entity);
        }

        return properties.length == Functions.DUDE_NUM_PROPERTIES;
    }


    public boolean parseFairy(
            String[] properties, ImageStore imageStore)
    {
        if (properties.length == Functions.FAIRY_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.FAIRY_COL]),
                    Integer.parseInt(properties[Functions.FAIRY_ROW]));
            Entity entity = Factory.createFairy(properties[Functions.FAIRY_ID],
                    pt,
                    500,
                    Integer.parseInt(properties[Functions.FAIRY_ANIMATION_PERIOD]),
                    imageStore.getImageList(Functions.FAIRY_KEY));
            this.tryAddEntity(entity);
        }

        return properties.length == Functions.FAIRY_NUM_PROPERTIES;
    }



    public boolean parseTree(
            String[] properties, ImageStore imageStore)
    {
        if (properties.length == Functions.TREE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.TREE_COL]),
                    Integer.parseInt(properties[Functions.TREE_ROW]));
            Entity entity = Factory.createTree(properties[Functions.TREE_ID],
                    pt,
                    Integer.parseInt(properties[Functions.TREE_ACTION_PERIOD]),
                    Integer.parseInt(properties[Functions.TREE_ANIMATION_PERIOD]),
                    Integer.parseInt(properties[Functions.TREE_HEALTH]),
                    imageStore.getImageList(Functions.TREE_KEY));
            this.tryAddEntity(entity);
        }

        return properties.length == Functions.TREE_NUM_PROPERTIES;
    }


    public boolean parseObstacle(
            String[] properties,  ImageStore imageStore)
    {
        if (properties.length == Functions.OBSTACLE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.OBSTACLE_COL]),
                    Integer.parseInt(properties[Functions.OBSTACLE_ROW]));
            Entity entity = Factory.createObstacle(properties[Functions.OBSTACLE_ID], pt,
                    Integer.parseInt(properties[Functions.OBSTACLE_ANIMATION_PERIOD]),
                    imageStore.getImageList(
                            Functions.OBSTACLE_KEY));
            this.tryAddEntity(entity);
        }

        return properties.length == Functions.OBSTACLE_NUM_PROPERTIES;
    }

    public boolean parseHouse(
            String[] properties, ImageStore imageStore)
    {
        if (properties.length == Functions.HOUSE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.HOUSE_COL]),
                    Integer.parseInt(properties[Functions.HOUSE_ROW]));
            House house = Factory.createHouse(properties[Functions.HOUSE_ID], pt,
                    imageStore.getImageList(
                            Functions.HOUSE_KEY));
            this.tryAddEntity(house);
        }

        return properties.length == Functions.HOUSE_NUM_PROPERTIES;
    }


    public boolean isOccupied(Point pos) {
        return this.withinBounds(pos) && getOccupancyCell(pos) != null;
    }

    public Background getBackgroundCell( Point pos) {
        return this.background[pos.y][pos.x];
    }

    public Optional<Entity> getOccupant(Point pos) {
        if (this.isOccupied(pos)) {
            return Optional.of(getOccupancyCell( pos));
        }
        else {
            return Optional.empty();
        }
    }

    public Entity getOccupancyCell(Point pos) {
        return this.occupancy[pos.y][pos.x];
    }

    public Optional<PImage> getBackgroundImage(Point pos)
    {
        if (this.withinBounds(pos)) {
            return Optional.of((getBackgroundCell(pos).getCurrentImage()));
        }
        else {
            return Optional.empty();
        }
    }

    public void setOccupancyCell(Point pos, Entity entity)
    {
        this.occupancy[pos.y][pos.x] = entity;
    }

    public void moveEntity(Entity entity, Point pos) {
        Point oldPos = entity.getPosition();
        if (this.withinBounds(pos) && !pos.equals(oldPos)) {
            this.setOccupancyCell(oldPos, null);
            removeEntityAt( pos);
            this.setOccupancyCell(pos, entity);
            entity.setPosition(pos);
        }
    }

    public void removeEntity(Entity entity) {
        removeEntityAt( entity.getPosition());
    }

    public void removeEntityAt(Point pos) {
        if (this.withinBounds(pos) && this.getOccupancyCell(pos) != null) {
            Entity entity = this.getOccupancyCell(pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.setPosition(new Point(-1, -1));
            this.entities.remove(entity);
            this.setOccupancyCell(pos, null);
        }
    }



}