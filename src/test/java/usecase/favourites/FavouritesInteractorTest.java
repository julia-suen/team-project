package usecase.favourites;

import data_access.FavouritesDataAccess;
import entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;


public class FavouritesInteractorTest {
    private FavouritesOutputBoundary mockPresenter;
    private FavouritesDataAccess mockDao;
    private FavouritesInteractor favouritesInteractor;
    private User testUser;

    @BeforeEach
    void setup() {
        mockPresenter = mock(FavouritesOutputBoundary.class);
        mockDao = mock(FavouritesDataAccess.class);
        favouritesInteractor =  new FavouritesInteractor(mockPresenter, mockDao);
        testUser = new User("testuser");
    }

    // setCurrentUser tests
    @Test
    void testSetCurrentUserWithNull() {
        favouritesInteractor.setCurrentUser(testUser);
        reset(mockPresenter);

        favouritesInteractor.setCurrentUser(null);

        ArgumentCaptor<FavouritesOutputData> captor = ArgumentCaptor.forClass(FavouritesOutputData.class);
        verify(mockPresenter).prepareSuccessView(captor.capture());
        assertTrue(captor.getValue().favourites().isEmpty());
    }

    // execute - handleAdd
    @Test
    void testExecute_addFavouriteSuccess() throws IOException {

        favouritesInteractor.setCurrentUser(testUser);
        reset(mockPresenter);
        FavouritesInputData inputData = new FavouritesInputData("Alberta", FavouritesAction.ADD);

        favouritesInteractor.execute(inputData);

        verify(mockDao).saveFavourites(eq("testuser"), anyList());
        ArgumentCaptor<FavouritesOutputData> captor = ArgumentCaptor.forClass(FavouritesOutputData.class);
        verify(mockPresenter).prepareSuccessView(captor.capture());
        assertTrue(captor.getValue().favourites().contains("Alberta"));
    }

    @Test
    void testExecute_addFavourite_duplicateProvince() throws IOException {
        favouritesInteractor.setCurrentUser(testUser);
        reset(mockPresenter);

        FavouritesInputData inputData1 = new FavouritesInputData("New Brunswick", FavouritesAction.ADD);
        favouritesInteractor.execute(inputData1);
        reset(mockPresenter);

        FavouritesInputData inputData2 = new FavouritesInputData("New Brunswick", FavouritesAction.ADD);

        favouritesInteractor.execute(inputData2);

        verify(mockPresenter).prepareFailureView("Province already added!");
    }

    @Test
    void testExecute_clearFavouritesUserLoggedOut() throws IOException {
        FavouritesInputData inputData = new FavouritesInputData(null, FavouritesAction.CLEAR);

        favouritesInteractor.execute(inputData);

        ArgumentCaptor<FavouritesOutputData> captor = ArgumentCaptor.forClass(FavouritesOutputData.class);
        verify(mockPresenter).prepareSuccessView(captor.capture());
        assertTrue(captor.getValue().favourites().isEmpty());
        verify(mockDao, never()).saveFavourites(anyString(), anyList());
    }


    // execute - handleGet
    @Test
    void testExecute_getFavouritesEmptyList() {
        FavouritesInputData inputData = new FavouritesInputData(null, FavouritesAction.GET);

        favouritesInteractor.execute(inputData);

        ArgumentCaptor<FavouritesOutputData> captor = ArgumentCaptor.forClass(FavouritesOutputData.class);
        verify(mockPresenter).prepareSuccessView(captor.capture());
        assertTrue(captor.getValue().favourites().isEmpty());
    }

    // saveFavouritesToFile
    @Test
    void testSaveFavouritesToFileNullUser() throws IOException {

        favouritesInteractor.execute(new FavouritesInputData(null, FavouritesAction.CLEAR));

        verify(mockDao, never()).saveFavourites(anyString(), anyList());
    }

    // loadFavouritesFromFile
    @Test
    void testLoadFromFavouritesEmptyUser() throws IOException {
        favouritesInteractor.setCurrentUser(null);
        verify(mockDao, never()).loadFromFavourites(anyString());
    }
}
