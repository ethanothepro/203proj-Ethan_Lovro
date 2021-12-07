import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Optional;


import processing.core.*;
import processing.event.KeyEvent;

import javax.sound.midi.SysexMessage;

public final class VirtualWorld extends PApplet
{
    private static final int TIMER_ACTION_PERIOD = 100;

    private static final int VIEW_WIDTH = 640;
    private static final int VIEW_HEIGHT = 480;
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;
    private static final int WORLD_WIDTH_SCALE = 2;
    private static final int WORLD_HEIGHT_SCALE = 2;

    private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
    private static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
    private static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

    private static final String IMAGE_LIST_FILE_NAME = "imagelist";
    private static final String DEFAULT_IMAGE_NAME = "background_default";
    private static final int DEFAULT_IMAGE_COLOR = 0x808080;

    private static String LOAD_FILE_NAME = "world.sav";

    private static final String FAST_FLAG = "-fast";
    private static final String FASTER_FLAG = "-faster";
    private static final String FASTEST_FLAG = "-fastest";
    private static final double FAST_SCALE = 0.5;
    private static final double FASTER_SCALE = 0.25;
    private static final double FASTEST_SCALE = 0.10;

    private static double timeScale = 1.0;

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;

    private long nextTime;
    public int frameCount;

    private char currentKey = 'd';




    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        this.imageStore = new ImageStore(
                createImageColored(TILE_WIDTH, TILE_HEIGHT,
                                   DEFAULT_IMAGE_COLOR));
        this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
                                    createDefaultBackground(imageStore));
        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH,
                                  TILE_HEIGHT);
        this.scheduler = new EventScheduler(timeScale);

        loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
        loadWorld(world, LOAD_FILE_NAME, imageStore);

        scheduleActions(world, scheduler, imageStore);

        nextTime = System.currentTimeMillis() + TIMER_ACTION_PERIOD;



    }

    public void draw() {




        long time = System.currentTimeMillis();
        if (time >= nextTime) {
            this.scheduler.updateOnTime(time);
            nextTime = time + TIMER_ACTION_PERIOD;
        }

        view.drawViewport();
        frameCount+=1;







    }


    @Override
    public void keyPressed(KeyEvent event) {
        super.keyPressed(event);
        if (key == 'w'){
            currentKey = 'w';
        }
        if( key == 'd'){
            currentKey = 'd';
        }
        if(key == 'r'){
            currentKey = 'r';
        }
        if(key == 'l') {
            currentKey = 'l';
        }
        if(key == 's') {
            currentKey = 's';
        }
        if(key == 'c') {
            currentKey = 'c';
        }


    }

    // Just for debugging and for P5
    public void mousePressed() {
        Point pressed = mouseToPoint(mouseX, mouseY);
        List<Point> areaPoints = generateArea(pressed);
        System.out.println("Background: " + world.getBackgroundCell(pressed).getId());
        System.out.println("Object: " + world.getOccupancyCell(pressed));

        if(currentKey == 'd') {
            areaAction(areaPoints);

            Goomba goomba = new Goomba("Goomba", new Point(pressed.x, pressed.y - 1), imageStore.getImageList("goomba"), 1000, 6);
            Pipe pipe = new Pipe("Pipe", pressed, imageStore.getImageList("pipe"));

            try {
                //world.removeEntityAt(pressed);
                world.tryAddEntity(goomba);
                goomba.scheduleActions(scheduler, world, imageStore);
                world.tryAddEntity(pipe);


            } catch (IllegalArgumentException e) {
                System.out.println("Position Occupied");
            }


        }

        if(currentKey == 'w'){
            Obstacle water = new Obstacle("Water", pressed, imageStore.getImageList("obstacle"),Functions.getNumFromRange(500,100) );
            world.removeEntityAt(pressed);
            world.addEntity(water);
            water.scheduleActions(scheduler,world,imageStore);
        }

        if(currentKey == 'r'){
            Obstacle stone = new Obstacle("Stone", pressed, imageStore.getImageList("stone"),4 );
            world.removeEntityAt(pressed);
            world.addEntity(stone);
            stone.scheduleActions(scheduler, world, imageStore);
        }

        if(currentKey == 'l'){
            Obstacle lava = new Obstacle("Lava", pressed, imageStore.getImageList("lava"),Functions.getNumFromRange(500,100) );
            world.removeEntityAt(pressed);
            world.addEntity(lava);
            lava.scheduleActions(scheduler, world, imageStore);
        }
        if(currentKey == 's'){
            world.removeEntityAt(pressed);
            world.setBackground(pressed, new Background("sand", imageStore.getImageList("sand")));
        }

        if(currentKey == 'c'){
            world.removeEntityAt(pressed);
            Coin coin= new Coin("Coin",pressed,imageStore.getImageList("coin"),1000,Functions.getNumFromRange(100,6),false) ;
            world.addEntity(coin);
            coin.scheduleActions(scheduler, world, imageStore);
        }

    }

    //Code to determine what to do within area passed in
    private void areaAction(List<Point> areaPoints){
        for (Point point : areaPoints) {

            if ((world.getOccupancyCell(point) instanceof Obstacle)) {
                world.removeEntityAt(point);

                Obstacle lava = Factory.createObstacle("lava", point, 4, imageStore.getImageList("lava"));
                world.tryAddEntity(lava);
                lava.scheduleActions(scheduler,world,imageStore);
                //world.setBackgroundCell(point, new Background("stone", imageStore.getImageList("stone")));
            }

            if(world.getOccupancyCell(point) instanceof DudeNotFull || world.getOccupancyCell(point) instanceof DudeFull){
                world.removeEntityAt(point);

                Mario mario = new Mario("Mario", point,imageStore.getImageList("mario"),500,1);
                try{
                    world.tryAddEntity(mario);
                    mario.scheduleActions(scheduler,world,imageStore);

                }
                catch (Exception e){
                    System.out.println("An Error Occured");
                }

            }


            if (world.getOccupancyCell(point) instanceof Tree || world.getOccupancyCell(point) instanceof Stump){
                world.removeEntityAt(point);
                Coin coin= new Coin("Coin",point,imageStore.getImageList("coin"),1000,Functions.getNumFromRange(100,6),false) ;
                try{
                    world.tryAddEntity(coin);
                    coin.scheduleActions(scheduler,world, imageStore);
                    world.setBackground(point,new Background("sand", imageStore.getImageList("sand")));
                }
                catch (Exception e){
                    System.out.println("An Error Occured");
                }

            }





            //If there's no object at this point, then we check for each type of background cell and replace it

            String id = world.getBackgroundCell(point).getId();

            if (id.equals("grass")){
                Random random = new Random();
                int rand = random.nextInt(10);

                if (rand>0) {
                    world.setBackground(point, new Background("newgrass", imageStore.getImageList("sand")));
                }
                else{
                    Cactus cactus = new Cactus("cactus",point,imageStore.getImageList("cactus"));
                    try{
                        world.tryAddEntity(cactus);
                    }
                    catch(Exception e){
                        System.out.println("Oops");
                    }
                }

            }

            if (id.startsWith("dirt")|| world.getBackgroundCell(point).getId().equals("bridge")){
                world.setBackground(point, new Background("dirt", imageStore.getImageList("dirt")));
            }

            if (id.equals("flowers")){
                Random random = new Random();
                int rand = random.nextInt(10);
                if (rand>2) {
                    world.setBackgroundCell(point, new Background("sandstone", imageStore.getImageList("sandstone")));
                }
                else{
                    Cactus cactus = new Cactus("cactus",point,imageStore.getImageList("cactus"));
                    try{
                        world.tryAddEntity(cactus);
                    }
                    catch(Exception e){
                        System.out.println("Oops");
                    }
                }
            }



        }



    }

    private List<Point> generateArea(Point pressed){
        //Generate points in the area of expansion


        List<Point> areaPoints = new LinkedList<>();

        for (int i = 0; i<11; i++) {

            Point temp = new Point(pressed.x, pressed.y -5 + i);
            if (world.withinBounds(temp)) {
                areaPoints.add(temp);
            }

            for (int j = 0; j < 11; j++) {

                if (world.withinBounds(new Point(pressed.x + j -5, pressed.y + i -5))) {
                    areaPoints.add(new Point(pressed.x + j -5, pressed.y + i -5));
                }
            }
        }
        return areaPoints;

    }



    private Point mouseToPoint(int x, int y)
    {
        return view.getViewport().viewportToWorld( mouseX/TILE_WIDTH, mouseY/TILE_HEIGHT);
    }
    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP:
                    dy = -1;
                    break;
                case DOWN:
                    dy = 1;
                    break;
                case LEFT:
                    dx = -1;
                    break;
                case RIGHT:
                    dx = 1;
                    break;
            }
            view.shiftView(dx, dy);
        }
    }

    public static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME,
                              imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    public static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            img.pixels[i] = color;
        }
        img.updatePixels();
        return img;
    }

    static void loadImages(
            String filename, ImageStore imageStore, PApplet screen)
    {
        try {
            Scanner in = new Scanner(new File(filename));
            Functions.loadImages(in, imageStore, screen);
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void loadWorld(
            WorldModel world, String filename, ImageStore imageStore)
    {
        try {
            Scanner in = new Scanner(new File(filename));
            Functions.load(in, world, imageStore);
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void scheduleActions(
            WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        System.out.println("DSADA");
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Animators) {
                ((Animators) entity).scheduleActions(scheduler, world, imageStore);
            }
        }
    }

    public static void parseCommandLine(String[] args) {
        if (args.length > 1)
        {
            if (args[0].equals("file"))
            {

            }
        }
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG:
                    timeScale = Math.min(FAST_SCALE, timeScale);
                    break;
                case FASTER_FLAG:
                    timeScale = Math.min(FASTER_SCALE, timeScale);
                    break;
                case FASTEST_FLAG:
                    timeScale = Math.min(FASTEST_SCALE, timeScale);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        parseCommandLine(args);
        PApplet.main(VirtualWorld.class);
    }
}
