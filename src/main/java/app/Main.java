package app;

import entities.Coordinate;
import fireapi.DataAccess;
import fireapi.GetData;

import java.util.List;

public class Main {

    public static void main(String[] args) throws GetData.InvalidDataException {

        //delete later, for testing purposes
        String date = "2025-11-08";
        int days = 1;

        List<Coordinate> data = DataAccess.getFireData(days, date);

        System.out.println(data);

    }
}
