package com.codinghub.bluetoothtapper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.tapwithus.sdk.TapListener;
import com.tapwithus.sdk.TapSdk;
import com.tapwithus.sdk.TapSdkFactory;
import com.tapwithus.sdk.airmouse.AirMousePacket;
import com.tapwithus.sdk.mouse.MousePacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextToSpeech textToSpeech;

    private static final int REQUEST_ENABLE_BT = 100;
    BluetoothAdapter bluetoothAdapter;
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private Handler handler; // handler that gets info from Bluetooth service
    TapSdk sdk;
    private final boolean startWithControllerMode = false;

    ArrayList<String> letters = new ArrayList<>(Arrays.asList("A", "B", "C", "Rat", "Cat", "Bat"));
    ArrayList<String> letters_low = new ArrayList<>(Arrays.asList("B", "C", "E", "G", "H", "J", "L", "M", "O", "Y"));
    ArrayList<String> letters_medium = new ArrayList<>(Arrays.asList("RAT", "CAT", "BAT", "HAT", "MAT", "HII"));
    ArrayList<String> letters_high = new ArrayList<>(Arrays.asList("HELLO", "BUDDY", "C", "Rat", "Cat", "Bat"));
    ArrayList<Integer> isCorrect = new ArrayList<>();

    ArrayList<Long> timeLetters = new ArrayList<>();
    ArrayList<Integer> validData = new ArrayList<>(Arrays.asList(2, 6, 14, 30, 31, 17));
    Long startingTime, finishTime;
    Boolean onStart = false, onFirstDevice = true;
    int deviceData1 = 0, deviceData2 = 0, index = 0, level = 1, count = 0;

    TextView deviceName1, deviceName2, textViewDeviceData1, textViewDeviceData2;
    CardView device1finger1, device1finger2, device1finger3, device1finger4, device1finger5;
    CardView device2finger1, device2finger2, device2finger3, device2finger4, device2finger5;
    CardView startButton, inputOutputButton, stopButton, pausePlayButton;
    TextView textViewInput, timePrevious;
    String deviceFirst, logText = "";
    RecyclerView recyclerView;
    GridView gridView;

    RecyclerViewAdapterCustom adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });
        letters.clear();
        letters.addAll(letters_low);
        device1finger1 = findViewById(R.id.device1finger1);
        device1finger2 = findViewById(R.id.device1finger2);
        device1finger3 = findViewById(R.id.device1finger3);
        device1finger4 = findViewById(R.id.device1finger4);
        device1finger5 = findViewById(R.id.device1finger5);
        device2finger1 = findViewById(R.id.device2finger1);
        device2finger2 = findViewById(R.id.device2finger2);
        device2finger3 = findViewById(R.id.device2finger3);
        device2finger4 = findViewById(R.id.device2finger4);
        device2finger5 = findViewById(R.id.device2finger5);
        deviceName1 = findViewById(R.id.deviceName);
        deviceName2 = findViewById(R.id.device2Name);
        textViewDeviceData1 = findViewById(R.id.deviceData);
        textViewDeviceData2 = findViewById(R.id.device2Data);
        startButton = findViewById(R.id.startButton);
        final TextView startButtonTextView = findViewById(R.id.startButtonTextView);
        inputOutputButton = findViewById(R.id.cardViewInputOutput);
        recyclerView = findViewById(R.id.recyclerView);
        gridView = findViewById(R.id.gridView);
        stopButton = findViewById(R.id.stopButton);
        pausePlayButton = findViewById(R.id.pausePlayButton);
        timePrevious = findViewById(R.id.timePrevious);
        textViewInput = findViewById(R.id.textViewInputLetter);
        final Button lowButton = findViewById(R.id.lowButton);
        final Button mediumButton = findViewById(R.id.mediumButton);
        final Button highButton = findViewById(R.id.highButton);

        log(" on create called ");
        sdk = TapSdkFactory.getDefault(MainActivity.this);
        sdk.enableDebug();
        log("Debug started ");
        sdk.registerTapListener(tapListener);
        // recyclerview
        adapter = new RecyclerViewAdapterCustom(letters);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // levels
        lowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                level = 1;
                lowButton.setBackgroundResource(R.drawable.greenrectangle);
                mediumButton.setBackgroundResource(R.drawable.rectangle);
                highButton.setBackgroundResource(R.drawable.rectangle);
                letters.clear();
                letters.addAll(letters_low);
                adapter.notifyDataSetChanged();

            }
        });
        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                level = 2;
                lowButton.setBackgroundResource(R.drawable.rectangle);
                mediumButton.setBackgroundResource(R.drawable.greenrectangle);
                highButton.setBackgroundResource(R.drawable.rectangle);
                letters.clear();
                letters.addAll(letters_medium);
                adapter.notifyDataSetChanged();

            }
        });
        highButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                level = 3;
                lowButton.setBackgroundResource(R.drawable.rectangle);
                mediumButton.setBackgroundResource(R.drawable.rectangle);
                highButton.setBackgroundResource(R.drawable.greenrectangle);
                letters.clear();
                letters.addAll(letters_high);
                adapter.notifyDataSetChanged();
            }
        });
        // start | play | stop buttons
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 0;
                String speechText = "Type " + letters.get(index);
                textToSpeech.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, null);
                startButtonTextView.setText("Start Typing ...");
                if (!onStart) {
                    stopButton.setVisibility(View.VISIBLE);
                    pausePlayButton.setVisibility(View.VISIBLE);
                }
                startingTime = System.currentTimeMillis();
                onStart = true;
                setText(textViewInput, "");
                // logTextView.setText(logText);
