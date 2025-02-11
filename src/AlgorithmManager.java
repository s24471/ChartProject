import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AlgorithmManager {
    private static AlgorithmManager instance;

    private Map<String, Algorithm> algorithms;

    private AlgorithmManager() {
        algorithms = new HashMap<>();

        AlligatorAlgo alligatorAlgo = new AlligatorAlgo();

        TemaAlgo temaAlgo = new TemaAlgo();

        ParabolicSarAlgo parabolicSarAlgo = new ParabolicSarAlgo();

        TdMovingAverageAlgo tdMovingAverageAlgo = new TdMovingAverageAlgo();

        KeltnerChannelAlgo keltnerChannelAlgo = new KeltnerChannelAlgo();

        algorithms.put(alligatorAlgo.getName(), alligatorAlgo);

        algorithms.put(temaAlgo.getName(), temaAlgo);

        algorithms.put(parabolicSarAlgo.getName(), parabolicSarAlgo);

        algorithms.put(tdMovingAverageAlgo.getName(),tdMovingAverageAlgo);

        algorithms.put(keltnerChannelAlgo.getName(),keltnerChannelAlgo);

    }

    public static AlgorithmManager getInstance() {
        if (instance == null) {
            instance = new AlgorithmManager();
        }
        return instance;
    }

    public Collection<Algorithm> getAllAlgorithms() {
        return algorithms.values();
    }
}
