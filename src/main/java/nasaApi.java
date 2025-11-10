//package nasaApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;

public class nasaApi {


    /**
     * BreedFetcher implementation that relies on the dog.ceo API.
     * Note that all failures get reported as BreedNotFoundException
     * exceptions to align with the requirements of the BreedFetcher interface.
     */

    private static final String MESSAGE = "message";
    private static final String STATUS = "status";
    private static final String SUCCESS = "success";

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     *
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws lol if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws DataNotFoundException {
        final OkHttpClient client = new OkHttpClient();
        final String request_url = "https://dog.ceo/api/breed/" + breed + "/list";
        final Request request = new Request.Builder().url(request_url).build();

        try {
            Response response = client.newCall(request).execute();
            JSONObject responseBody = new JSONObject(response.body().string());

            if (Objects.equals(responseBody.getString(STATUS), SUCCESS)) {
                final JSONArray subBreeds = responseBody.getJSONArray(MESSAGE);
                List<String> subBreedList = new ArrayList<>();

                for (int i = 0; i < subBreeds.length(); i++) {
                    subBreedList.add(subBreeds.getString(i));
                }
                return subBreedList;
            } else {
                throw new BreedNotFoundException(breed);
            }

        } catch (Exception e) {
            throw new BreedNotFoundException(breed);

        }

    }
}