//                adapter.notifyDataSetChanged();
//                recyclerView.smoothScrollToPosition(index);
                // implementing grid
                isCorrect.add(2);
                GridViewBaseAdapter gridViewBaseAdapter = new GridViewBaseAdapter(MainActivity.this, getGridLetters(), isCorrect);
                gridView.setAdapter(gridViewBaseAdapter);

            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startButtonTextView.setText("Click To begin");
                TextView textView = findViewById(R.id.pausePlayButtonTextView);
                textView.setText("Pause Typing");
                startingTime = System.currentTimeMillis();
                onStart = false;
                // logTextView.setText(logText);
                timeLetters.clear();
                isCorrect.clear();
                count = 0;
                index = 0;
                updateGrid();

            }
        });
        pausePlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = findViewById(R.id.pausePlayButtonTextView);

                startingTime = System.currentTimeMillis();
                if (onStart) {
                    textView.setText("Continue..");
                } else {
                    textView.setText("Pause typing");
                }
                onStart = !onStart;
            }
        });

    }

    public List<String> getGridLetters() {
        if (level == 1) {
            return letters_low.subList(0, index + 1);
        } else if (level == 2) {
            return letters_medium.subList(0, index + 1);
        } else {
            return letters_high.subList(0, index + 1);
        }
    }


    private final TapListener tapListener = new TapListener() {
        @Override
        public void onBluetoothTurnedOn() {
            log("Bluetooth turned ON");
        }

        @Override
        public void onBluetoothTurnedOff() {
            log("Bluetooth turned OFF");
        }

        @Override
        public void onTapStartConnecting(@NonNull String tapIdentifier) {
            log("Tap started connecting - " + tapIdentifier);
        }

        @Override
        public void onTapConnected(@NonNull String tapIdentifier) {
            log("TAP connected " + tapIdentifier);
        }

        @Override
        public void onTapDisconnected(@NonNull String tapIdentifier) {
            log("TAP disconnected " + tapIdentifier);
        }

        @Override
        public void onTapResumed(@NonNull String tapIdentifier) {

        }

        @Override
        public void onTapChanged(@NonNull String tapIdentifier) {

        }

        @Override
        public void onControllerModeStarted(@NonNull String tapIdentifier) {

        }

        @Override
        public void onTextModeStarted(@NonNull String tapIdentifier) {

        }

        @Override
        public void onTapInputReceived(@NonNull String tapIdentifier, int data) {
            log("TapInputReceived - " + tapIdentifier + ", " + data + ", repeatData = " + data);
            boolean isNot17one = true;
            if (tapIdentifier.equals("F0:6D:D5:FA:AE:83") && data == 17) {
                isNot17one = false;
            }

            if (validData.contains(data) && isNot17one) {
                if (tapIdentifier.equals("F0:6D:D5:FA:AE:83")) {
                    if (onStart && onFirstDevice) {
                        deviceFirst = tapIdentifier;
                        deviceData1 = data;
                        onFirstDevice = false;
                    }
                    setText(deviceName1, tapIdentifier);
                    setText(textViewDeviceData1, data + "");

                    performCardClick1(data);
                }

                if (tapIdentifier.equals("ED:34:CC:93:2F:8E")) {
                    if (onStart && onFirstDevice) {
                        deviceFirst = tapIdentifier;
                        deviceData2 = data;
                        onFirstDevice = false;
                    }
                    setText(deviceName2, tapIdentifier);
                    setText(textViewDeviceData2, data + "");

                    performCardClick2(data);
                }

                if (onStart && !onFirstDevice) {
                    if (!tapIdentifier.equals(deviceFirst)) {
                        if (deviceData1 == 0) {
                            deviceData1 = data;
                        } else {
                            deviceData2 = data;
                        }
                        String letter = getMatchedLetter(deviceData1, deviceData2);
//                        if (letter.equals(letters.get(index))) {
//                            textViewInput.setTextColor(getColor(R.color.green));
//                        } else {
//                            textViewInput.setTextColor(getColor(R.color.red));
//                        }
                        setText(textViewInput, letter);
                        onFirstDevice = true;
                        deviceData1 = 0;
                        deviceData2 = 0;
                        if (index >= letters.size()) {
                            return;
                        }
                        isCorrect.set(index, 0);
                        if (letter.equals(letters.get(index)) && level == 1) {

                            isCorrect.set(index, 1);

                        }
                        isCorrect.add(2);
                        logText = "";
                        finishTime = System.currentTimeMillis();
                        // logTextView.setText(logText);
                        timeLetters.add(finishTime - startingTime);
                        setText(timePrevious, timeLetters.get(timeLetters.size() - 1).toString());
                        startingTime = System.currentTimeMillis();
                        ++index;
                        String speechText = "Type " + letters.get(index);
                        textToSpeech.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, null);
//                        // for level 2
//                        logText += letter;
//                        isCorrect.set(index, 2);
//                        if (count == 0) {
//                            if (letter.equals(letters.get(index).substring(0, 1))) {
//                                ++count;
//                            } else {
//                                count = 0;
//                                isCorrect.set(index, 0);
//                                isCorrect.add(2);
//                                logText = "";
//                                finishTime = System.currentTimeMillis();
//                                // logTextView.setText(logText);
//                                timeLetters.add(finishTime - startingTime);
//                                setText(timePrevious, timeLetters.get(timeLetters.size() - 1).toString());
//                                startingTime = System.currentTimeMillis();
//                                ++index;
//                                String speechText = "Type " + letters.get(index);
//                                textToSpeech.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, null);
//                            }
//                        } else if (count == 1) {
//                            if (letter.equals(letters.get(index).substring(1, 2))) {
//                                ++count;
//                            } else {
//                                count = 0;
//                                isCorrect.set(index, 0);
//                                isCorrect.add(2);
//                                logText = "";
//                                finishTime = System.currentTimeMillis();
//                                // logTextView.setText(logText);
//                                timeLetters.add(finishTime - startingTime);
//                                setText(timePrevious, timeLetters.get(timeLetters.size() - 1).toString());
//                                startingTime = System.currentTimeMillis();
//                                ++index;
//                                String speechText = "Type " + letters.get(index);
//                                textToSpeech.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, null);
//                            }
//                        } else if (count == 2) {
//                            if (letter.equals(letters.get(index).substring(2, 3))) {
//                                ++count;
//                            } else {
//                                count = 0;
//                                isCorrect.set(index, 0);
//                                isCorrect.add(2);
//                                logText = "";
//                                finishTime = System.currentTimeMillis();
//                                // logTextView.setText(logText);
//                                timeLetters.add(finishTime - startingTime);
//                                setText(timePrevious, timeLetters.get(timeLetters.size() - 1).toString());
//                                startingTime = System.currentTimeMillis();
//                                ++index;
//                                String speechText = "Type " + letters.get(index);
//                                textToSpeech.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, null);
//                            }
//                        }else if (count == 3) {
//                            if (letter.equals(letters.get(index).substring(3, 4))) {
//                                ++count;
//                            } else {
//                                count = 0;
//                                isCorrect.set(index, 0);
//                                isCorrect.add(2);
//                                logText = "";
//                                finishTime = System.currentTimeMillis();
//                                // logTextView.setText(logText);
//                                timeLetters.add(finishTime - startingTime);
//                                setText(timePrevious, timeLetters.get(timeLetters.size() - 1).toString());
//                                startingTime = System.currentTimeMillis();
//                                ++index;
//                                String speechText = "Type " + letters.get(index);
//                                textToSpeech.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, null);
//                            }
//                        }else {
//                            if (letter.equals(letters.get(index).substring(4, 5))) {
//                                isCorrect.set(index, 1);
//                                finishTime = System.currentTimeMillis();
//                                // logTextView.setText(logText);
//                                timeLetters.add(finishTime - startingTime);
//                                setText(timePrevious, timeLetters.get(timeLetters.size() - 1).toString());
//                            } else {
//                                isCorrect.set(index, 0);
//                            }
//                            isCorrect.add(2);
//                            count = 0;
//                            ++index;
//                            startingTime = System.currentTimeMillis();
//                            logText = "";
//                            String speechText = "Type " + letters.get(index);
//                            textToSpeech.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, null);
//                        }
                        setText(textViewInput, logText);

                        // index changes

                        if (index >= letters.size()) {
                            stopButton.performClick();
                            stopButton.setPressed(true);
                            stopButton.setActivated(true);
                            stopButton.invalidate();
                            return;
                        }
                        onStart = true;
                        //  recyclerView.smoothScrollToPosition(index);
                        updateGrid();
//                        String speechText = "Type " + letters.get(index);
//                        textToSpeech.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            }

        }


        @Override
        public void onMouseInputReceived(@NonNull String tapIdentifier, @NonNull MousePacket data) {
            log(tapIdentifier + " mouse input received " + data.dx.getInt() + " " + data.dy.getInt() + " " + data.dt.getUnsignedLong() + " " + data.proximity.getInt());

        }

        @Override
        public void onAirMouseInputReceived(@NonNull String tapIdentifier, @NonNull AirMousePacket data) {
            log(tapIdentifier + " air mouse input received " + data.gesture.getInt());

        }

        @Override
        public void onError(@NonNull String tapIdentifier, int code, @NonNull String description) {
            log("Error - " + tapIdentifier + " - " + code + " - " + description);

        }
    };

    private void performCardClick2(int data) {
        int[] a = decToBinary(data);
        if (a[0] == 1) {
            setBackgroundColor(device2finger1, getColor(R.color.fingersClick));
        } else {
            setBackgroundColor(device2finger1, getColor(R.color.fingersNotClick));
        }
        if (a[1] == 1) {
            setBackgroundColor(device2finger2, getColor(R.color.fingersClick));
        } else {
            setBackgroundColor(device2finger2, getColor(R.color.fingersNotClick));
        }
        if (a[2] == 1) {
            setBackgroundColor(device2finger3, getColor(R.color.fingersClick));
        } else {
            setBackgroundColor(device2finger3, getColor(R.color.fingersNotClick));
        }
        if (a[3] == 1) {
            setBackgroundColor(device2finger4, getColor(R.color.fingersClick));
        } else {
            setBackgroundColor(device2finger4, getColor(R.color.fingersNotClick));
        }
        if (a[4] == 1) {
            setBackgroundColor(device2finger5, getColor(R.color.fingersClick));
        } else {
            setBackgroundColor(device2finger5, getColor(R.color.fingersNotClick));
        }
    }

    private void performCardClick1(int data) {
        int[] a = decToBinary(data);
        if (a[0] == 1) {
            setBackgroundColor(device1finger1, getColor(R.color.fingersClick));
        } else {
            setBackgroundColor(device1finger1, getColor(R.color.fingersNotClick));
        }
        if (a[1] == 1) {
            setBackgroundColor(device1finger2, getColor(R.color.fingersClick));
        } else {
            setBackgroundColor(device1finger2, getColor(R.color.fingersNotClick));
        }
        if (a[2] == 1) {
            setBackgroundColor(device1finger3, getColor(R.color.fingersClick));
        } else {
            setBackgroundColor(device1finger3, getColor(R.color.fingersNotClick));
        }
        if (a[3] == 1) {
            setBackgroundColor(device1finger4, getColor(R.color.fingersClick));
        } else {
            setBackgroundColor(device1finger4, getColor(R.color.fingersNotClick));
        }
        if (a[4] == 1) {
            setBackgroundColor(device1finger5, getColor(R.color.fingersClick));
        } else {
            setBackgroundColor(device1finger5, getColor(R.color.fingersNotClick));
        }
    }


    private void setBackgroundColor(final CardView text, final int value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setBackgroundColor(value);
            }
        });
    }

    private void updateGrid() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gridView.setAdapter(new GridViewBaseAdapter(getApplicationContext(), getGridLetters(), isCorrect));
                gridView.deferNotifyDataSetChanged();
            }
        });

    }

    private void setText(final TextView text, final String value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }

    private String getMatchedLetter(int deviceData1, int deviceData2) {
        if (deviceData2 == 2) {
            if (deviceData1 == 2) {
                return "A";
            } else if (deviceData1 == 6)
                return "B";
            else if (deviceData1 == 14)
                return "C";
            else if (deviceData1 == 30)
                return "D";
            else if (deviceData1 == 31)
                return "E";
        } else if (deviceData2 == 6) {
            if (deviceData1 == 2) {
                return "F";
            } else if (deviceData1 == 6)
                return "G";
            else if (deviceData1 == 14)
                return "H";
            else if (deviceData1 == 30)
                return "I";
            else if (deviceData1 == 31)
                return "J";

        } else if (deviceData2 == 14) {
            if (deviceData1 == 2) {
                return "K";
            } else if (deviceData1 == 6)
                return "L";
            else if (deviceData1 == 14)
                return "M";
            else if (deviceData1 == 30)
                return "N";
            else if (deviceData1 == 31)
                return "N";

        } else if (deviceData2 == 30) {
            if (deviceData1 == 2) {
                return "P";
            } else if (deviceData1 == 6)
                return "Q";
            else if (deviceData1 == 14)
                return "R";
            else if (deviceData1 == 30)
                return "S";
            else if (deviceData1 == 31)
                return "T";

        } else {
            if (deviceData1 == 2) {
                return "U";
            } else if (deviceData1 == 6)
                return "V";
            else if (deviceData1 == 14)
                return "W";
            else if (deviceData1 == 30)
                return "X";
            else if (deviceData1 == 31)
                return "Y";
            else if (deviceData1 == 17)
                return "Z";
        }
        return "A";
    }

    @Override
    protected void onResume() {
        super.onResume();
        log("on Resume ");
        sdk.resume();
    }

    @Override
    protected void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        log("onPause");
        sdk.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        log("onDestroy");
        sdk.unregisterTapListener(tapListener);
        super.onDestroy();
    }

    // function to convert decimal to binary
    static int[] decToBinary(int n) {
        // array to store binary number
        int[] binaryNum = new int[1000];

        // counter for binary array
        int i = 0;
        while (n > 0) {
            // storing remainder in binary array
            binaryNum[i] = n % 2;
            n = n / 2;
            i++;
        }
        return binaryNum;
    }

    void log(String message) {
        Log.d("debugging", message);
    }
}