package com.example.tp_game;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import com.example.tp_game.Position;

public class Maze {
    private int size;
    private Random random;
    private int[][] array;
    private boolean[][] visitted;
    private boolean[][] visitted_way;
    private int paths = 0;
    private int steps = 0;

    public int getSize() {
        return size; // 미로 배열의 외곽 벽을 제외한 크기 반환
    }

    public Maze(int size) {
        if(size % 2 == 0) size++;
        this.size = size;
        random = new Random();
        array = new int[size + 2][size + 2];
        visitted = new boolean[size + 2][size + 2];
        visitted_way = new boolean[size + 2][size + 2];

        generate();
    }
    public Maze(int[][] array1){
        this.array= array1;
        this.size = (array1.length-2);


    }

    public int[][] getArray() {
        return this.array;
    }

    private boolean canUp(Position position) {
        return position.getX() - 2 > 0;
    }

    private boolean canRight(Position position) {
        return position.getY() + 2 <= size;
    }

    private boolean canDown(Position position) {
        return position.getX() + 2 <= size;
    }

    private boolean canLeft(Position position) {
        return position.getY() - 2 > 0;
    }

    private Position randNext(Position current) {
        ArrayList<Integer> nexts = new ArrayList<Integer>();
        if (canUp(current))
            nexts.add(0);
        if (canRight(current))
            nexts.add(1);
        if (canDown(current))
            nexts.add(2);
        if (canLeft(current))
            nexts.add(3);

        int value = nexts.get(random.nextInt(nexts.size()));
        switch (value) {
            case 0: {
                return new Position(current.getX() - 2, current.getY());
            }
            case 1: {
                return new Position(current.getX(), current.getY() + 2);
            }
            case 2: {
                return new Position(current.getX() + 2, current.getY());
            }
            case 3: {
                return new Position(current.getX(), current.getY() - 2);
            }
            default:
                throw new IllegalArgumentException("Unexpected value: " + value);
        }
    }

    private void creatPath(Position current, Position next) {
        int x = (current.getX() + next.getX()) / 2;
        int y = (current.getY() + next.getY()) / 2;
        array[x][y] = 0;
    }

    public boolean canMoveUp(Position position) {
        return position.getX() - 1 > 0 && array[position.getX() - 1][position.getY()] == 0;
    }

    public boolean canMoveRight(Position position) {
        return position.getY() + 1 <= size + 1 && array[position.getX()][position.getY() + 1] == 0;
    }

    public boolean canMoveDown(Position position) {
        return position.getX() + 1 <= size && array[position.getX() + 1][position.getY()] == 0;
    }

    public boolean canMoveLeft(Position position) {
        return position.getY() - 1 > 0 && array[position.getX()][position.getY() - 1] == 0;
    }

    public void generate() {
        // First settup
        for (int i = 0; i < size + 2; i++) {
            array[0][i] = 1;
            array[size + 1][i] = 1;
            array[i][0] = 1;
            array[i][size + 1] = 1;
        }

        for (int i = 1; i <= size; i++) {
            for (int j = 1; j <= size; j++) {
                if (i % 2 == 1 && j % 2 == 1) {
                    array[i][j] = 0;
                    visitted[i][j] = false;
                    paths++;
                    visitted_way[i][j] = false;
                } else {
                    array[i][j] = 1;
                }
            }
        }

        array[1][0] = 0;
        array[size][size + 1] = 0;

        // Draw matrix
        int x_current = 1, y_current = 1;
        Position current = new Position(x_current, y_current);
        int visitedCell = 1;
        visitted[x_current][y_current] = true;
        while (visitedCell < paths) {
            steps++;
            Position next = randNext(current);
            int x_next = next.getX();
            int y_next = next.getY();

            if (visitted[x_next][y_next] == false) {
                visitted[x_next][y_next] = true;
                visitedCell++;
                creatPath(current, next);
            }

            // Update current position
            current.setX(next.getX());
            current.setY(next.getY());
        }
    }

