import javax.swing.*;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy
        implements PathingStrategy
{



    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        List<Point> path = new LinkedList<Point>();
        PriorityQueue<Node> openList = new PriorityQueue<>();
        HashMap<Point, Node> closedList = new HashMap<>();

        Predicate<Point> inClosedList= p -> !closedList.containsKey(p);

        //Add starting point to open list as a Node
        Node cur = new Node(null, start, computeHeuristic(start, end), 0);
        openList.add(cur);




        while (!withinReach.test(cur.loc, end)) {

            //Get a list of neighbors points that are passable and not in closed list
            List<Point> neighbors = potentialNeighbors.apply(cur.loc).filter(canPassThrough).filter(inClosedList).collect(Collectors.toList());
            for (Point neighbor : neighbors) {
                //if (closedList.containsKey(neighbor)){break;}

                Node temp = new Node(cur, neighbor, computeHeuristic(neighbor, end), cur.g + 1);
                for (Node n : openList) {
                    //If there is a node that has a location equal to adjacent, replace g value if better
                    if (n.loc == neighbor) {
                        //Only add if g value less than previous g value
                        if (temp.g < n.g) {
                            openList.add(temp);
                        }
                    }
                }

                //If not in list add to list
                openList.add(temp);
            }

            closedList.put(cur.loc, cur);

            if (openList.isEmpty()){
                return path;
            }

            openList.remove(cur);
            cur = openList.poll();


            if (cur == null )
            {
                path.clear();
                return path;
            }



            //if (openList.isEmpty()){return path;}

        }

        //Build path from node
        while(cur.prev!=null){
            path.add(0,cur.loc);
            cur = cur.prev;
        }


        return path;
    }

    private int computeHeuristic(Point a, Point b){
        return (int) Math.sqrt((Math.pow(b.x - a.x,2)) + (Math.pow(b.y - a.y,2)));
    }

    private class Node implements Comparable<Node>{

        private Node prev;
        private Point loc;
        private int f;
        private int h;
        private int g;

        private Node(Node prev, Point loc, int h, int g){
            this.prev =  prev;
            this.loc = loc;
            this.f = h +g;
            this.h = h;
            this.g = g;
        }

        //COMPARES NODES BY LEAST F VALUE
        @Override
        public int compareTo(Node n) {
            Integer a = this.f;
            Integer b = n.f;

            return a.compareTo(b);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(loc, node.loc);
        }

        @Override
        public int hashCode() {
            return Objects.hash(loc);
        }
    }
}
