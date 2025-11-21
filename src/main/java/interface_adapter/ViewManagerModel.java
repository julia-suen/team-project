package interface_adapter;

/**
 * Model for the view manager. The state is the name of the currently active view.
 * An initial state of "" is used.
 */

public class ViewManagerModel extends ViewModel<String>{
    public ViewManagerModel() {
        super("view manager");
    }

}
