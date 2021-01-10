package sample.Algorithm;

import javafx.collections.ObservableList;
import sample.Entity.Medicine;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmWrapper {

    public List<Position> calculate(ObservableList<Medicine> medicines, int startPositionX, int startPositionY) {
        List<Position> positions = new ArrayList<>();

        positions.add(new Position(startPositionX, startPositionY));

        for (Medicine medicine : medicines) {
            positions.add(new Position(medicine.getPosX(), medicine.getPosY()));
        }

        int[] order = AlgorithmTSP.runAlgorithm(positions);
        List<Position> sortedPositions = new ArrayList<>();

        for (int position : order) {
            sortedPositions.add(positions.get(position));
        }

        return sortedPositions;
    }

}
