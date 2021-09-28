package NewLands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class PathFinder {
    private ArrayList<Point> open;
    private ArrayList<Point> closed;
    private HashMap<Point, Point> parents;
    private HashMap<Point, Integer> totalCost;

    public PathFinder() {
        this.open = new ArrayList<Point>();
        this.closed = new ArrayList<Point>();
        this.parents = new HashMap<Point, Point>();
        this.totalCost = new HashMap<Point, Integer>();
    }

    private int heuristicCost(Point from, Point to) {
        return Math.max(Math.abs(from.x - to.x), Math.abs(from.y - to.y));
    }

    private int costToGetTo(Point from) {
        return parents.get(from) == null ? 0 : (1 + costToGetTo(parents.get(from)));
    }

    private int totalCost(Point from, Point to) {
        if (totalCost.containsKey(from))
            return totalCost.get(from);

        int cost = costToGetTo(from) + heuristicCost(from, to);
        totalCost.put(from, cost);
        return cost;
    }

    private void reParent(Point child, Point parent) {
        parents.put(child, parent);
        totalCost.remove(child);
    }

    public ArrayList<Point> findPath(Creature creature, Point start, Point end, int maxTries) {
        open.clear();
        closed.clear();
        parents.clear();
        totalCost.clear();

        open.add(start);

        for (int tries = 0; tries < maxTries && open.size() > 0; tries++) {
            Point closest = getClosestPoint(end);

            open.remove(closest);
            closed.add(closest);

            if (closest.equals(end))
                return createPath(start, closest);
            else
                checkNeighbours(creature, end, closest);
        }
        return null;
    }

    private Point getClosestPoint(Point end) {
        Point closest = open.get(0);

        for (Point other : open) {
            if (totalCost(other, end) < totalCost(closest, end))
                closest = other;
        }

        return closest;
    }

    private void checkNeighbours(Creature creature, Point end, Point closest) {
        for (Point neighbour : closest.neighbours8()) {
            if (closed.contains(neighbour)
                || !creature.canEnter(neighbour.x, neighbour.y, creature.z)
                && !neighbour.equals(end))
                    continue;
            
                if (open.contains(neighbour))
                    reParentNeighbourIfNecessary(closest, neighbour);
                else
                    reParentNeighbour(closest, neighbour);
        }
    }

    private void reParentNeighbour(Point closest, Point neighbour) {
        reParent(neighbour, closest);
        open.add(neighbour);
    }

    private void reParentNeighbourIfNecessary(Point closest, Point neighbour) {
        Point originalParent = parents.get(neighbour);
        double currentCost = costToGetTo(neighbour);
        reParent(neighbour, closest);
        double reparentCost = costToGetTo(neighbour);

        if (reparentCost < currentCost)
            open.remove(neighbour);
        else
            reParent(neighbour, originalParent);
    }

    private ArrayList<Point> createPath(Point start, Point end) {
        ArrayList<Point> path = new ArrayList<Point>();

        while (!end.equals(start)) {
            path.add(end);
            end = parents.get(end);
        }

        Collections.reverse(path);
        return path;
    }
}
