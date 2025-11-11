package fireapi;

import java.util.HashMap;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class Main {

    public static void main(String[] args) {

        //delete later, for testing purposes
        String date = "2025-11-08";
        int days = 1;

        HashMap<float[], Object[]> data = dataAccess.getFireData(days, date);

        System.out.println(data);

    }
}
