package pt.hmsk.logic;

public class ProgramLogic {
    
    private State state = State.LOGGED_OUT;

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }
}
