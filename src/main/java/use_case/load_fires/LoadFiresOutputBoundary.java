package use_case.load_fires;

public interface LoadFiresOutputBoundary {
    void prepareSuccessView(LoadFiresOutputData outputData);
    void prepareFailView(String errorMessage);
}
