package com.edxdn.hmsoon.ui.record;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import com.edxdn.hmsoon.databinding.ActivityEvalutionBinding;
import java.util.Locale;

/**
 * Created by sonseongbin on 2017. 3. 19..
 */

public class CustomSTT implements RecognitionListener {

    private final String TAG = CustomSTT.class.getSimpleName();
    private Activity activity;
    private Intent intentSpeech;
    private SpeechRecognizer speechRecognizer;
    private ActivityEvalutionBinding binding;

    public CustomSTT(Activity activity, ActivityEvalutionBinding binding, Locale language) {
        this.activity = activity;
        this.binding = binding;
        init(language);
    }

    public void init(Locale language) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity);
        speechRecognizer.setRecognitionListener(this);
        intentSpeech = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentSpeech.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intentSpeech.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
    }

    public void startCustomSTT() {
        if (speechRecognizer != null && intentSpeech != null) {
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
        binding.textViewSpeechStatus.setText("onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        binding.textViewSpeechStatus.setText("onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        //binding.textViewSpeechStatus.setText("onRmsChanged");
        binding.textViewSpeechStatus.setText("문장을 말해주세요");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        binding.textViewSpeechStatus.setText("onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        stopCustomSTT();
        //binding.textViewSpeechStatus.setText("onEndOfSpeech");
    }

    @Override
    public void onError(int error) {
        //binding.textViewSpeechStatus.setText("onError");
        binding.textViewSpeechStatus.setText("조금만 더 크고, 정확하게 말해주세요");
    }

    @Override
    public void onResults(Bundle results) {
        binding.textViewSpeechResult.setText(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        binding.textViewSpeechStatus.setText("onPartialResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        binding.textViewSpeechStatus.setText("onEvent");
    }
}