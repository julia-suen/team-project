package app;

import entities.Coordinate;
import data_access.FireDataAccess;
import data_access.getData;

import java.util.List;

public class Main {

    public static void main(String[] args) throws getData.InvalidDataException {

        //delete later, for testing purposes
        String date = "2025-11-08";
        int days = 1;

        List<Coordinate> data = FireDataAccess.getFireData(days, date);

        System.out.println(data);

    }
}
