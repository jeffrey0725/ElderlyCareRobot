package com.example.jeffreycheung.elderlycarerobot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements RecognitionListener, TextToSpeech.OnInitListener{

    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    public Handler handler;
    boolean listening = false; // check availability of voice
    public String message;
    public RequestQueue queue;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public TextToSpeech textToSpeech;
    HashMap<String, String> params;
    private String LOG_TAG = "Status";
    public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("reminder");

        textView = (TextView) findViewById(R.id.status);

        params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");

        handler = new Handler();

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "zh_hk");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        recognizerIntent.putExtra("android.speech.extra.DICTATION_MODE", true);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 6000);

        textToSpeech = new TextToSpeech(this, this);
        final Handler handler = new Handler();
        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                //textToSpeech.speak("Good afternoon madam", TextToSpeech.QUEUE_FLUSH, params);
                speech.cancel();

            }
        }, 1000);*/


        //speech.startListening(recognizerIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (listening) {
            Log.i(LOG_TAG, "onResume");
            textView.setText("onResume");
            speech.startListening(recognizerIntent);
        }*/
        Log.i(LOG_TAG, "onResume");
        textView.setText("onResume");
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onPause() {
        super.onPause();
        speech.cancel();
        Log.i(LOG_TAG, "onPause");
        textView.setText("onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        textToSpeech.shutdown();
        speech.cancel();
        if (speech != null) {
            speech.destroy();
            speech = null;
        }
        Log.i(LOG_TAG, "onDestroy");
        textView.setText("onDestory");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        textView.setText("onBeginningOfSpeech");

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        /*
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //1s = 1000ms
                //restart voice recognition after 0s
                speech.startListening(recognizerIntent);
                listening = false;
                Log.i(LOG_TAG, "onEndOfSpeech");
            }
        }, 5000);
        */
        Log.i(LOG_TAG, "onEndOfSpeech");
        textView.setText("onEndOfSpeech");
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.i(LOG_TAG, errorMessage);
        if (errorMessage.equals("No speech input")) {
            try {
                String error1Speak = "唔好意思，請你講多一次";
                //Toast.makeText(getApplicationContext(), error1Speak, Toast.LENGTH_SHORT).show();
                textToSpeech.speak(error1Speak, TextToSpeech.QUEUE_FLUSH, params);
                //textView.setText("No speech input");
                Thread.sleep(5000);
                speech.startListening(recognizerIntent);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (errorMessage.equals("No match")) {
            try {
                String error2Speak = "我聽得唔係好清楚，可唔可以再講多一次";
                //Toast.makeText(getApplicationContext(), error2Speak, Toast.LENGTH_SHORT).show();
                textToSpeech.speak(error2Speak, TextToSpeech.QUEUE_FLUSH, params);
                //textView.setText("No match");
                Thread.sleep(5000);
                speech.startListening(recognizerIntent);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //List of error message
    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    @Override
    public void onStop() {
        super.onStop();
        textToSpeech.shutdown();
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {

    }

    @Override
    public void onPartialResults(Bundle arg0) {

    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        listening = false;
        Log.i(LOG_TAG, "onReadyForSpeech");
        textView.setText("onReadyForSpeech");
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onResults(Bundle data) {
        Log.i(LOG_TAG, "onResults");
        textView.setText("onResult");
        ArrayList<String> result = data.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String inputString = result.get(0);
        if (inputString.equals("help")) {
            Toast.makeText(getApplicationContext(), inputString, Toast.LENGTH_SHORT).show();
            //Intent callIntent = new Intent(this, CallActivity.class);
            //startActivity(callIntent);
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "51345283"));
            //startActivity(callIntent);
        } else {
            try {
                inputString = URLEncoder.encode(inputString, "utf-8");
                String in = URLEncoder.encode(inputString, "ISO-8859-1");
                speech.cancel();
                listening = false;
                message = ("/?talk=" + inputString);
                Toast.makeText(getApplicationContext(), in, Toast.LENGTH_SHORT).show();
                api();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            /*speech.cancel();
            listening = false;
            message = ("/?talk=" + inputString);
            Toast.makeText(getApplicationContext(), inputString, Toast.LENGTH_SHORT).show();
            api();*/
        }
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    public void api() {
        queue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String ip = sharedPreferences.getString("ipKey", null);
        //String url = "http://192.168.0.111:5001" + message; //connect with webserver on Raspberry Pi 3
        //String url = "http://192.168.1.145:5001" + message; //connect with webserver on Mac
        String url = "http://192.168.0.103:5001" + message; //connect with webserver on Mac
        url = url.replace(" ", "_");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String result;
                try {
                    String[] strings = response.split(":");
                    if (strings[0].equals("youtube")) {
                        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + strings[1])); //Open a video which is random by ubuntu
                        //Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + "ERrMRbfdriA")); //Default to one youtube link
                        youtubeIntent.putExtra("force_fullscreen",true);
                        startActivity(youtubeIntent);
                    } else if (strings[0].equals("skype")) {
                        Uri uri = Uri.parse("skype:" + strings[1] + "?call&video=true");
                        Intent skypeIntent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(skypeIntent);
                    } else if (strings[0].equals("mirror")) {
                        Intent MirrorIntent = getPackageManager().getLaunchIntentForPackage("com.example.pc.myapplication3");
                        startActivity(MirrorIntent);
                    } else if (strings[0].equals("happy")) {
                        getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                        //MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.);
                        //mediaPlayer.start();
                        try {
                            result = new String(strings[1].getBytes("ISO-8859-1"));
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                            textToSpeech.speak(result, TextToSpeech.QUEUE_FLUSH, params);
                        } catch (UnsupportedEncodingException e2) {
                            e2.printStackTrace();
                        }
                    } else if (strings[0].equals("sad")) {
                        getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                        try {
                            result = new String(strings[1].getBytes("ISO-8859-1"));
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                            textToSpeech.speak(result, TextToSpeech.QUEUE_FLUSH, params);
                        } catch (UnsupportedEncodingException e3) {
                            e3.printStackTrace();
                        }
                    } else if (strings[0].equals("angry")){
                        getWindow().getDecorView().setBackgroundColor(Color.RED);
                        try {
                            result = new String(strings[1].getBytes("ISO-8859-1"));
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                            textToSpeech.speak(result, TextToSpeech.QUEUE_FLUSH, params);
                        } catch (UnsupportedEncodingException e4) {
                            e4.printStackTrace();
                        }
                    } else if (strings[0].equals("stress")) {
                        getWindow().getDecorView().setBackgroundColor(Color.GRAY);
                        try {
                            result = new String(strings[1].getBytes("ISO-8859-1"));
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                            textToSpeech.speak(result, TextToSpeech.QUEUE_FLUSH, params);
                        } catch (UnsupportedEncodingException e5) {
                            e5.printStackTrace();
                        }
                    } else {
                        try {
                            result = new String(response.getBytes("ISO-8859-1"));
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show(); //This is a pop up show the result of the response
                            textToSpeech.speak(result, TextToSpeech.QUEUE_FLUSH, params);
                            speech.cancel();
                        } catch (UnsupportedEncodingException e1) {
                            e1.printStackTrace();
                        }

                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    //Right corner option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //Setting page option and intent to the SettingsActivity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    //Process the TextToSpeech(TTS)
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                @Override
                public void onStart(String utteranceId) {
                    listening = false;
                }

                @Override
                public void onDone(String utteranceId) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            speech.startListening(recognizerIntent);
                        }
                    });
                }

                @Override
                public void onError(String utteranceId) {

                }
            });
        }
    }
}
