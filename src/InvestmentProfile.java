import java.util.Map;

public class InvestmentProfile {
    private String profileName;
    private Map<String, Integer> sectorHoldings;

    public InvestmentProfile(String profileName, Map<String, Integer> sectorHoldings) {
        this.profileName = profileName;
        this.sectorHoldings = sectorHoldings;
    }

    // Getters and Setters
}
