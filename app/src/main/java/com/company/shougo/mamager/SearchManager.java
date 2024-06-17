package com.company.shougo.mamager;

import android.content.Context;

import com.company.shougo.Parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchManager {

    public static void addSearchLocation(Context context,String word){
        List<String> list = getSearchLocation(context);

        for (int i=0;i<list.size();i++){
            if (list.get(i).equals(word)){
                list.remove(list.get(i));
            }
        }

        if (list.size()> Parameter.SEARCH_SIZE){
            list.remove(list.get(0));
        }

        list.add(word);

        String s = "";

        for (int i=0;i<list.size();i++){
            s = s + list.get(i) + ",";
        }

        s = s.substring(0, s.length()-1);

        SaveManager.saveSearchLocation(context, s);

    }

    public static void removeSearchLocation(Context context, String word){
        List<String> list = getSearchLocation(context);

        list.remove(word);

        String s = "";

        for (int i=0;i<list.size();i++){
            s = s + list.get(i) + ",";
        }

        if (s.length()>0) {
            s = s.substring(0, s.length() - 1);
        }

        SaveManager.saveSearchLocation(context, s);
    }

    public static List<String> getSearchLocation(Context context){

        List<String> list = new ArrayList<>();

        String ss = SaveManager.getSearchLocation(context);

        if (ss!=null && ss.length()>0){
            String[] words = ss.split(",");

            list.addAll(Arrays.asList(words));
        }

        return list;

    }

    public static void addSearch(Context context,String word){
        List<String> list = getSearch(context);

        for (int i=0;i<list.size();i++){
            if (list.get(i).equals(word)){
                list.remove(list.get(i));
            }
        }

        if (list.size()> Parameter.SEARCH_SIZE){
            list.remove(list.get(0));
        }

        list.add(word);

        String s = "";

        for (int i=0;i<list.size();i++){
            s = s + list.get(i) + ",";
        }

        s = s.substring(0, s.length()-1);

        SaveManager.saveSearch(context, s);

    }

    public static void removeSearch(Context context, String word){
        List<String> list = getSearch(context);

        list.remove(word);

        String s = "";

        for (int i=0;i<list.size();i++){
            s = s + list.get(i) + ",";
        }

        if (s.length()>0) {
            s = s.substring(0, s.length() - 1);
        }

        SaveManager.saveSearch(context, s);
    }

    public static List<String> getSearch(Context context){

        List<String> list = new ArrayList<>();

        String ss = SaveManager.getSearch(context);

        if (ss!=null && ss.length()>0){
            String[] words = ss.split(",");

            list.addAll(Arrays.asList(words));
        }

        return list;

    }

}
