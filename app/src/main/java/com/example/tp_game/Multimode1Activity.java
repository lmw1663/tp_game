package com.example.tp_game;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Multimode1Activity extends AppCompatActivity {

    public static String roomNumber;
    private MazePanel mazePanel;
    private GuestMazePanel guestMazePanel;
    private PopupWindow levelMenuPopup;
    private PopupWindow gameMenuPopup;
    private String userId;
    private int level=11; // 레벨 변수 추가
    public static boolean Isautoclicked = true;
    public boolean getautoboolean() {
        return Isautoclicked;
    }
    private boolean isHost = false;


    public static void setautoboolean(boolean value) {
        Isautoclicked = value;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        userId = currentUser.getUid();
        roomNumber = getIntent().getStringExtra("roomNumber");
        DataFetchTask dataFetchTask = new DataFetchTask();

        dataFetchTask.fetchDataAndExecuteTask(new DataFetchCallback() {
            @Override
            public void onDataFetched(String data) {


                if (data.equals(userId)) {
                    isHost = true;
                }

                if(isHost) {
                    setContentView(R.layout.activity_gamemode1);
                    // MazePanel 초기화
                    initializeMazePanel();
                    int[][] mazeArray = mazePanel.getmaze();
                    List<List<Integer>> nestedList = new ArrayList<>();
                    for (int[] row : mazeArray) {
                        List<Integer> innerList = new ArrayList<>();
                        for (int element : row) {
                            innerList.add(element);
                        }
                        nestedList.add(innerList);
                    }


                    DatabaseReference mapreference = FirebaseDatabase.getInstance().getReference("rooms").child(roomNumber).child("maze");
                    mapreference.setValue(nestedList);

                    Button upButton = findViewById(R.id.upButton);
                    Button rightButton = findViewById(R.id.rightButton);
                    Button downButton = findViewById(R.id.downButton);
                    Button leftButton = findViewById(R.id.leftButton);

                    //Find Guest direction button

                    // Direction buttons의 OnClickListener 설정
                    upButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mazePanel.movePlayerUp(); // 위쪽으로 이동
                        }
                    });

                    rightButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mazePanel.movePlayerRight(); // 오른쪽으로 이동
                        }
                    });

                    downButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mazePanel.movePlayerDown(); // 아래쪽으로 이동
                        }
                    });

                    leftButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mazePanel.movePlayerLeft(); // 왼쪽으로 이동
                        }
                    });




                }else{
                    setContentView(R.layout.activity_gamemode1guest);
                    DatabaseReference guestreference = FirebaseDatabase.getInstance().getReference("rooms").child(roomNumber).child("guest");
                    guestreference.setValue(userId);

                    DatabaseReference mazeRef = FirebaseDatabase.getInstance().getReference("rooms").child(roomNumber).child("maze");
                    mazeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Parse the maze data from Firebase (nested list)
                                GenericTypeIndicator<List<List<Integer>>> typeIndicator = new GenericTypeIndicator<List<List<Integer>>>() {};
                                List<List<Integer>> nestedList = dataSnapshot.getValue(typeIndicator);

                                // Convert nested list to int[][] array (optional)
                                int[][] mazeArray = new int[nestedList.size()][]; // Initialize 2D array with row size
                                for (int i = 0; i < nestedList.size(); i++) {
                                    mazeArray[i] = new int[nestedList.get(i).size()]; // Initialize each row with column size
                                    for (int j = 0; j < nestedList.get(i).size(); j++) {
                                        mazeArray[i][j] = nestedList.get(i).get(j);
                                        System.out.print(mazeArray[i][j]);
                                        System.out.print(" ");// Assign each element
                                    }
                                    System.out.println();
                                }

                                initializeguestMazePanel(mazeArray); // Use mazeArray or nestedList depending on your needs.
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle database errors
                        }
                    });
                    //database get value of map
                    Button upButtonguest = findViewById(R.id.upButtonguest);
                    Button rightButtonguest = findViewById(R.id.rightButtonguest);
                    Button downButtonguest = findViewById(R.id.downButtonguest);
                    Button leftButtonguest = findViewById(R.id.leftButtonguest);



                    upButtonguest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            guestMazePanel.movePlayerUp(); // 위쪽으로 이동
                        }
                    });

                    rightButtonguest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            guestMazePanel.movePlayerRight(); // 오른쪽으로 이동
                        }
                    });

                    downButtonguest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            guestMazePanel.movePlayerDown(); // 아래쪽으로 이동
                        }
                    });

                    leftButtonguest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            guestMazePanel.movePlayerLeft(); // 왼쪽으로 이동
                        }
                    });
                }
            }
        });


        System.out.println(isHost);



       /* // Find menu items
        TextView gameMenu = findViewById(R.id.gameMenu);
        TextView levelMenu = findViewById(R.id.levelMenu);
        TextView computerMenu = findViewById(R.id.computerMenu);
        TextView helpMenu = findViewById(R.id.helpMenu);*/




        /*// Computer Menu와 Help Menu의 OnClickListener 설정
        computerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Isautoclicked ==true){
                    mazePanel.autoMove(); // autoMove 메소드 호출
                }
                Isautoclicked=false;
            }
        });

        helpMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle help menu click
            }
        });

*/



        /*// 레벨 메뉴를 표시하는 메서드
        levelMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLevelMenuPopup(v);
            }
        });

        // 게임 메뉴를 표시하는 메서드
        gameMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGameMenuPopup(v);
            }
        });*/
    }
    public class DataFetchTask {

        private FirebaseDatabase database;
        private DatabaseReference reference;

        public DataFetchTask() {
            database = FirebaseDatabase.getInstance();
            reference = database.getReference("rooms").child(roomNumber).child("host");
            System.out.println(reference);
        }

            public void fetchDataAndExecuteTask(DataFetchCallback callback) {
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String data = dataSnapshot.getValue().toString();
                        System.out.println(data);
                        callback.onDataFetched(data);
                        reference.removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            };

            reference.addValueEventListener(listener);
        }
    }
    // 레벨 메뉴를 표시하는 메서드
   /* private void showLevelMenuPopup(View anchorView) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_level_menu, null);

        // 팝업 윈도우 생성
        levelMenuPopup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        levelMenuPopup.setFocusable(true);
        levelMenuPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        levelMenuPopup.showAsDropDown(anchorView);

        // 각 레벨 버튼에 클릭 리스너 설정
        popupView.findViewById(R.id.level1Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazePanel.setMazeSize(11); // 레벨 1에 해당하는 미로 크기 설정
                levelMenuPopup.dismiss(); // 팝업 닫기
            }
        });

        popupView.findViewById(R.id.level2Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazePanel.setMazeSize(15); // 레벨 2에 해당하는 미로 크기 설정
                levelMenuPopup.dismiss(); // 팝업 닫기
            }
        });

        popupView.findViewById(R.id.level3Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazePanel.setMazeSize(18); // 레벨 3에 해당하는 미로 크기 설정
                levelMenuPopup.dismiss(); // 팝업 닫기
            }
        });

        popupView.findViewById(R.id.level4Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazePanel.setMazeSize(21); // 레벨 4에 해당하는 미로 크기 설정
                levelMenuPopup.dismiss(); // 팝업 닫기
            }
        });

        popupView.findViewById(R.id.level5Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazePanel.setMazeSize(26); // 레벨 5에 해당하는 미로 크기 설정
                levelMenuPopup.dismiss(); // 팝업 닫기
            }
        });

        // 추가적인 레벨 버튼들에 대한 설정
    }

    private void showGameMenuPopup(View anchorView) {
        View GamepopupView = LayoutInflater.from(this).inflate(R.layout.new_game_restart_menu, null);

        // 팝업 윈도우 생성
        gameMenuPopup = new PopupWindow(GamepopupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        gameMenuPopup.setFocusable(true);
        gameMenuPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        gameMenuPopup.showAsDropDown(anchorView);

        // 각 레벨 버튼에 클릭 리스너 설정
        GamepopupView.findViewById(R.id.newgameButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazePanel.setMazeSize(level); // 레벨 1에 해당하는 미로 크기 설정
                gameMenuPopup.dismiss(); // 팝업 닫기
            }
        });

        GamepopupView.findViewById(R.id.restartButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazePanel.restart();
                gameMenuPopup.dismiss(); // 팝업 닫기
            }
        });
        GamepopupView.findViewById(R.id.goHomeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Multimode1Activity.this,HomeActivity.class);
                startActivity(intent);
            }
        });
    }*/

    // 미로 패널 초기화 메서드
    private void initializeMazePanel() {
        FrameLayout mazePanelLayout = findViewById(R.id.mazePanel);
        mazePanel = new MazePanel(this);
        mazePanelLayout.addView(mazePanel);
    }
    private void initializeguestMazePanel(int[][] mazearray) {
        FrameLayout mazePanelLayout = findViewById(R.id.mazePanel);
        guestMazePanel = new GuestMazePanel(this ,mazearray);
        System.out.println(guestMazePanel.getmaze());
        mazePanelLayout.addView(guestMazePanel);
    }

    // ... (Implement your game logic methods here)
    private void deleteRoomData(String roomNumber) {
        // Access Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference roomsRef = database.getReference("rooms");
        DatabaseReference roomRef = roomsRef.child(roomNumber);

        // Delete room data
        roomRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("GameMode1Activity", "Room data deleted: " + roomNumber);
            } else {
                Log.e("GameMode1Activity", "Failed to delete room data: " + task.getException().getMessage());
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();

        // **Placeholders for user leave detection and room data deletion**
        // 1. Check if user left the game (implement logic based on your needs)
        boolean userLeftGame = false;

        // 2. If user left the game, retrieve room number and delete room data
        if (userLeftGame) {
            if (getIntent().hasExtra("roomNumber")) {
                roomNumber = getIntent().getStringExtra("roomNumber"); // Get the room number
                deleteRoomData(roomNumber);
            }
        }

    }
    public static String getRoomnumber(){
        return roomNumber;
    }
}
