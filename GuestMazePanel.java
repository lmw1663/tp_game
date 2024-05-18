package com.example.tp_game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Stack;

public class GuestMazePanel extends View {
    private Maze guestmaze;
    private int[][] mazeArray;
    private Paint paint;
    private int cellSize;
    private Position current;
    public String roomNumber;

    private Position start; // 시작 위치
    private Position end; // 목표 위치
    public Position hostPlayerPosition;


    private enum Direction {
        // 플레이어의 방향을 나타내는 변수 추가
        UP, DOWN, LEFT, RIGHT
    }

    private Direction playerDirection = Direction.RIGHT; // 초기 방향 설정

    private int delay = 100; // 이동 간격(ms)

    private boolean[][] visitted_way; // 방문한 위치 배열
    private Handler handler; // 핸들러 추가
    private boolean isAutoMoving = false; // autoMove() 실행 여부를 추적하는 플래그


    public GuestMazePanel(Context context) {
        super(context);
        init();
    }

    public GuestMazePanel(Context context, int[][] mazeArray) {
        super(context);
        this.mazeArray = mazeArray;
        init(mazeArray);
    }

    private void init() {
        guestmaze = new Maze(mazeArray); // 11은 미로 크기, 필요에 따라 조정 가능
        mazeArray = guestmaze.getArray();
        roomNumber = Multimode1Activity.getRoomnumber();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        current = new Position(1, 0); // 시작 위치 설정

        // 시작 위치와 목표 위치 초기화
        start = new Position(1, 0); // 시작 위치
        end = new Position(mazeArray.length - 2, mazeArray.length - 1); // 목표 위치 설정
        hostPlayerPosition = new Position(1,0);
        // 방문한 위치 배열 초기화
        visitted_way = new boolean[mazeArray.length][mazeArray.length];

        handler = new Handler(); // 핸들러 초기화
    }

    private void init(int[][] Array) {
        guestmaze = new Maze(Array); // 11은 미로 크기, 필요에 따라 조정 가능
        mazeArray = guestmaze.getArray();
        roomNumber = Multimode1Activity.getRoomnumber();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        current = new Position(1, 0); // 시작 위치 설정

        // 시작 위치와 목표 위치 초기화
        start = new Position(1, 0); // 시작 위치
        end = new Position(mazeArray.length - 2, mazeArray.length - 1); // 목표 위치 설정
        hostPlayerPosition = new Position(1,0);
        // 방문한 위치 배열 초기화
        visitted_way = new boolean[mazeArray.length][mazeArray.length];

        handler = new Handler(); // 핸들러 초기화
        System.out.println(this.getmaze());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMaze(canvas);
        drawPlayer(canvas);
    }

    public void restart() {
        current = new Position(1, 0);
        invalidate();
    }

    private void drawMaze(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        cellSize = Math.min(width, height) / (mazeArray.length - 2); // 미로의 크기에 맞게 셀 크기 조정
        cellSize *= 0.9;

        for (int i = 0; i < mazeArray.length; i++) {
            for (int j = 0; j < mazeArray[0].length; j++) {
                if (mazeArray[i][j] == 0) {
                    paint.setColor(Color.WHITE); // 길을 나타내는 색상
                } else if (mazeArray[i][j] == 2) {
                    paint.setColor(Color.YELLOW); // 최단 경로를 나타내는 색상 (초록색)
                } else {
                    paint.setColor(Color.BLACK); // 벽을 나타내는 색상
                }
                canvas.drawRect(j * cellSize, i * cellSize, (j + 1) * cellSize, (i + 1) * cellSize, paint);
            }
        }
    }
    public int[][] getmaze(){
        return this.guestmaze.getArray();
    }
    // drawPlayer 메서드에서 이미지 그리기 전에 방향에 따라 이미지 선택하는 코드 추가
    private void drawPlayer(Canvas canvas) {
        // Santa 이미지를 가져옴
        Bitmap hostBitmap;


/*        // 플레이어 방향에 따라 적절한 이미지 선택
        switch (hostPlayerPosition.getDirection()) {
            case UP:
                hostBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.santa_up);
                break;
            case DOWN:
                hostBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.santa_down);
                break;
            case LEFT:
                hostBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.santa_left);
                break;
            case RIGHT:
                hostBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.santa_right);
                break;
            default:
                hostBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.santa_right); // 기본값은 오른쪽
        }*/
        hostBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.santa_right);
        // 이미지 크기 조정
        int width = (int) (cellSize * 0.8f);  // 예시로 크기 조정
        int height = (int) (cellSize * 0.8f);
        hostBitmap = Bitmap.createScaledBitmap(hostBitmap, width, height, true);

        // 이미지가 정중앙에 위치하도록 좌표 계산
        paint.setStyle(Paint.Style.FILL); // Set fill style for solid circle
        paint.setColor(Color.BLUE); // Set color for the circle

