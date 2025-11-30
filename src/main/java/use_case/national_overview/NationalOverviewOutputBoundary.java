package use_case.national_overview;

public interface NationalOverviewOutputBoundary {
    void prepareSuccessView(NationalOverviewOutputData outputData);
    void prepareFailView(String errorMessage);
}
