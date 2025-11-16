package app;

import fireapi.dataAccess;
import fireapi.getData;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws getData.InvalidDataException {

        //delete later, for testing purposes
        String date = "2025-11-08";
        int days = 1;

        HashMap<float[], Object[]> data = dataAccess.getFireData(days, date);

        System.out.println(data);

    }
}
