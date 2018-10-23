package com.test.english.record;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

/**
 * Created by sonseongbin on 2017. 3. 19..
 */

public class CustomSTT implements RecognitionListener{

    public interface CallListener {
        void speechStatus(String status);
        void speechResult(String speak);
    }

    private final String TAG = CustomSTT.class.getSimpleName();

    private Activity activity;
    private Intent intentSpeech;
    private SpeechRecognizer speechRecognizer;
    CallListener callListener;

    public CustomSTT(Activity activity, CallListener callListener, String language) {
        this.activity = activity;
        this.callListener = callListener;
        init(language);
    }

    public void init(String language) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity);
        speechRecognizer.setRecognitionListener(this);
        intentSpeech = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentSpeech.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intentSpeech.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
    }

    public void startCustomSTT() {
        if(speechRecognizer != null && intentSpeech != null) {
            speechRecognizer.startListening(intentSpeech);
        }
    }

    public void stopCustomSTT() {
        if(speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        callListener.speechStatus("onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        callListener.speechStatus("onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        callListener.speechStatus("onRmsChanged");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        callListener.speechStatus("onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        stopCustomSTT();
        callListener.speechStatus("onEndOfSpeech");
    }

    @Override
    public void onError(int error) {
        callListener.speechStatus("onError");
    }

    @Override
    public void onResults(Bundle results) {
        callListener.speechStatus("onResults");
        callListener.speechResult(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        callListener.speechStatus("onPartialResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        callListener.speechStatus("onEvent");
    }
}