        loadHostPlayerPosition();
        if (hostPlayerPosition != null) {
            // Calculate coordinates for centered drawing
            float left = (hostPlayerPosition.getX() + 0.5f) * cellSize - hostBitmap.getWidth() / 2f;
            float top = (hostPlayerPosition.getY() + 0.5f) * cellSize - hostBitmap.getHeight() / 2f;
            canvas.drawBitmap(hostBitmap, left, top, paint);

        }
        canvas.drawCircle((current.getY() + 0.5f) * cellSize,(current.getX() + 0.5f) * cellSize,cellSize*0.2f,paint);

        invalidate();
    }
    private void loadHostPlayerPosition(){
        DatabaseReference mazeRef = FirebaseDatabase.getInstance().getReference("rooms").child(Multimode1Activity.getRoomnumber()).child("hostPosition");
        mazeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Parse the maze data from Firebase (JSON string, 2D array, etc.)
                    String hostPosition = dataSnapshot.getValue(String.class);
                    String[] splitStrings = hostPosition.split(" ");

                    int number1 = Integer.parseInt(splitStrings[0]);
                    int number2 = Integer.parseInt(splitStrings[1]);
                    System.out.println(current.getX());
                    System.out.println(current.getY());
                    System.out.println(number1);
                    System.out.println(number2);
                    hostPlayerPosition.setX(number2);
                    hostPlayerPosition.setY(number1);
                    // Check if position is available

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors
            }
        });
    }
    public void setMazeSize(int size) {
        SingleActivity.setautoboolean(true);
        guestmaze = new Maze(size);
        mazeArray = guestmaze.getArray();
        current = new Position(1, 0); // 시작 위치 초기화
        start = current; // 시작 위치 설정
        end = new Position(size , size+1 ); // 목표 위치 설정
        invalidate(); // 뷰를 다시 그려서 변경된 미로를 표시
    }

    private boolean passed() {
        return current.equals(end);
    }

    private void move(Position current) {
        invalidate(); //repaint 기능


        if (passed()) {
            Toast.makeText(getContext(), "Congratulations! You have cleared the maze!", Toast.LENGTH_SHORT).show();
            // Refresh the maze after a 3-second delay
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setMazeSize(guestmaze.getSize());
                    invalidate();
                }
            }, 3000);
        }

    }

    // up 버튼 눌렀을 때 호출되는 메서드 수정
    public void movePlayerUp() {
        if (guestmaze.canMoveUp(current)) {
            current.setX(current.getX() - 1);
            move(current);
            String GuestPosition = String.valueOf(current.getX())+" "+String.valueOf(current.getY());
            DatabaseReference posreference = FirebaseDatabase.getInstance().getReference("rooms").child(roomNumber).child("guestPosition");
            posreference.setValue(GuestPosition);
        }
    }

    // down 버튼 눌렀을 때 호출되는 메서드 수정
    public void movePlayerDown() {
        if (guestmaze.canMoveDown(current)) {
            current.setX(current.getX() + 1);
            move(current);
            String GuestPosition = String.valueOf(current.getX())+" "+String.valueOf(current.getY());
            DatabaseReference posreference = FirebaseDatabase.getInstance().getReference("rooms").child(roomNumber).child("guestPosition");
            posreference.setValue(GuestPosition);
        }
    }

    // left 버튼 눌렀을 때 호출되는 메서드 수정
    public void movePlayerLeft() {
        if (guestmaze.canMoveLeft(current)) {
            current.setY(current.getY() - 1);
            move(current);
            String GuestPosition = String.valueOf(current.getX())+" "+String.valueOf(current.getY());
            DatabaseReference posreference = FirebaseDatabase.getInstance().getReference("rooms").child(roomNumber).child("guestPosition");
            posreference.setValue(GuestPosition);
        }
    }

    // right 버튼 눌렀을 때 호출되는 메서드 수정
    public void movePlayerRight() {
        if (guestmaze.canMoveRight(current)) {
            current.setY(current.getY() + 1);
            move(current);
            String GuestPosition = String.valueOf(current.getX())+" "+String.valueOf(current.getY());
            DatabaseReference posreference = FirebaseDatabase.getInstance().getReference("rooms").child(roomNumber).child("guestPosition");
            posreference.setValue(GuestPosition);
        }
    }

    public void autoMove() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println();
                Stack<Position> way = guestmaze.getDirectWay(start, end);
                Paint paint = new Paint();
                paint.setColor(Color.YELLOW);
                while (!way.empty()) {
                    Position next = way.pop();
                    int x = next.getX();
                    int y = next.getY();
                    mazeArray[x][y] = 2; // 최단 경로로 표시
                    postInvalidate(); // UI 스레드에서 invalidate() 호출
                    try {
                        Thread.sleep(delay); // 지연 시간 적용
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public Position getGuestPlayerPosition(){
        Position position = new Position((int)this.getX(),(int)this.getY());
        return position;

    }
}