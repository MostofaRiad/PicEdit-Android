package com.allyouneedapp.palpicandroid.utils;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;

import java.util.ArrayList;

/**
 * Created by Soul on 11/27/2016.
 */

    class LanguageDetector {
        public static void init(ArrayList<String> profiles) throws LangDetectException {
            DetectorFactory.loadProfile(profiles);
        }
        public static String detect(String text) throws LangDetectException {
            Detector detector = DetectorFactory.create();
            detector.append(text);
            return detector.detect();
        }
        public static ArrayList<Language> detectLangs(String text) throws LangDetectException {
            Detector detector = DetectorFactory.create();
            detector.append(text);
            return detector.getProbabilities();
        }
    }
