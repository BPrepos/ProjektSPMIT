package sample.Algorithm;

import javafx.collections.ObservableList;
import sample.Entity.Medicine;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmWrapper {

    public int[] calculate(ObservableList<Medicine> medicines, int startPositionX, int startPositionY) {
        List<Position> positions = new ArrayList<>();

        positions.add(new Position(startPositionX, startPositionY));

        for (Medicine medicine : medicines) {
            positions.add(new Position(medicine.getPosX(), medicine.getPosY()));
        }

        return AlgorithmTSP.runAlgorithm(positions);
    }

}