    public void printVisitted() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (visitted[i][j] == true) {
                    System.out.print(1 + " ");
                } else {
                    System.out.print(0 + " ");
                }
            }
            System.out.println();
        }
    }

    public void print() {
        for (int i = 0; i < size + 2; i++) {
            for (int j = 0; j < size + 2; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
    }

    private boolean canMove(Position current, Position next) {
        ArrayList<Position> nexts = new ArrayList<Position>();
        if (canMoveUp(current))
            nexts.add(new Position(current.getX() - 1, current.getY()));
        if (canMoveRight(current))
            nexts.add(new Position(current.getX(), current.getY() + 1));
        if (canMoveDown(current))
            nexts.add(new Position(current.getX() + 1, current.getY()));
        if (canMoveLeft(current))
            nexts.add(new Position(current.getX(), current.getY() - 1));
        return nexts.contains(next);
    }

    private void resetVisittedWay() {
        for (int i = 0; i < size + 2; i++) {
            for (int j = 0; j < size + 2; j++) {
                visitted_way[i][j] = false;

            }
        }
    }
    private void printVisittedWay() {

        for (int i = 0; i < size + 2; i++) {
            for (int j = 0; j < size + 2; j++) {
                System.out.println("start");
                System.out.println(i);
                System.out.println(j);
                System.out.println(visitted_way[i][j]);

            }
        }
        System.out.println(size);
        System.out.println("end");
    }

    public Stack<Position> getWay(Position start, Position end) {
        Stack<Position> visitted_pos = new Stack<Position>();
        Stack<Position> valid_pos = new Stack<Position>();

        Position current = start;
        visitted_way[1][0] = true;
        visitted_pos.push(new Position(start.getX(), start.getY()));
        while (!current.equals(end)) {
            System.out.println(valid_pos);
            int x = current.getX();
            int y = current.getY();
            if (canMoveUp(current) && visitted_way[x - 1][y] == false)
                valid_pos.push(new Position(x - 1, y));
            if (canMoveLeft(current) && visitted_way[x][y - 1] == false)
                valid_pos.push(new Position(x, y - 1));
            if (canMoveRight(current) && visitted_way[x][y + 1] == false){
                valid_pos.push(new Position(x, y + 1));
            }
            if (canMoveDown(current) && visitted_way[x + 1][y] == false)
                valid_pos.push(new Position(x + 1, y));
            current = valid_pos.pop();
            visitted_pos.push(new Position(current.getX(), current.getY()));
            x = current.getX();
            y = current.getY();

            visitted_way[x][y] = true;
        }
        Stack<Position> way = new Stack<Position>();
        while (!visitted_pos.empty()) {
            way.push(visitted_pos.pop());
        }
        resetVisittedWay();
        return way;
    }

    //BackTracking
    public Stack<Position> getDirectWay(Position start, Position end) {
        System.out.println("sdfasdfasdf");
        printVisittedWay();
        resetVisittedWay();
        Stack<Position> visitted_pos = getWay(start, end);
        Stack<Position> revrse_way = new Stack<Position>();
        while (!visitted_pos.empty()) {
            revrse_way.push(visitted_pos.pop());
        }
        visitted_pos.push(new Position(end.getX(), end.getY()));
        revrse_way.pop();
        Position current = end;
        while (!revrse_way.empty()) {
            current = revrse_way.pop();
            Position next = visitted_pos.peek();

            if (canMove(current, next)) {
                visitted_pos.push(new Position(current.getX(), current.getY()));
            }
        }
        return visitted_pos;
    }

    public static void main(String[] args) {
        int size = 11;
        Position start = new Position(1, 0);
        Position end = new Position(size, size + 1);
        Maze maze = new Maze(size);

        maze.print();

        maze = new Maze(size);
        System.out.println("-----------------");
        maze.print();
    }
}